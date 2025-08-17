package com.fullstack.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.dto.ApiResponse;
import com.fullstack.exception.RecordNotFoundException;
import com.fullstack.model.UserInfm;
import com.fullstack.repository.UserRepository;
import com.fullstack.util.DateTimeUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing user data")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // @PostMapping("/create")
    // @Operation(summary = "Create a new user", description = "Creates a new user record in the database")
    // public ResponseEntity<ApiResponse<UserInfm>> createUser(@RequestBody UserCreateRequest request) {
    //     try {
    //         // Check if userId already exists
    //         if (userRepository.findByUserId(request.getUserId()).isPresent()) {
    //             return ResponseEntity.badRequest()
    //                     .body(new ApiResponse<>(400, "User ID already exists", null));
    //         }
    //         // Check if email already exists
    //         if (userRepository.findByEml(request.getEml()).isPresent()) {
    //             return ResponseEntity.badRequest()
    //                     .body(new ApiResponse<>(400, "Email already exists", null));
    //         }
    //         LocalDateTime now = LocalDateTime.now();
    //         var user = UserInfm.builder()
    //                 .userId(request.getUserId())
    //                 .userNm(request.getUserNm())
    //                 .userPwd(passwordEncoder.encode(request.getUserPwd()))
    //                 .eml(request.getEml())
    //                 .tel(request.getTel())
    //                 .usrImg(request.getUsrImg() != null ? request.getUsrImg() : "")
    //                 .role(request.getRole() != null ? request.getRole() : com.fullstack.model.Role.USER)
    //                 .lockYn("N")
    //                 .loginFailedCnt(0)
    //                 .actYn("Y")
    //                 .regId(request.getRegId() != null ? request.getRegId() : request.getUserId())
    //                 .regDtm(DateTimeUtil.formatDefault(now))
    //                 .modId(request.getModId() != null ? request.getModId() : request.getUserId())
    //                 .modDtm(DateTimeUtil.formatDefault(now))
    //                 .build();
    //         UserInfm savedUser = userRepository.save(user);
    //         return ResponseEntity.ok(new ApiResponse<>(200, "User created successfully", savedUser));
    //     } catch (Exception e) {
    //         log.error("Error creating user: ", e);
    //         return ResponseEntity.status(500)
    //                 .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
    //     }
    // }

    // @GetMapping("/findbyid/{id}")
    // @Operation(summary = "Find user by ID", description = "Retrieves a user by their ID")
    // public ResponseEntity<ApiResponse<UserInfm>> findById(@PathVariable Long id) {
    //     try {
    //         Optional<UserInfm> user = userRepository.findById(id);
    //         if (user.isPresent()) {
    //             return ResponseEntity.ok(new ApiResponse<>(200, "User found", user.get()));
    //         } else {
    //             return ResponseEntity.status(404)
    //                     .body(new ApiResponse<>(404, "User not found with ID: " + id, null));
    //         }
    //     } catch (Exception e) {
    //         log.error("Error finding user: ", e);
    //         return ResponseEntity.status(500)
    //                 .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
    //     }
    // }
    
    // @GetMapping("/findbyuserid/{userId}")
    // @Operation(summary = "Find user by userId", description = "Retrieves a user by their userId")
    // public ResponseEntity<ApiResponse<UserInfm>> findByUserId(@PathVariable String userId) {
    //     try {
    //         Optional<UserInfm> user = userRepository.findByUserId(userId);
    //         if (user.isPresent()) {
    //             return ResponseEntity.ok(new ApiResponse<>(200, "User found", user.get()));
    //         } else {
    //             return ResponseEntity.status(404)
    //                     .body(new ApiResponse<>(404, "User not found with userId: " + userId, null));
    //         }
    //     } catch (Exception e) {
    //         log.error("Error finding user: ", e);
    //         return ResponseEntity.status(500)
    //                 .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
    //     }
    // }

    // @GetMapping("/findall")
    // @Operation(summary = "Get all users", description = "Retrieves all users from the database")
    // public ResponseEntity<ApiResponse<List<UserInfm>>> findAll() {
    //     try {
    //         List<UserInfm> users = userRepository.findAll();
    //         return ResponseEntity.ok(new ApiResponse<>(200, "Users retrieved successfully", users));
    //     } catch (Exception e) {
    //         log.error("Error retrieving users: ", e);
    //         return ResponseEntity.status(500)
    //                 .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
    //     }
    // }

    // @PutMapping("/update/{id}")
    // @Operation(summary = "Update user", description = "Updates an existing user record")
    // public ResponseEntity<ApiResponse<UserInfm>> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
    //     try {
    //         UserInfm existingUser = userRepository.findById(id)
    //                 .orElseThrow(() -> new RecordNotFoundException("User with ID " + id + " does not exist"));
    //         // Check if new userId conflicts with existing user (if userId is being changed)
    //         if (request.getUserId() != null && !request.getUserId().equals(existingUser.getUserId())) {
    //             if (userRepository.findByUserId(request.getUserId()).isPresent()) {
    //                 return ResponseEntity.badRequest()
    //                         .body(new ApiResponse<>(400, "User ID already exists", null));
    //             }
    //         }
    //         // Check if new email conflicts with existing user (if email is being changed)
    //         if (request.getEml() != null && !request.getEml().equals(existingUser.getEml())) {
    //             if (userRepository.findByEml(request.getEml()).isPresent()) {
    //                 return ResponseEntity.badRequest()
    //                         .body(new ApiResponse<>(400, "Email already exists", null));
    //             }
    //         }
    //         // Update fields
    //         if (request.getUserId() != null) existingUser.setUserId(request.getUserId());
    //         if (request.getUserNm() != null) existingUser.setUserNm(request.getUserNm());
    //         if (request.getUserPwd() != null) existingUser.setUserPwd(passwordEncoder.encode(request.getUserPwd()));
    //         if (request.getEml() != null) existingUser.setEml(request.getEml());
    //         if (request.getTel() != null) existingUser.setTel(request.getTel());
    //         if (request.getUsrImg() != null) existingUser.setUsrImg(request.getUsrImg());
    //         if (request.getRole() != null) existingUser.setRole(request.getRole());
    //         if (request.getLockYn() != null) existingUser.setLockYn(request.getLockYn());
    //         if (request.getActYn() != null) existingUser.setActYn(request.getActYn());
    //         // Update modification info
    //         existingUser.setModId(request.getModId() != null ? request.getModId() : existingUser.getUserId());
    //         existingUser.setModDtm(DateTimeUtil.formatDefault(LocalDateTime.now()));
    //         UserInfm updatedUser = userRepository.save(existingUser);
    //         return ResponseEntity.ok(new ApiResponse<>(200, "User updated successfully", updatedUser));
    //     } catch (RecordNotFoundException e) {
    //         return ResponseEntity.status(404)
    //                 .body(new ApiResponse<>(404, e.getMessage(), null));
    //     } catch (Exception e) {
    //         log.error("Error updating user: ", e);
    //         return ResponseEntity.status(500)
    //                 .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
    //     }
    // }

    // @DeleteMapping("/delete/{id}")
    // @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    // public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable Long id) {
    //     try {
    //         if (!userRepository.existsById(id)) {
    //             return ResponseEntity.status(404)
    //                     .body(new ApiResponse<>(404, "User not found with ID: " + id, null));
    //         }
    //         userRepository.deleteById(id);
    //         return ResponseEntity.ok(new ApiResponse<>(200, "User deleted successfully", null));
    //     } catch (Exception e) {
    //         log.error("Error deleting user: ", e);
    //         return ResponseEntity.status(500)
    //                 .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
    //     }
    // }
    
}

// Request DTOs
class UserCreateRequest {
    private String userId;
    private String userNm;
    private String userPwd;
    private String eml;
    private String tel;
    private String usrImg;
    private com.fullstack.model.Role role;
    private String regId;
    private String modId;
    
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
    
    public String getUsrImg() { return usrImg; }
    public void setUsrImg(String usrImg) { this.usrImg = usrImg; }
    
    public com.fullstack.model.Role getRole() { return role; }
    public void setRole(com.fullstack.model.Role role) { this.role = role; }
    
    public String getRegId() { return regId; }
    public void setRegId(String regId) { this.regId = regId; }
    
    public String getModId() { return modId; }
    public void setModId(String modId) { this.modId = modId; }
}

class UserUpdateRequest {
    private String userId;
    private String userNm;
    private String userPwd;
    private String eml;
    private String tel;
    private String usrImg;
    private com.fullstack.model.Role role;
    private String lockYn;
    private String actYn;
    private String modId;
    
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
    
    public String getUsrImg() { return usrImg; }
    public void setUsrImg(String usrImg) { this.usrImg = usrImg; }
    
    public com.fullstack.model.Role getRole() { return role; }
    public void setRole(com.fullstack.model.Role role) { this.role = role; }
    
    public String getLockYn() { return lockYn; }
    public void setLockYn(String lockYn) { this.lockYn = lockYn; }
    
    public String getActYn() { return actYn; }
    public void setActYn(String actYn) { this.actYn = actYn; }
    
    public String getModId() { return modId; }
    public void setModId(String modId) { this.modId = modId; }
}
