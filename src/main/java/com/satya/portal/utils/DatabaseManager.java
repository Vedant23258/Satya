package com.satya.portal.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.satya.portal.DBConnection;
import com.satya.portal.models.CourtCase;
import com.satya.portal.models.Layout;
import com.satya.portal.models.User;

/**
 * DatabaseManager - Singleton class for managing database operations
 * Handles all CRUD operations for the SATYA Portal application
 */
public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        // Private constructor to prevent instantiation
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Authenticate user by username and password
     * 
     * @param username The username
     * @param password The password
     * @param role The expected role
     * @return User object if authentication is successful, null otherwise
     */
    public User authenticateUser(String username, String password, User.Role role) {
        // Debug logging
        LOGGER.info("Attempting to authenticate user: " + username + " with role: " + role.getValue());
        
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, username);
            statement.setString(2, password);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    // Use "id" instead of "user_id" to match your table structure
                    user.setUserId(String.valueOf(resultSet.getInt("id")));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(User.Role.fromString(resultSet.getString("role")));
                    
                    // Log the actual role from database
                    LOGGER.info("User found with role: " + user.getRole().getValue());
                    LOGGER.info("Expected role: " + role.getValue());
                    
                    // Check if the role matches (case-insensitive comparison)
                    if (user.getRole().getValue().equalsIgnoreCase(role.getValue())) {
                        // Set default values for missing columns
                        user.setFullName("Default User");
                        user.setEmail("user@example.com");
                        user.setDepartment("Default Department");
                        user.setActive(true);
                        user.setLastLogin(LocalDateTime.now());
                        user.setCreatedDate(LocalDateTime.now());
                        
                        LOGGER.info("Authentication successful for user: " + username);
                        return user;
                    } else {
                        LOGGER.info("Role mismatch. Expected: " + role.getValue() + ", Actual: " + user.getRole().getValue());
                    }
                } else {
                    LOGGER.info("No user found with username: " + username + " and provided password");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user", e);
        }
        
        return null;
    }
    
    /**
     * Register a new user
     * 
     * @param user The user to register
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(User user) {
        // Simplified insert statement to match your table structure
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().getValue().toUpperCase());
            // Note: We're not inserting the other fields since they don't exist in your table
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user", e);
            return false;
        }
    }
    
    /**
     * Update user's last login time
     * 
     * @param userId The user ID
     */
    public void updateUserLastLogin(String userId) {
        // Comment out this method since the last_login column doesn't exist in your table
        /*
        String sql = "UPDATE users SET last_login = ? WHERE user_id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(2, userId);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user last login", e);
        }
        */
    }
    
    /**
     * Get all users
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                User user = new User();
                // Use "id" instead of "user_id" to match your table structure
                user.setUserId(String.valueOf(resultSet.getInt("id")));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(User.Role.fromString(resultSet.getString("role")));
                
                // Log user info for debugging
                LOGGER.info("Loaded user from DB: " + user.getUsername() + ", Role: " + user.getRole().getValue());
                
                // Set default values for missing columns
                user.setFullName("Default User");
                user.setEmail("user@example.com");
                user.setDepartment("Default Department");
                user.setActive(true);
                user.setLastLogin(LocalDateTime.now());
                user.setCreatedDate(LocalDateTime.now());
                
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching users", e);
        }
        
        return users;
    }
    
    /**
     * Search layouts based on criteria
     * 
     * @param query Search query
     * @param status Status filter
     * @param owner Owner filter
     * @return List of matching layouts
     */
    public List<Layout> searchLayouts(String query, String status, String owner) {
        List<Layout> layouts = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT file_no, layout_name, status, owner, survey_number, area, application_date, latitude, longitude, remarks, total_plots FROM layouts WHERE 1=1");
        
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (layout_name LIKE ? OR file_no LIKE ? OR survey_number LIKE ?)");
        }
        
        if (status != null && !status.equals("All")) {
            sql.append(" AND status = ?");
        }
        
        if (owner != null && !owner.trim().isEmpty()) {
            sql.append(" AND owner LIKE ?");
        }
        
        sql.append(" ORDER BY application_date DESC");
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (query != null && !query.trim().isEmpty()) {
                String searchQuery = "%" + query.trim() + "%";
                statement.setString(paramIndex++, searchQuery);
                statement.setString(paramIndex++, searchQuery);
                statement.setString(paramIndex++, searchQuery);
            }
            
            if (status != null && !status.equals("All")) {
                statement.setString(paramIndex++, status);
            }
            
            if (owner != null && !owner.trim().isEmpty()) {
                statement.setString(paramIndex++, "%" + owner.trim() + "%");
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Layout layout = new Layout();
                    layout.setFileNumber(resultSet.getString("file_no"));
                    layout.setLayoutName(resultSet.getString("layout_name"));
                    layout.setStatus(resultSet.getString("status"));
                    layout.setOwnerName(resultSet.getString("owner"));
                    layout.setSurveyNumber(resultSet.getString("survey_number"));
                    layout.setAreaInAcres(resultSet.getDouble("area"));
                    
                    java.sql.Date applicationDate = resultSet.getDate("application_date");
                    if (applicationDate != null) {
                        layout.setApplicationDate(applicationDate.toLocalDate());
                    }
                    
                    layout.setLatitude(resultSet.getDouble("latitude"));
                    layout.setLongitude(resultSet.getDouble("longitude"));
                    layout.setRemarks(resultSet.getString("remarks"));
                    layout.setTotalPlots(resultSet.getInt("total_plots"));
                    
                    // Set default values for missing columns
                    layout.setApprovalDate(null);
                    layout.setApprovedBy("Unknown");
                    layout.setDocumentPath("");
                    layout.setHasCourtCase(false);
                    layout.setZoneClassification("Unknown");
                    layout.setSetbackCompliance(100.0);
                    
                    layouts.add(layout);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching layouts", e);
        }
        
        return layouts;
    }
    
    /**
     * Get layout by ID
     * 
     * @param layoutId The layout ID
     * @return Layout object or null if not found
     */
    public Layout getLayoutById(String layoutId) {
        // Modified query to match your table structure
        String sql = "SELECT file_no, layout_name, status, owner, survey_number, area, application_date, latitude, longitude, remarks, total_plots FROM layouts WHERE file_no = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, layoutId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Layout layout = new Layout();
                    layout.setFileNumber(resultSet.getString("file_no"));
                    layout.setLayoutName(resultSet.getString("layout_name"));
                    layout.setStatus(resultSet.getString("status"));
                    layout.setOwnerName(resultSet.getString("owner"));
                    layout.setSurveyNumber(resultSet.getString("survey_number"));
                    layout.setAreaInAcres(resultSet.getDouble("area"));
                    
                    java.sql.Date applicationDate = resultSet.getDate("application_date");
                    if (applicationDate != null) {
                        layout.setApplicationDate(applicationDate.toLocalDate());
                    }
                    
                    layout.setLatitude(resultSet.getDouble("latitude"));
                    layout.setLongitude(resultSet.getDouble("longitude"));
                    layout.setRemarks(resultSet.getString("remarks"));
                    layout.setTotalPlots(resultSet.getInt("total_plots"));
                    
                    // Set default values for missing columns
                    layout.setApprovalDate(null);
                    layout.setApprovedBy("Unknown");
                    layout.setDocumentPath("");
                    layout.setHasCourtCase(false);
                    layout.setZoneClassification("Unknown");
                    layout.setSetbackCompliance(100.0);
                    
                    return layout;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching layout by ID", e);
        }
        
        return null;
    }
    
    /**
     * Add a new layout
     * 
     * @param layout The layout to add
     * @return true if successful, false otherwise
     */
    public boolean addLayout(Layout layout) {
        String sql = "INSERT INTO layouts (file_number, layout_name, status, owner_name, survey_number, " +
                     "area_in_acres, application_date, approval_date, latitude, longitude, remarks, " +
                     "total_plots, approved_by, document_path, has_court_case, zone_classification, " +
                     "setback_compliance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, layout.getFileNumber());
            statement.setString(2, layout.getLayoutName());
            statement.setString(3, layout.getStatus());
            statement.setString(4, layout.getOwnerName());
            statement.setString(5, layout.getSurveyNumber());
            statement.setDouble(6, layout.getAreaInAcres());
            
            if (layout.getApplicationDate() != null) {
                statement.setDate(7, java.sql.Date.valueOf(layout.getApplicationDate()));
            } else {
                statement.setNull(7, Types.DATE);
            }
            
            if (layout.getApprovalDate() != null) {
                statement.setDate(8, java.sql.Date.valueOf(layout.getApprovalDate()));
            } else {
                statement.setNull(8, Types.DATE);
            }
            
            statement.setDouble(9, layout.getLatitude());
            statement.setDouble(10, layout.getLongitude());
            statement.setString(11, layout.getRemarks());
            statement.setInt(12, layout.getTotalPlots());
            statement.setString(13, layout.getApprovedBy());
            statement.setString(14, layout.getDocumentPath());
            statement.setBoolean(15, layout.isHasCourtCase());
            statement.setString(16, layout.getZoneClassification());
            statement.setDouble(17, layout.getSetbackCompliance());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding layout", e);
            return false;
        }
    }
    
    /**
     * Update an existing layout
     * 
     * @param layout The layout to update
     * @return true if successful, false otherwise
     */
    public boolean updateLayout(Layout layout) {
        String sql = "UPDATE layouts SET layout_name = ?, status = ?, owner_name = ?, survey_number = ?, " +
                     "area_in_acres = ?, application_date = ?, approval_date = ?, latitude = ?, longitude = ?, " +
                     "remarks = ?, total_plots = ?, approved_by = ?, document_path = ?, has_court_case = ?, " +
                     "zone_classification = ?, setback_compliance = ? WHERE file_number = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, layout.getLayoutName());
            statement.setString(2, layout.getStatus());
            statement.setString(3, layout.getOwnerName());
            statement.setString(4, layout.getSurveyNumber());
            statement.setDouble(5, layout.getAreaInAcres());
            
            if (layout.getApplicationDate() != null) {
                statement.setDate(6, java.sql.Date.valueOf(layout.getApplicationDate()));
            } else {
                statement.setNull(6, Types.DATE);
            }
            
            if (layout.getApprovalDate() != null) {
                statement.setDate(7, java.sql.Date.valueOf(layout.getApprovalDate()));
            } else {
                statement.setNull(7, Types.DATE);
            }
            
            statement.setDouble(8, layout.getLatitude());
            statement.setDouble(9, layout.getLongitude());
            statement.setString(10, layout.getRemarks());
            statement.setInt(11, layout.getTotalPlots());
            statement.setString(12, layout.getApprovedBy());
            statement.setString(13, layout.getDocumentPath());
            statement.setBoolean(14, layout.isHasCourtCase());
            statement.setString(15, layout.getZoneClassification());
            statement.setDouble(16, layout.getSetbackCompliance());
            statement.setString(17, layout.getFileNumber());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating layout", e);
            return false;
        }
    }
    
    /**
     * Delete a layout by ID
     * 
     * @param layoutId The layout ID
     * @return true if successful, false otherwise
     */
    public boolean deleteLayout(String layoutId) {
        String sql = "DELETE FROM layouts WHERE file_number = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, layoutId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting layout", e);
            return false;
        }
    }
    
    /**
     * Get all layouts
     * 
     * @return List of all layouts
     */
    public List<Layout> getAllLayouts() {
        List<Layout> layouts = new ArrayList<>();
        // Modified query to match your table structure
        String sql = "SELECT file_no, layout_name, status, owner, survey_number, area, application_date, latitude, longitude, remarks, total_plots FROM layouts ORDER BY application_date DESC";
        
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                Layout layout = new Layout();
                layout.setFileNumber(resultSet.getString("file_no"));
                layout.setLayoutName(resultSet.getString("layout_name"));
                layout.setStatus(resultSet.getString("status"));
                layout.setOwnerName(resultSet.getString("owner"));
                layout.setSurveyNumber(resultSet.getString("survey_number"));
                layout.setAreaInAcres(resultSet.getDouble("area"));
                
                java.sql.Date applicationDate = resultSet.getDate("application_date");
                if (applicationDate != null) {
                    layout.setApplicationDate(applicationDate.toLocalDate());
                }
                
                layout.setLatitude(resultSet.getDouble("latitude"));
                layout.setLongitude(resultSet.getDouble("longitude"));
                layout.setRemarks(resultSet.getString("remarks"));
                layout.setTotalPlots(resultSet.getInt("total_plots"));
                
                // Set default values for missing columns
                layout.setApprovalDate(null);
                layout.setApprovedBy("Unknown");
                layout.setDocumentPath("");
                layout.setHasCourtCase(false);
                layout.setZoneClassification("Unknown");
                layout.setSetbackCompliance(100.0);
                
                layouts.add(layout);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching layouts", e);
        }
        
        return layouts;
    }
    
    /**
     * Get court cases by layout ID
     * 
     * @param layoutId The layout ID
     * @return List of court cases for the layout
     */
    public List<CourtCase> getCourtCasesByLayoutId(String layoutId) {
        List<CourtCase> courtCases = new ArrayList<>();
        String sql = "SELECT * FROM court_cases WHERE related_layout_id = ? ORDER BY filing_date DESC";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, layoutId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courtCases.add(mapResultSetToCourtCase(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching court cases by layout ID", e);
        }
        
        return courtCases;
    }
    
    /**
     * Get all court cases
     * 
     * @return List of all court cases
     */
    public List<CourtCase> getAllCourtCases() {
        List<CourtCase> courtCases = new ArrayList<>();
        String sql = "SELECT * FROM court_cases ORDER BY filing_date DESC";
        
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                courtCases.add(mapResultSetToCourtCase(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching court cases", e);
        }
        
        return courtCases;
    }
    
    /**
     * Add a new court case
     * 
     * @param courtCase The court case to add
     * @return true if successful, false otherwise
     */
    public boolean addCourtCase(CourtCase courtCase) {
        String sql = "INSERT INTO court_cases (case_id, case_title, case_type, status, filing_date, " +
                     "hearing_date, judgment_date, description, related_layout_id, court_name, " +
                     "judge_name, petitioner, respondent, case_number, outcome, document_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, courtCase.getCaseId());
            statement.setString(2, courtCase.getCaseTitle());
            statement.setString(3, courtCase.getCaseType());
            statement.setString(4, courtCase.getStatus());
            
            if (courtCase.getFilingDate() != null) {
                statement.setDate(5, java.sql.Date.valueOf(courtCase.getFilingDate()));
            } else {
                statement.setNull(5, Types.DATE);
            }
            
            if (courtCase.getHearingDate() != null) {
                statement.setDate(6, java.sql.Date.valueOf(courtCase.getHearingDate()));
            } else {
                statement.setNull(6, Types.DATE);
            }
            
            if (courtCase.getJudgmentDate() != null) {
                statement.setDate(7, java.sql.Date.valueOf(courtCase.getJudgmentDate()));
            } else {
                statement.setNull(7, Types.DATE);
            }
            
            statement.setString(8, courtCase.getDescription());
            statement.setString(9, courtCase.getRelatedLayoutId());
            statement.setString(10, courtCase.getCourtName());
            statement.setString(11, courtCase.getJudgeName());
            statement.setString(12, courtCase.getPetitioner());
            statement.setString(13, courtCase.getRespondent());
            statement.setString(14, courtCase.getCaseNumber());
            statement.setString(15, courtCase.getOutcome());
            statement.setString(16, courtCase.getDocumentPath());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding court case", e);
            return false;
        }
    }
    
    /**
     * Update an existing court case
     * 
     * @param courtCase The court case to update
     * @return true if successful, false otherwise
     */
    public boolean updateCourtCase(CourtCase courtCase) {
        String sql = "UPDATE court_cases SET case_title = ?, case_type = ?, status = ?, filing_date = ?, " +
                     "hearing_date = ?, judgment_date = ?, description = ?, related_layout_id = ?, " +
                     "court_name = ?, judge_name = ?, petitioner = ?, respondent = ?, case_number = ?, " +
                     "outcome = ?, document_path = ? WHERE case_id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, courtCase.getCaseTitle());
            statement.setString(2, courtCase.getCaseType());
            statement.setString(3, courtCase.getStatus());
            
            if (courtCase.getFilingDate() != null) {
                statement.setDate(4, java.sql.Date.valueOf(courtCase.getFilingDate()));
            } else {
                statement.setNull(4, Types.DATE);
            }
            
            if (courtCase.getHearingDate() != null) {
                statement.setDate(5, java.sql.Date.valueOf(courtCase.getHearingDate()));
            } else {
                statement.setNull(5, Types.DATE);
            }
            
            if (courtCase.getJudgmentDate() != null) {
                statement.setDate(6, java.sql.Date.valueOf(courtCase.getJudgmentDate()));
            } else {
                statement.setNull(6, Types.DATE);
            }
            
            statement.setString(7, courtCase.getDescription());
            statement.setString(8, courtCase.getRelatedLayoutId());
            statement.setString(9, courtCase.getCourtName());
            statement.setString(10, courtCase.getJudgeName());
            statement.setString(11, courtCase.getPetitioner());
            statement.setString(12, courtCase.getRespondent());
            statement.setString(13, courtCase.getCaseNumber());
            statement.setString(14, courtCase.getOutcome());
            statement.setString(15, courtCase.getDocumentPath());
            statement.setString(16, courtCase.getCaseId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating court case", e);
            return false;
        }
    }
    
    /**
     * Delete a court case by ID
     * 
     * @param caseId The court case ID
     * @return true if successful, false otherwise
     */
    public boolean deleteCourtCase(String caseId) {
        String sql = "DELETE FROM court_cases WHERE case_id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, caseId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting court case", e);
            return false;
        }
    }
    
    /**
     * Get dashboard statistics
     * 
     * @return Map containing dashboard statistics
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try (Connection connection = DBConnection.getConnection()) {
            // Total layouts
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM layouts");
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    statistics.put("totalLayouts", resultSet.getInt(1));
                }
            }
            
            // Approved layouts
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM layouts WHERE status = 'Approved'");
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    statistics.put("approvedLayouts", resultSet.getInt(1));
                }
            }
            
            // Pending layouts
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM layouts WHERE status IN ('Pending', 'Under Review')");
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    statistics.put("pendingLayouts", resultSet.getInt(1));
                }
            }
            
            // Rejected layouts
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM layouts WHERE status = 'Rejected'");
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    statistics.put("rejectedLayouts", resultSet.getInt(1));
                }
            }
            
            // Active court cases
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM court_cases WHERE status = 'Active'");
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    statistics.put("activeCases", resultSet.getInt(1));
                }
            }
            
            // Total users
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    statistics.put("totalUsers", resultSet.getInt(1));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching dashboard statistics", e);
        }
        
        return statistics;
    }
    
    /**
     * Map ResultSet to Layout object
     * 
     * @param resultSet The ResultSet
     * @return Layout object
     * @throws SQLException if mapping fails
     */
    private Layout mapResultSetToLayout(ResultSet resultSet) throws SQLException {
        Layout layout = new Layout();
        layout.setFileNumber(resultSet.getString("file_number"));
        layout.setLayoutName(resultSet.getString("layout_name"));
        layout.setStatus(resultSet.getString("status"));
        layout.setOwnerName(resultSet.getString("owner_name"));
        layout.setSurveyNumber(resultSet.getString("survey_number"));
        layout.setAreaInAcres(resultSet.getDouble("area_in_acres"));
        
        java.sql.Date applicationDate = resultSet.getDate("application_date");
        if (applicationDate != null) {
            layout.setApplicationDate(applicationDate.toLocalDate());
        }
        
        java.sql.Date approvalDate = resultSet.getDate("approval_date");
        if (approvalDate != null) {
            layout.setApprovalDate(approvalDate.toLocalDate());
        }
        
        layout.setLatitude(resultSet.getDouble("latitude"));
        layout.setLongitude(resultSet.getDouble("longitude"));
        layout.setRemarks(resultSet.getString("remarks"));
        layout.setTotalPlots(resultSet.getInt("total_plots"));
        layout.setApprovedBy(resultSet.getString("approved_by"));
        layout.setDocumentPath(resultSet.getString("document_path"));
        layout.setHasCourtCase(resultSet.getBoolean("has_court_case"));
        layout.setZoneClassification(resultSet.getString("zone_classification"));
        layout.setSetbackCompliance(resultSet.getDouble("setback_compliance"));
        
        return layout;
    }
    
    /**
     * Map ResultSet to CourtCase object
     * 
     * @param resultSet The ResultSet
     * @return CourtCase object
     * @throws SQLException if mapping fails
     */
    private CourtCase mapResultSetToCourtCase(ResultSet resultSet) throws SQLException {
        CourtCase courtCase = new CourtCase();
        courtCase.setCaseId(resultSet.getString("case_id"));
        courtCase.setCaseTitle(resultSet.getString("case_title"));
        courtCase.setCaseType(resultSet.getString("case_type"));
        courtCase.setStatus(resultSet.getString("status"));
        
        java.sql.Date filingDate = resultSet.getDate("filing_date");
        if (filingDate != null) {
            courtCase.setFilingDate(filingDate.toLocalDate());
        }
        
        java.sql.Date hearingDate = resultSet.getDate("hearing_date");
        if (hearingDate != null) {
            courtCase.setHearingDate(hearingDate.toLocalDate());
        }
        
        java.sql.Date judgmentDate = resultSet.getDate("judgment_date");
        if (judgmentDate != null) {
            courtCase.setJudgmentDate(judgmentDate.toLocalDate());
        }
        
        courtCase.setDescription(resultSet.getString("description"));
        courtCase.setRelatedLayoutId(resultSet.getString("related_layout_id"));
        courtCase.setCourtName(resultSet.getString("court_name"));
        courtCase.setJudgeName(resultSet.getString("judge_name"));
        courtCase.setPetitioner(resultSet.getString("petitioner"));
        courtCase.setRespondent(resultSet.getString("respondent"));
        courtCase.setCaseNumber(resultSet.getString("case_number"));
        courtCase.setOutcome(resultSet.getString("outcome"));
        courtCase.setDocumentPath(resultSet.getString("document_path"));
        
        return courtCase;
    }
    
    /**
     * Delete a user by ID
     * 
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(String userId) {
        // Since your table uses "id" instead of "user_id", we need to adjust the query
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, userId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            return false;
        }
    }
}