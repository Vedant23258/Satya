package com.satya.portal.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User model class representing system users
 */
public class User {
    public enum Role {
        ADMIN("admin"),
        USER("user"),
        VIEWER("viewer");
        
        private final String value;
        
        Role(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Role fromString(String value) {
            for (Role role : Role.values()) {
                if (role.value.equalsIgnoreCase(value)) {
                    return role;
                }
            }
            return USER; // default
        }
    }
    
    private String userId;
    private String username;
    private String password;
    private Role role;
    private String fullName;
    private String email;
    private String department;
    private boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdDate;
    
    // Default constructor
    public User() {
        this.isActive = true;
        this.createdDate = LocalDateTime.now();
        this.role = Role.USER;
    }
    
    // Constructor with basic info
    public User(String userId, String username, String password, Role role) {
        this();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Full constructor
    public User(String userId, String username, String password, Role role, 
                String fullName, String email, String department) {
        this(userId, username, password, role);
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    // Utility methods
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    public boolean canViewDocuments() {
        return role == Role.ADMIN || role == Role.USER;
    }
    
    public boolean canModifyLayouts() {
        return role == Role.ADMIN;
    }
    
    public boolean canAccessAdminPanel() {
        return role == Role.ADMIN;
    }
    
    // Authentication method
    public boolean authenticate(String inputPassword) {
        return Objects.equals(this.password, inputPassword);
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                ", lastLogin=" + lastLogin +
                '}';
    }
}