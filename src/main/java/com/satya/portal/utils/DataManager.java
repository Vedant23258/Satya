package com.satya.portal.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.satya.portal.DBConnection;
import com.satya.portal.models.CourtCase;
import com.satya.portal.models.Layout;
import com.satya.portal.models.User;

/**
 * DataManager - Singleton class for managing application data
 * Provides data from database for production and mock data for development/testing
 */
public class DataManager {
    private static DataManager instance;
    private DatabaseManager databaseManager;
    private boolean useDatabase = false; // Flag to switch between mock and database
    
    private List<User> mockUsers;
    private List<Layout> mockLayouts;
    private List<CourtCase> mockCourtCases;
    private Map<String, Object> statisticsData;
    
    private DataManager() {
        // Try to initialize database manager
        try {
            databaseManager = DatabaseManager.getInstance();
            // Test connection and check if required tables exist
            if (databaseManager != null && DBConnection.testConnection() && testDatabaseTables()) {
                useDatabase = true;
            }
        } catch (Exception e) {
            // If database is not available, fall back to mock data
            useDatabase = false;
        }
        
        if (!useDatabase) {
            initializeMockData();
        }
    }
    
    /**
     * Test if required database tables exist
     * 
     * @return true if all required tables exist, false otherwise
     */
    private boolean testDatabaseTables() {
        try (Connection connection = DBConnection.getConnection()) {
            // Test if layouts table exists by running a simple query
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery("SELECT 1 FROM layouts LIMIT 1");
                return true;
            }
        } catch (SQLException e) {
            // If any exception occurs, tables don't exist or aren't accessible
            return false;
        }
    }
    
    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }
    
    private void initializeMockData() {
        initializeUsers();
        initializeLayouts();
        initializeCourtCases();
        initializeStatistics();
    }
    
    private void initializeUsers() {
        mockUsers = new ArrayList<>();
        
        // Admin user
        mockUsers.add(new User("U001", "admin", "admin123", User.Role.ADMIN,
                "Administrator", "admin@satya.gov.in", "IT Department"));
        
        // Regular user
        mockUsers.add(new User("U002", "user", "user123", User.Role.USER,
                "Regular User", "user@satya.gov.in", "Revenue Department"));
        
        // Viewer user
        mockUsers.add(new User("U003", "viewer", "viewer123", User.Role.VIEWER,
                "Document Viewer", "viewer@satya.gov.in", "Public Services"));
        
        // Additional users
        mockUsers.add(new User("U004", "surveyor", "survey123", User.Role.USER,
                "Field Surveyor", "surveyor@satya.gov.in", "Survey Department"));
        
        mockUsers.add(new User("U005", "manager", "manager123", User.Role.ADMIN,
                "Layout Manager", "manager@satya.gov.in", "Urban Planning"));
    }
    
    private void initializeLayouts() {
        mockLayouts = new ArrayList<>();
        
        // Sample layouts with various statuses
        mockLayouts.add(new Layout("L001", "Green Valley Layout", "Approved", 
                "Rajesh Kumar", "Sy.No. 45, Nellore", 2.5, LocalDate.of(2023, 1, 15),
                14.5234, 79.9876, "All approvals completed", 150));
        
        mockLayouts.add(new Layout("L002", "Sunrise Enclave", "Pending", 
                "Sita Devi", "Sy.No. 78, Kavali", 1.8, LocalDate.of(2023, 3, 20),
                14.9234, 79.8765, "Awaiting NOC clearance", 120));
        
        mockLayouts.add(new Layout("L003", "Metro Heights", "Under Review", 
                "Anil Reddy", "Sy.No. 92, Gudur", 3.2, LocalDate.of(2023, 2, 10),
                14.1456, 79.8432, "Technical review in progress", 200));
        
        mockLayouts.add(new Layout("L004", "Palm Gardens", "Rejected", 
                "Venkat Rao", "Sy.No. 156, Atmakur", 1.2, LocalDate.of(2022, 12, 5),
                14.2345, 79.7654, "Violated setback norms", 80));
        
        mockLayouts.add(new Layout("L005", "Royal Apartments", "Unauthorized", 
                "Unknown", "Sy.No. 203, Nellore", 0.8, LocalDate.of(2023, 4, 1),
                14.4567, 79.9123, "Construction without approval", 60));
        
        mockLayouts.add(new Layout("L006", "Tech City Phase 1", "Approved", 
                "IT Corporation Ltd", "Sy.No. 301-305, Nellore", 15.0, LocalDate.of(2022, 8, 15),
                14.4789, 79.9456, "Special Economic Zone", 500));
        
        mockLayouts.add(new Layout("L007", "Farmers Colony", "Pending", 
                "Agricultural Society", "Sy.No. 87, Sullurpeta", 4.5, LocalDate.of(2023, 5, 10),
                13.7654, 79.8901, "Waiting for environmental clearance", 300));
        
        mockLayouts.add(new Layout("L008", "Coastal View Villas", "Under Review", 
                "Coastal Developers", "Sy.No. 45, Kavali", 2.8, LocalDate.of(2023, 6, 1),
                14.9123, 79.8567, "CRZ clearance verification", 180));
    }
    
    private void initializeCourtCases() {
        mockCourtCases = new ArrayList<>();
        
        mockCourtCases.add(new CourtCase("C001", "Land Dispute - Green Valley", 
                "Civil", "Active", LocalDate.of(2023, 2, 15), LocalDate.of(2023, 8, 20),
                "Boundary dispute resolution", "L001"));
        
        mockCourtCases.add(new CourtCase("C002", "Unauthorized Construction", 
                "Criminal", "Pending", LocalDate.of(2023, 4, 10), null,
                "Construction without proper approvals", "L005"));
        
        mockCourtCases.add(new CourtCase("C003", "NOC Violation Case", 
                "Civil", "Closed", LocalDate.of(2022, 11, 5), LocalDate.of(2023, 1, 15),
                "Violation of No Objection Certificate terms", "L004"));
        
        mockCourtCases.add(new CourtCase("C004", "Environmental Violation", 
                "Civil", "Active", LocalDate.of(2023, 5, 20), LocalDate.of(2023, 12, 1),
                "Violation of environmental norms", "L007"));
    }
    
    private void initializeStatistics() {
        statisticsData = new HashMap<>();
        
        // Dashboard statistics
        Map<String, Integer> dashboardStats = new HashMap<>();
        dashboardStats.put("totalLayouts", mockLayouts.size());
        dashboardStats.put("approvedLayouts", (int) mockLayouts.stream().filter(l -> "Approved".equals(l.getStatus())).count());
        dashboardStats.put("pendingLayouts", (int) mockLayouts.stream().filter(l -> "Pending".equals(l.getStatus())).count());
        dashboardStats.put("rejectedLayouts", (int) mockLayouts.stream().filter(l -> "Rejected".equals(l.getStatus())).count());
        dashboardStats.put("activeCases", (int) mockCourtCases.stream().filter(c -> "Active".equals(c.getStatus())).count());
        dashboardStats.put("totalUsers", mockUsers.size());
        
        statisticsData.put("dashboard", dashboardStats);
        
        // Monthly search trends (mock data)
        int[] searchTrends = {45, 67, 89, 123, 156, 178, 201, 234, 267, 289, 312, 345};
        statisticsData.put("searchTrends", searchTrends);
        
        // Layout status distribution
        Map<String, Integer> statusDistribution = new HashMap<>();
        statusDistribution.put("Approved", 2);
        statusDistribution.put("Pending", 2);
        statusDistribution.put("Under Review", 2);
        statusDistribution.put("Rejected", 1);
        statusDistribution.put("Unauthorized", 1);
        
        statisticsData.put("statusDistribution", statusDistribution);
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
        if (useDatabase && databaseManager != null) {
            return databaseManager.authenticateUser(username, password, role);
        } else {
            // Use mock authentication
            for (User user : mockUsers) {
                if (user.getUsername().equals(username) && user.authenticate(password)) {
                    if ((role == User.Role.ADMIN && user.isAdmin()) ||
                        (role == User.Role.USER && user.canViewDocuments() && !user.isAdmin()) ||
                        (role == User.Role.VIEWER && !user.canViewDocuments())) {
                        return user;
                    }
                }
            }
            return null;
        }
    }
    
    /**
     * Register a new user
     * 
     * @param user The user to register
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(User user) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.registerUser(user);
        } else {
            // Mock implementation
            mockUsers.add(user);
            return true;
        }
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
        if (useDatabase && databaseManager != null) {
            return databaseManager.searchLayouts(query, status, owner);
        } else {
            List<Layout> results = new ArrayList<>();
            
            for (Layout layout : mockLayouts) {
                boolean matches = true;
                
                if (query != null && !query.trim().isEmpty()) {
                    String q = query.toLowerCase();
                    matches = layout.getLayoutName().toLowerCase().contains(q) ||
                             layout.getFileNumber().toLowerCase().contains(q) ||
                             layout.getSurveyNumber().toLowerCase().contains(q);
                }
                
                if (matches && status != null && !status.equals("All")) {
                    matches = layout.getStatus().equals(status);
                }
                
                if (matches && owner != null && !owner.trim().isEmpty()) {
                    matches = layout.getOwnerName().toLowerCase().contains(owner.toLowerCase());
                }
                
                if (matches) {
                    results.add(layout);
                }
            }
            
            return results;
        }
    }
    
    /**
     * Get layout by ID
     * 
     * @param layoutId The layout ID
     * @return Layout object or null if not found
     */
    public Layout getLayoutById(String layoutId) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.getLayoutById(layoutId);
        } else {
            return mockLayouts.stream()
                    .filter(l -> l.getFileNumber().equals(layoutId))
                    .findFirst()
                    .orElse(null);
        }
    }
    
    /**
     * Add a new layout
     * 
     * @param layout The layout to add
     * @return true if successful, false otherwise
     */
    public boolean addLayout(Layout layout) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.addLayout(layout);
        } else {
            mockLayouts.add(layout);
            updateStatistics();
            return true;
        }
    }
    
    /**
     * Update an existing layout
     * 
     * @param layout The layout to update
     * @return true if successful, false otherwise
     */
    public boolean updateLayout(Layout layout) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.updateLayout(layout);
        } else {
            for (int i = 0; i < mockLayouts.size(); i++) {
                if (mockLayouts.get(i).getFileNumber().equals(layout.getFileNumber())) {
                    mockLayouts.set(i, layout);
                    break;
                }
            }
            updateStatistics();
            return true;
        }
    }
    
    /**
     * Delete a layout by ID
     * 
     * @param layoutId The layout ID
     * @return true if successful, false otherwise
     */
    public boolean deleteLayout(String layoutId) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.deleteLayout(layoutId);
        } else {
            mockLayouts.removeIf(l -> l.getFileNumber().equals(layoutId));
            updateStatistics();
            return true;
        }
    }
    
    /**
     * Get all layouts
     * 
     * @return List of all layouts
     */
    public List<Layout> getAllLayouts() {
        if (useDatabase && databaseManager != null) {
            return databaseManager.getAllLayouts();
        } else {
            return new ArrayList<>(mockLayouts);
        }
    }
    
    /**
     * Get court cases by layout ID
     * 
     * @param layoutId The layout ID
     * @return List of court cases for the layout
     */
    public List<CourtCase> getCourtCasesByLayoutId(String layoutId) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.getCourtCasesByLayoutId(layoutId);
        } else {
            return mockCourtCases.stream()
                    .filter(c -> layoutId.equals(c.getRelatedLayoutId()))
                    .collect(ArrayList::new, (list, case_) -> list.add(case_), List::addAll);
        }
    }
    
    /**
     * Get all court cases
     * 
     * @return List of all court cases
     */
    public List<CourtCase> getAllCourtCases() {
        if (useDatabase && databaseManager != null) {
            return databaseManager.getAllCourtCases();
        } else {
            return new ArrayList<>(mockCourtCases);
        }
    }
    
    /**
     * Add a new court case
     * 
     * @param courtCase The court case to add
     * @return true if successful, false otherwise
     */
    public boolean addCourtCase(CourtCase courtCase) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.addCourtCase(courtCase);
        } else {
            mockCourtCases.add(courtCase);
            return true;
        }
    }
    
    /**
     * Update an existing court case
     * 
     * @param courtCase The court case to update
     * @return true if successful, false otherwise
     */
    public boolean updateCourtCase(CourtCase courtCase) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.updateCourtCase(courtCase);
        } else {
            for (int i = 0; i < mockCourtCases.size(); i++) {
                if (mockCourtCases.get(i).getCaseId().equals(courtCase.getCaseId())) {
                    mockCourtCases.set(i, courtCase);
                    break;
                }
            }
            return true;
        }
    }
    
    /**
     * Delete a court case by ID
     * 
     * @param caseId The court case ID
     * @return true if successful, false otherwise
     */
    public boolean deleteCourtCase(String caseId) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.deleteCourtCase(caseId);
        } else {
            mockCourtCases.removeIf(c -> c.getCaseId().equals(caseId));
            return true;
        }
    }
    
    /**
     * Get all users
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        if (useDatabase && databaseManager != null) {
            return databaseManager.getAllUsers();
        } else {
            return new ArrayList<>(mockUsers);
        }
    }
    
    /**
     * Get dashboard statistics
     * 
     * @return Map containing dashboard statistics
     */
    public Map<String, Object> getStatisticsData() {
        if (useDatabase && databaseManager != null) {
            return databaseManager.getDashboardStatistics();
        } else {
            return new HashMap<>(statisticsData);
        }
    }
    
    /**
     * Delete a user by ID
     * 
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(String userId) {
        if (useDatabase && databaseManager != null) {
            return databaseManager.deleteUser(userId);
        } else {
            // Mock implementation
            mockUsers.removeIf(user -> user.getUserId().equals(userId));
            return true;
        }
    }
    
    // Deprecated methods - kept for backward compatibility
    public List<User> getMockUsers() {
        return getAllUsers();
    }
    
    public List<Layout> getMockLayouts() {
        return getAllLayouts();
    }
    
    public List<CourtCase> getMockCourtCases() {
        return getAllCourtCases();
    }
    
    private void updateStatistics() {
        if (!useDatabase) {
            // Recalculate statistics when data changes
            Map<String, Integer> dashboardStats = (Map<String, Integer>) statisticsData.get("dashboard");
            dashboardStats.put("totalLayouts", mockLayouts.size());
            dashboardStats.put("approvedLayouts", (int) mockLayouts.stream().filter(l -> "Approved".equals(l.getStatus())).count());
            dashboardStats.put("pendingLayouts", (int) mockLayouts.stream().filter(l -> "Pending".equals(l.getStatus())).count());
            dashboardStats.put("rejectedLayouts", (int) mockLayouts.stream().filter(l -> "Rejected".equals(l.getStatus())).count());
        }
    }
    
    public void initialize() {
        // No initialization needed as we're using database
    }
}