package com.fullstack.model;

import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS_INFM")
public class UserInfm implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // Auto-generated ID as primary key

    @Column(name = "biz_key", nullable = false, unique = true, length = 10)
    private String bizKey;        // Business Key (will be set manually)

    private String userId;        // User ID for login
    private String userNm;        // User Name
    private String userPwd;       // Password
    private String tel;           // Telephone
    private String eml;           // Email
    private String usrImg;        // User Image
    private String lockYn;        // Lock Yes/No (Y/N)
    private Integer loginFailedCnt; // Login Failed Count
    private String actYn;         // Active Yes/No (Y/N)
    private String regId;         // Register ID
    private LocalDateTime lstLgnDtm; // Last Login DateTime
    private LocalDateTime regDtm;     // Register DateTime
    private String modId;         // Modify ID
    private LocalDateTime modDtm;     // Modify DateTime

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userId;
    }
    
    @Override
    public String getPassword() {
        return userPwd;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"Y".equals(lockYn);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "Y".equals(actYn);
    }
    
    // Helper methods for login lockout functionality
    public void incrementFailedLoginAttempts() {
        this.loginFailedCnt = (this.loginFailedCnt == null) ? 1 : this.loginFailedCnt + 1;
        if (this.loginFailedCnt >= 5) {
            this.lockYn = "Y";
        }
    }
    
    public void resetFailedLoginAttempts() {
        this.loginFailedCnt = 0;
        this.lockYn = "N";
    }
    
    public boolean isLocked() {
        return "Y".equals(this.lockYn);
    }
} 