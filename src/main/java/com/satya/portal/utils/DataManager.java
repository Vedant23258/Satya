package com.satya.portal.utils;

import com.satya.portal.models.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * DataManager - Singleton class for managing application data
 * Provides mock data for development and testing
 */
public class DataManager {
    private static DataManager instance;
    
    private List<User> mockUsers;
    private List<Layout> mockLayouts;
    private List<CourtCase> mockCourtCases;
    private List<Feedback> mockFeedback;
    private List<Violation> mockViolations;
    private Map<String, Object> statisticsData;
    
    private DataManager() {
        initializeMockData();
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
        initializeFeedback();
        initializeViolations();
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
    
    private void initializeFeedback() {
        mockFeedback = new ArrayList<>();
        
        mockFeedback.add(new Feedback("F001", "U002", "System is very user-friendly", 
                "Positive", LocalDateTime.of(2023, 6, 15, 10, 30), true));
        
        mockFeedback.add(new Feedback("F002", "U003", "Document loading is slow", 
                "Negative", LocalDateTime.of(2023, 6, 14, 14, 20), false));
        
        mockFeedback.add(new Feedback("F003", "U004", "Map feature is excellent", 
                "Positive", LocalDateTime.of(2023, 6, 13, 9, 15), true));
        
        mockFeedback.add(new Feedback("F004", "U002", "Need more search filters", 
                "Suggestion", LocalDateTime.of(2023, 6, 12, 16, 45), false));
        
        mockFeedback.add(new Feedback("F005", "U005", "AI scanning feature is impressive", 
                "Positive", LocalDateTime.of(2023, 6, 11, 11, 30), true));
    }
    
    private void initializeViolations() {
        mockViolations = new ArrayList<>();
        
        mockViolations.add(new Violation("V001", "U002", "Screenshot Attempt", 
                LocalDateTime.of(2023, 6, 15, 14, 30), "Layout document L001", 
                "Attempted to take screenshot"));
        
        mockViolations.add(new Violation("V002", "U003", "Print Attempt", 
                LocalDateTime.of(2023, 6, 14, 16, 15), "Court case document C001", 
                "Attempted to print document"));
        
        mockViolations.add(new Violation("V003", "U004", "Copy Attempt", 
                LocalDateTime.of(2023, 6, 13, 10, 20), "Layout document L003", 
                "Attempted to copy text"));
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
        dashboardStats.put("totalViolations", mockViolations.size());
        dashboardStats.put("totalFeedback", mockFeedback.size());
        
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
    
    // Getter methods
    public List<User> getMockUsers() {
        return new ArrayList<>(mockUsers);
    }
    
    public List<Layout> getMockLayouts() {
        return new ArrayList<>(mockLayouts);
    }
    
    public List<CourtCase> getMockCourtCases() {
        return new ArrayList<>(mockCourtCases);
    }
    
    public List<Feedback> getMockFeedback() {
        return new ArrayList<>(mockFeedback);
    }
    
    public List<Violation> getMockViolations() {
        return new ArrayList<>(mockViolations);
    }
    
    public Map<String, Object> getStatisticsData() {
        return new HashMap<>(statisticsData);
    }
    
    // Search methods
    public List<Layout> searchLayouts(String query, String status, String owner) {
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
    
    public Layout getLayoutById(String layoutId) {
        return mockLayouts.stream()
                .filter(l -> l.getFileNumber().equals(layoutId))
                .findFirst()
                .orElse(null);
    }
    
    public List<CourtCase> getCourtCasesByLayoutId(String layoutId) {
        return mockCourtCases.stream()
                .filter(c -> layoutId.equals(c.getRelatedLayoutId()))
                .collect(ArrayList::new, (list, case_) -> list.add(case_), List::addAll);
    }
    
    // CRUD operations
    public void addLayout(Layout layout) {
        mockLayouts.add(layout);
        updateStatistics();
    }
    
    public void updateLayout(Layout layout) {
        for (int i = 0; i < mockLayouts.size(); i++) {
            if (mockLayouts.get(i).getFileNumber().equals(layout.getFileNumber())) {
                mockLayouts.set(i, layout);
                break;
            }
        }
        updateStatistics();
    }
    
    public void deleteLayout(String layoutId) {
        mockLayouts.removeIf(l -> l.getFileNumber().equals(layoutId));
        updateStatistics();
    }
    
    public void addFeedback(Feedback feedback) {
        mockFeedback.add(feedback);
    }
    
    public void addViolation(Violation violation) {
        mockViolations.add(violation);
    }
    
    private void updateStatistics() {
        // Recalculate statistics when data changes
        Map<String, Integer> dashboardStats = (Map<String, Integer>) statisticsData.get("dashboard");
        dashboardStats.put("totalLayouts", mockLayouts.size());
        dashboardStats.put("approvedLayouts", (int) mockLayouts.stream().filter(l -> "Approved".equals(l.getStatus())).count());
        dashboardStats.put("pendingLayouts", (int) mockLayouts.stream().filter(l -> "Pending".equals(l.getStatus())).count());
        dashboardStats.put("rejectedLayouts", (int) mockLayouts.stream().filter(l -> "Rejected".equals(l.getStatus())).count());
    }

    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private static class Feedback {

        public Feedback(String f001, String u002, String system_is_very_userfriendly, String positive, LocalDateTime of, boolean par) {
        }
    }

    private static class Violation {

        public Violation(String v001, String u002, String screenshot_Attempt, LocalDateTime of, String layout_document_L001, String attempted_to_take_screenshot) {
        }
    }
}