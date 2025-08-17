package com.fullstack.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.dto.ApiResponse;
import com.fullstack.model.Role;
import com.fullstack.model.UserInfm;
import com.fullstack.repository.UserRepository;
import com.fullstack.security.JwtService;
import com.fullstack.util.DateTimeUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with userId, userNm, userPwd, eml, and tel")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        System.out.println("Registration request received: " + request.getUserId());
        
        try {
            // Check if userId already exists
            if (userRepository.findByUserId(request.getUserId()).isPresent()) {
                System.out.println("User ID already exists: " + request.getUserId());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(400, "User ID already exists", null));
            }
            // Check if email already exists
            if (userRepository.findByEml(request.getEml()).isPresent()) {
                System.out.println("Email already exists: " + request.getEml());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(400, "Email already exists", null));
            }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Generate bizKey
        String bizKey = generateBizKey();
        
        var user = UserInfm.builder()
                .bizKey(bizKey)
                .userId(request.getUserId())
                .userNm(request.getUserNm())
                .userPwd(passwordEncoder.encode(request.getUserPwd()))
                .eml(request.getEml())
                .tel(request.getTel())
                .usrImg("") // Set empty string for user image
                .role(Role.USER)
                .lockYn("N")
                .loginFailedCnt(0)
                .actYn("Y")
                .regId(request.getUserId())
                .regDtm(DateTimeUtil.formatDefault(now))
                .modId(request.getUserId())
                .modDtm(DateTimeUtil.formatDefault(now))
                .build();
        userRepository.save(user);
        jwtService.generateToken(user);
        System.out.println("User registered successfully: " + request.getUserId());
        return ResponseEntity.ok(new ApiResponse<>(200, "User registered successfully",null));
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
        }
    }
    
    // Helper method to generate bizKey
    private String generateBizKey() {
        try {
            // Get all users to find the maximum bizKey
            var allUsers = userRepository.findAll();
            if (allUsers.isEmpty()) {
                return "00001";
            }
            
            // Find the maximum bizKey value
            long maxValue = allUsers.stream()
                    .mapToLong(user -> {
                        try {
                            return Long.parseLong(user.getBizKey());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0);
            
            return String.format("%05d", maxValue + 1);
        } catch (Exception e) {
            System.err.println("Error generating bizKey: " + e.getMessage());
            return "00001";
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user with userId and userPwd")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody AuthRequest request) {
        try {
            // First check if user exists
            var userOptional = userRepository.findByUserId(request.getUserId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(401).body(
                        new ApiResponse<>(401, "The User is not found!", null)
                );
            }
            
            var user = userOptional.get();
            
            // Check if account is locked
            if (user.isLocked()) {
                return ResponseEntity.status(423).body(
                        new ApiResponse<>(423, "Account is locked due to too many failed login attempts. Please try again later.", null)
                );
            }
            
            // Try to authenticate
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserId(),
                            request.getUserPwd()
                    )
            );
            
            // Successful login: reset attempts and update last login time
            user.resetFailedLoginAttempts();
            user.setLstLgnDtm(DateTimeUtil.formatDefault(LocalDateTime.now()));
            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return ResponseEntity.ok(new ApiResponse<>(200, "Login successful",
                    AuthResponse.builder().token(jwtToken).message("Login successful").build()));
                    
        } catch (BadCredentialsException ex) {
            // Handle incorrect password
            userRepository.findByUserId(request.getUserId()).ifPresent(user -> {
                user.incrementFailedLoginAttempts();
                userRepository.save(user);
            });
            return ResponseEntity.status(401).body(
                    new ApiResponse<>(401, "Incorrect userId or password", null)
            );
        } catch (UsernameNotFoundException ex) {
            // Handle user not found
            return ResponseEntity.status(401).body(
                    new ApiResponse<>(401, "Incorrect userId or password", null)
            );
        } catch (AuthenticationException ex) {
            // Handle other authentication errors
            return ResponseEntity.status(401).body(
                    new ApiResponse<>(401, "Authentication failed", null)
            );
        } catch (Exception ex) {
            // Log the exception for debugging
            System.err.println("Login error: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(500).body(
                    new ApiResponse<>(500, "Internal server error", null)
            );
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the current user")
    public ResponseEntity<ApiResponse<AuthResponse>> logout() {
        return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful",
                AuthResponse.builder().message("Logout successful").build()));
    }
}

// Request/Response DTOs
class RegisterRequest {
    private String userId;
    private String userNm;
    private String userPwd;
    private String eml;
    private String tel;
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserNm() { return userNm; }
    public void setUserNm(String userNm) { this.userNm = userNm; }
    public String getUserPwd() { return userPwd; }
    public void setUserPwd(String userPwd) { this.userPwd = userPwd; }
    public String getEml() { return eml; }
    public void setEml(String eml) { this.eml = eml; }
    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
}

class AuthRequest {
    private String userId;
    private String userPwd;
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserPwd() { return userPwd; }
    public void setUserPwd(String userPwd) { this.userPwd = userPwd; }
}

class AuthResponse {
    private String token;
    private String message;
    
    public AuthResponse() {}
    
    public AuthResponse(String token) {
        this.token = token;
    }
    
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }
    
    public static class AuthResponseBuilder {
        private String token;
        private String message;
        
        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }
        
        public AuthResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public AuthResponse build() {
            return new AuthResponse(token, message);
        }
    }
} 