package com.satya.portal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.roots.map.MapPanel;
import com.satya.portal.models.User;
import com.satya.portal.utils.DataManager;
import com.satya.portal.utils.ModernUIUtils;
import com.satya.portal.utils.ScreenshotProtection;

public class MainFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);

    private final SATYAPortalApp parentApp;
    private final User currentUser;
    JTabbedPane mainTabbedPane;
    private JLabel statusLabel;
    private JLabel userInfoLabel;
    private JLabel timeLabel;

    private SearchPanel searchPanel;
    private MapPanel mapPanel;
    private JPanel documentViewer;
    private AdminPanel adminPanel;
    private JMenuBar menuBar;
    private Timer clockTimer;
    private Image backgroundImg;

    public MainFrame(SATYAPortalApp app, User user) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}
        UIManager.put("Button.arc", 18);
        UIManager.put("Component.arrowType", "chevron");
        UIManager.put("TabbedPane.selected", new Color(30, 110, 180));
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);

        this.parentApp = app;
        this.currentUser = user;
        try {
            backgroundImg = javax.imageio.ImageIO.read(new File("src/main/resources/frontpage.jpg"));
        } catch (Exception ex) {
            backgroundImg = null;
        }

        initializeFrame();
        createMenuBar();
        createMainInterface();
        createStatusBar();
        setupWindowListeners();
        startClock();
        
        // Initialize screenshot protection
        ScreenshotProtection.protectWindow(this, "Screenshotting is prohibited for security reasons. This application contains sensitive information.");
        
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("SATYA Portal - Layout Verification System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(ModernUIUtils.PRIMARY_BLUE);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(ModernUIUtils.BUTTON_FONT);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmExit());
        // Style menu items
        styleMenuItem(exitItem);
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(Color.WHITE);
        viewMenu.setFont(ModernUIUtils.BUTTON_FONT);
        JMenuItem refreshItem = new JMenuItem("Refresh Data");
        JMenuItem themeItem = new JMenuItem("Toggle Dark Theme");
        themeItem.addActionListener(e -> parentApp.toggleTheme());
        refreshItem.addActionListener(e -> refreshAllData());
        // Style menu items
        styleMenuItem(refreshItem);
        styleMenuItem(themeItem);
        viewMenu.add(refreshItem); viewMenu.addSeparator();
        viewMenu.add(themeItem);

        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setForeground(Color.WHITE);
        toolsMenu.setFont(ModernUIUtils.BUTTON_FONT);
        JMenuItem calculatorItem = new JMenuItem("Area Calculator");
        calculatorItem.addActionListener(e -> openSystemCalculator());
        // Style menu items
        styleMenuItem(calculatorItem);
        toolsMenu.add(calculatorItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(ModernUIUtils.BUTTON_FONT);
        JMenuItem aboutItem = new JMenuItem("About SATYA Portal");
        aboutItem.addActionListener(e -> showAboutDialog());
        // Style menu items
        styleMenuItem(aboutItem);
        helpMenu.add(aboutItem);

        JMenu userMenu = new JMenu(currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userMenu.setForeground(Color.WHITE);
        userMenu.setFont(ModernUIUtils.BUTTON_FONT);
        JMenuItem profileItem = new JMenuItem("My Profile");
        JMenuItem logoutItem = new JMenuItem("Logout");
        profileItem.addActionListener(e -> showUserProfile());
        logoutItem.addActionListener(e -> confirmLogout());
        // Style menu items
        styleMenuItem(profileItem);
        styleMenuItem(logoutItem);
        userMenu.add(profileItem); userMenu.addSeparator(); userMenu.add(logoutItem);

        menuBar.add(fileMenu); menuBar.add(viewMenu); menuBar.add(toolsMenu); menuBar.add(helpMenu); menuBar.add(Box.createHorizontalGlue()); menuBar.add(userMenu);
        setJMenuBar(menuBar);
    }
    
    /**
     * Opens the system calculator based on the operating system.
     */
    private void openSystemCalculator() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec("calc");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                // Linux
                Runtime.getRuntime().exec("gnome-calculator");
            } else if (os.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec("open -a Calculator");
            } else {
                JOptionPane.showMessageDialog(this, "Calculator not supported on this operating system.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to open calculator: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Styles a menu item with modern UI properties.
     *
     * @param menuItem The menu item to style
     */
    private void styleMenuItem(JMenuItem menuItem) {
        menuItem.setFont(ModernUIUtils.BODY_FONT);
        menuItem.setBackground(Color.WHITE);
        menuItem.setForeground(Color.BLACK);
    }

    private void createMainInterface() {
        BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundImg, 0.20f);
        JPanel glassPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(new Color(255,255,255,80));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setBorder(BorderFactory.createEmptyBorder(10,28,16,28));

        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        mainTabbedPane.setFont(ModernUIUtils.BUTTON_FONT);
        mainTabbedPane.setBackground(ModernUIUtils.CARD_BACKGROUND);
        mainTabbedPane.setOpaque(true);
        // Set modern tab styling
        mainTabbedPane.putClientProperty("JTabbedPane.tabAreaAlignment", "leading");
        mainTabbedPane.putClientProperty("JTabbedPane.tabType", "card");
        mainTabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
        mainTabbedPane.putClientProperty("JTabbedPane.tabSeparatorsAtBottom", true);

        searchPanel = new SearchPanel(this);
        mapPanel = new MapPanel();
        documentViewer = createDocumentViewerPanel();

        mainTabbedPane.addTab("🔍 Search", searchPanel);
        mainTabbedPane.addTab("🗺️ Map View", mapPanel);
        mainTabbedPane.addTab("📄 Documents", documentViewer);
        mainTabbedPane.addTab("⚖️ Court Cases", createCourtCasesPanel());
        mainTabbedPane.addTab("❓ Help", createHelpPanel());

        if (currentUser.isAdmin()) {
            adminPanel = new AdminPanel(this);
            mainTabbedPane.addTab("⚙️ Admin", adminPanel);
        }
        glassPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        glassPanel.add(mainTabbedPane, BorderLayout.CENTER);

        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(glassPanel, BorderLayout.CENTER);
        setContentPane(backgroundPanel);
        mainTabbedPane.setSelectedIndex(0);
    }
    
    /**
     * Creates a simple document viewer panel.
     * 
     * @return A JPanel representing the document viewer
     */
    private JPanel createDocumentViewerPanel() {
        JPanel panel = ModernUIUtils.createModernCard(20);
        panel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("Document Viewer");
        headerLabel.setFont(ModernUIUtils.HEADER_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        JTextArea infoArea = new JTextArea();
        infoArea.setText("Document Viewer\n\n" +
            "This panel displays documents related to property layouts.\n\n" +
            "Features:\n" +
            "• Secure document viewing with protection against copying, printing, and screenshots\n" +
            "• Document search and filtering\n" +
            "• Zoom and navigation controls\n" +
            "• Annotation tools\n\n" +
            "To view documents:\n" +
            "1. Search for a layout in the Search tab\n" +
            "2. Double-click on a layout row or click 'View Documents' in the details dialog\n" +
            "3. The document viewer will display relevant documents for that layout");
        infoArea.setEditable(false);
        infoArea.setFont(ModernUIUtils.BODY_FONT);
        infoArea.setBackground(panel.getBackground());
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(ModernUIUtils.createModernBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createHeaderPanel() {
        // Create a gradient panel for the header
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, ModernUIUtils.PRIMARY_BLUE, 0, getHeight(), ModernUIUtils.SECONDARY_BLUE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("SATYA Portal");
        titleLabel.setFont(ModernUIUtils.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        // Ensure the label can display international characters properly
        titleLabel.putClientProperty("html.disable", Boolean.TRUE);
        
        // Force font that supports Telugu characters
        try {
            Font teluguFont = new Font("Noto Sans Telugu", Font.BOLD, 24);
            if (teluguFont.canDisplay('\u0C38')) { // Check if font can display Telugu 'స'
                titleLabel.setFont(teluguFont);
            }
        } catch (Exception e) {
            // Fallback to default if font not available
            titleLabel.setFont(ModernUIUtils.TITLE_FONT);
        }

        JLabel subtitleLabel = new JLabel("సత్య పోర్టల్");
        subtitleLabel.setFont(ModernUIUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(Color.WHITE);
        // Ensure the label can display international characters properly
        subtitleLabel.putClientProperty("html.disable", Boolean.TRUE);
        
        // Force font that supports Telugu characters for subtitle as well
        try {
            Font teluguFont = new Font("Noto Sans Telugu", Font.PLAIN, 16);
            if (teluguFont.canDisplay('\u0C38')) { // Check if font can display Telugu 'స'
                subtitleLabel.setFont(teluguFont);
            }
        } catch (Exception e) {
            // Fallback to default if font not available
            subtitleLabel.setFont(ModernUIUtils.SUBTITLE_FONT);
        }

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(subtitleLabel);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);
        userInfoLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userInfoLabel.setFont(ModernUIUtils.BUTTON_FONT);
        userInfoLabel.setForeground(Color.WHITE);
        timeLabel = new JLabel();
        timeLabel.setFont(ModernUIUtils.BODY_FONT);
        timeLabel.setForeground(Color.WHITE);
        
        // Add a logout button with red color
        JButton logoutButton = new JButton("Logout 🔒");
        ModernUIUtils.styleButton(logoutButton, ModernUIUtils.DANGER_RED);
        logoutButton.addActionListener(e -> confirmLogout());

        infoPanel.add(userInfoLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(timeLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(logoutButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createCourtCasesPanel() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // Create a centered card panel to hold all content
        JPanel cardPanel = ModernUIUtils.createModernCard(25);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header panel with centered title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Court Cases Management");
        headerLabel.setFont(ModernUIUtils.HEADER_FONT);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // Create table data
        String[] columnNames = {"Case ID", "Title", "Type", "Status", "Filing Date", "Hearing Date"};
        Object[][] data = {
            {"C-001", "Parcel XYZ", "Civil", "Active", "2025-09-01", "2025-11-10"},
            {"C-002", "Plot Dispute", "Revenue", "Closed", "2025-07-12", "2025-10-21"},
            {"C-003", "Boundary", "Civil", "Active", "2025-08-19", "2025-12-02"},
        };

        // Create table with modern styling
        JTable table = new JTable(data, columnNames);
        ModernUIUtils.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(ModernUIUtils.createModernBorder());
        
        // Create a panel to center the table and scroll pane
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(scrollPane);
        
        cardPanel.add(tablePanel, BorderLayout.CENTER);

        // Create button panel with centered buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        JButton viewButton = ModernUIUtils.createModernButton("View Details", ModernUIUtils.PRIMARY_BLUE);
        JButton refreshButton = ModernUIUtils.createModernButton("Refresh", ModernUIUtils.SUCCESS_GREEN);

        viewButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String caseId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(cardPanel, "Details for case: " + caseId);
            } else {
                JOptionPane.showMessageDialog(cardPanel, "Please select a row.");
            }
        });

        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Center the card panel in the main panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(cardPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createHelpPanel() {
        JPanel panel = ModernUIUtils.createModernCard(20);
        panel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("SATYA Portal - Help & Documentation");
        headerLabel.setFont(ModernUIUtils.HEADER_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Create HTML content for help panel
        String helpContent = "<html>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Arial, sans-serif; font-size: 14px; }" +
                "h1 { color: #2980b9; font-size: 20px; margin-bottom: 15px; }" +
                "h2 { color: #3498db; font-size: 16px; margin-top: 20px; margin-bottom: 10px; }" +
                "p { margin: 5px 0; line-height: 1.4; }" +
                "ul { margin: 10px 0; padding-left: 20px; }" +
                "li { margin: 5px 0; }" +
                ".section { margin-bottom: 25px; }" +
                ".highlight { background-color: #e3f2fd; padding: 10px; border-left: 3px solid #2980b9; }" +
                "</style>" +
                "<body>" +
                "<div class='section'>" +
                "<h1>📘 SATYA Portal - Layout Verification System</h1>" +
                "<p>Welcome to SATYA Portal, a comprehensive solution for managing property layouts, legal cases, and administrative tasks with enhanced security and user management features.</p>" +
                "</div>" +
                
                "<div class='section'>" +
                "<h2>🔍 Key Features</h2>" +
                "<ul>" +
                "<li><b>Layout Search & Management</b> - Search and filter property layouts with advanced criteria</li>" +
                "<li><b>Interactive Map Visualization</b> - Visual representation of layout locations using OpenStreetMap</li>" +
                "<li><b>Secure Document Viewer</b> - Protected document viewing with anti-copying measures</li>" +
                "<li><b>Court Case Management</b> - Track and manage legal cases related to property disputes</li>" +
                "<li><b>Administrative Tools</b> - User management and system administration (Admin users only)</li>" +
                "</ul>" +
                "</div>" +
                
                "<div class='section'>" +
                "<h2>🧭 Navigation Guide</h2>" +
                "<ul>" +
                "<li><b>Search Tab</b> - Find and filter layouts using multiple search criteria</li>" +
                "<li><b>Map View</b> - Visual representation of layout locations on an interactive map</li>" +
                "<li><b>Documents</b> - Secure document viewing with protection against copying, printing, and screenshots</li>" +
                "<li><b>Court Cases</b> - Legal case management with status tracking</li>" +
                "<li><b>Admin</b> - Administrative functions (Available only for Admin users)</li>" +
                "</ul>" +
                "</div>" +
                
                "<div class='section'>" +
                "<h2>🛠️ Tools & Utilities</h2>" +
                "<ul>" +
                "<li><b>Area Calculator</b> - Opens system calculator for area calculations (Tools menu)</li>" +
                "<li><b>Data Export</b> - Export search results to CSV, Excel, or PDF formats</li>" +
                "<li><b>Advanced Filters</b> - Detailed filtering options for precise search results</li>" +
                "</ul>" +
                "</div>" +
                
                "<div class='section'>" +
                "<h2>🔒 Security Features</h2>" +
                "<ul>" +
                "<li><b>Document Protection</b> - All documents are protected against copying, printing, and screenshots</li>" +
                "<li><b>Violation Monitoring</b> - Security violations are logged and monitored</li>" +
                "<li><b>Role-Based Access Control</b> - Different permissions for Admin, User, and Viewer roles</li>" +
                "<li><b>Session Management</b> - Automatic timeout and logout for security</li>" +
                "</ul>" +
                "</div>" +
                
                "<div class='section highlight'>" +
                "<h2>📞 Support Information</h2>" +
                "<p><b>Technical Support:</b> support@satya.gov.in</p>" +
                "<p><b>Administrative Queries:</b> admin@satya.gov.in</p>" +
                "<p><b>Version:</b> 1.0.0</p>" +
                "<p><b>Developed for:</b> Government of Andhra Pradesh</p>" +
                "</div>" +
                
                "</body>" +
                "</html>";

        JEditorPane helpText = new JEditorPane("text/html", helpContent);
        helpText.setEditable(false);
        helpText.setBackground(panel.getBackground());
        helpText.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        helpText.setFont(ModernUIUtils.BODY_FONT);
        
        // Enable scrolling
        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(ModernUIUtils.createModernBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void createStatusBar() {
        JPanel statusBar = ModernUIUtils.createModernCard(5);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUIUtils.CARD_BORDER));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(ModernUIUtils.BODY_FONT);
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(ModernUIUtils.BODY_FONT);
        versionLabel.setForeground(ModernUIUtils.DARK_GRAY);
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void setupWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void startClock() {
        clockTimer = new Timer(1000, (ActionEvent e) -> updateClock());
        clockTimer.start();
        updateClock();
    }
    private void updateClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");
        timeLabel.setText(java.time.LocalDateTime.now().format(formatter));
    }

    private void showAboutDialog() {
        String aboutText = "SATYA Portal - Layout Verification System\n\n" +
                "Version: 1.0.0\n" +
                "Developed for: Government of Andhra Pradesh\n" +
                "Technology: Java Swing\n\n" +
                "A comprehensive solution for managing property layouts,\n" +
                "legal cases, and administrative tasks with enhanced\n" +
                "security and user management features.\n\n" +
                "© 2023 SATYA Portal Team";
        JOptionPane.showMessageDialog(this, aboutText, "About SATYA Portal", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshAllData() {
        setStatusText("Refreshing data...", Color.BLUE);
        Timer refreshTimer = new Timer(2000, (ActionEvent e) -> {
            if (searchPanel != null) searchPanel.refreshData();
            if (mapPanel != null) mapPanel.refreshData();
            if (adminPanel != null) adminPanel.refreshData();

            setStatusText("Data refreshed successfully", SUCCESS_COLOR);
            Timer clearTimer = new Timer(3000, evt -> setStatusText("Ready", Color.BLACK));
            clearTimer.setRepeats(false);
            clearTimer.start();
        });
        refreshTimer.setRepeats(false);
        refreshTimer.start();
    }

    private void showUserProfile() {
        String profileInfo =
                "User Profile Information\n\n" +
                        "User ID: " + currentUser.getUserId() + "\n" +
                        "Username: " + currentUser.getUsername() + "\n" +
                        "Full Name: " + currentUser.getFullName() + "\n" +
                        "Email: " + currentUser.getEmail() + "\n" +
                        "Department: " + currentUser.getDepartment() + "\n" +
                        "Role: " + currentUser.getRole() + "\n" +
                        "Last Login: " + (currentUser.getLastLogin() != null ?
                        currentUser.getLastLogin().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) :
                        "Not available") + "\n" +
                        "Account Status: " + (currentUser.isActive() ? "Active" : "Inactive");
        JOptionPane.showMessageDialog(this, profileInfo, "User Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            cleanup();
            parentApp.logout();
        }
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit SATYA Portal?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            cleanup();
            System.exit(0);
        }
    }

    public void cleanup() {
        if (clockTimer != null) clockTimer.stop();
        if (mapPanel != null) { /* cleanup if any */ }
        if (documentViewer != null) { /* cleanup if any */ }
    }

    public void setStatusText(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    public void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Get the map panel instance
     * @return the map panel
     */
    public MapPanel getMapPanel() {
        return mapPanel;
    }
    
    /**
     * Center the map on a specific latitude and longitude
     * @param latitude the latitude to center on
     * @param longitude the longitude to center on
     */
    public void centerMapOnLocation(double latitude, double longitude) {
        if (mapPanel != null) {
            mapPanel.centerOnLocation(latitude, longitude);
        }
    }
    
    // AdminPanel (inner class, fully working example; can replace with separate file if needed)
    public static class AdminPanel extends JPanel {
        private MainFrame parent;
        private JTable usersTable;
        private DefaultTableModel usersTableModel;
        
        public AdminPanel(MainFrame parent) {
            this.parent = parent;
            setLayout(new BorderLayout(20, 20));
            setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
            setOpaque(false);

            add(createStatsPanel(), BorderLayout.NORTH);
            add(createUsersPanel(), BorderLayout.CENTER);
        }

        private JPanel createStatsPanel() {
            JPanel panel = new JPanel(new GridLayout(1, 3, 18, 0));
            panel.setOpaque(false);

            // Get statistics from DataManager
            java.util.Map<String, Object> stats = DataManager.getInstance().getStatisticsData();
            
            int totalLayouts = (Integer) stats.getOrDefault("totalLayouts", 0);
            int activeUsers = (Integer) stats.getOrDefault("totalUsers", 0);
            int activeCases = (Integer) stats.getOrDefault("activeCases", 0);

            JLabel layoutStats = statCard("Total Layouts", String.valueOf(totalLayouts));
            JLabel usersStats = statCard("Active Users", String.valueOf(activeUsers));
            JLabel logsStats = statCard("Active Cases", String.valueOf(activeCases));

            panel.add(layoutStats);
            panel.add(usersStats);
            panel.add(logsStats);

            return panel;
        }

        private JLabel statCard(String title, String value) {
            JLabel label = new JLabel("<html><center><span style='font-size:13px;'>" + title + "</span><br>"
                    + "<span style='font-size:24px;font-weight:bold;color:#2980b9;'>" + value + "</span></center></html>");
            label.setOpaque(true);
            label.setBackground(ModernUIUtils.CARD_BACKGROUND);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUIUtils.PRIMARY_BLUE, 2, true),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
            ));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }

        private JPanel createUsersPanel() {
            JPanel panel = ModernUIUtils.createModernCard(15);

            JLabel header = new JLabel("Registered Users");
            header.setFont(ModernUIUtils.HEADER_FONT);
            header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            panel.add(header, BorderLayout.NORTH);

            // Create users table

            String[] columns = { "User ID", "Username", "Full Name", "Role", "Email", "Department", "Status" };
            usersTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            usersTable = new JTable(usersTableModel);
            ModernUIUtils.styleTable(usersTable);
            usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            // Set preferred column widths for better visibility
            usersTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // User ID
            usersTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Username
            usersTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Full Name
            usersTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Role
            usersTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Email
            usersTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Department
            usersTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status

            // Load users data
            loadUsersData();

            JScrollPane scrollPane = new JScrollPane(usersTable);
            scrollPane.setBorder(ModernUIUtils.createModernBorder());
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            actions.setOpaque(false);
            JButton refreshBtn = ModernUIUtils.createModernButton("🔄 Refresh", ModernUIUtils.PRIMARY_BLUE);
            JButton addUserBtn = ModernUIUtils.createModernButton("➕ Add User", ModernUIUtils.SUCCESS_GREEN);
            JButton removeUserBtn = ModernUIUtils.createModernButton("❌ Remove User", ModernUIUtils.DANGER_RED);
            
            refreshBtn.addActionListener(e -> loadUsersData());
            addUserBtn.addActionListener(e -> showAddUserDialog());
            removeUserBtn.addActionListener(e -> removeSelectedUser());
            
            actions.add(refreshBtn);
            actions.add(addUserBtn);
            actions.add(removeUserBtn);

            panel.add(actions, BorderLayout.SOUTH);

            return panel;
        }

        private void loadUsersData() {
            // Clear existing data
            usersTableModel.setRowCount(0);
            
            // Load users from database
            java.util.List<User> users = DataManager.getInstance().getAllUsers();
            
            for (User user : users) {
                Object[] row = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole().toString(),
                    user.getEmail(),
                    user.getDepartment(),
                    user.isActive() ? "Active" : "Inactive"
                };
                usersTableModel.addRow(row);
            }
        }

        private void showAddUserDialog() {
            JDialog dialog = new JDialog(parent, "Add New User", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(parent);
            
            JPanel panel = ModernUIUtils.createModernCard(20);
            panel.setLayout(new GridBagLayout());
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Create input fields
            JTextField userIdField = new JTextField(20);
            JTextField usernameField = new JTextField(20);
            JPasswordField passwordField = new JPasswordField(20);
            JTextField fullNameField = new JTextField(20);
            JTextField emailField = new JTextField(20);
            JTextField departmentField = new JTextField(20);
            
            String[] roles = {"ADMIN", "USER", "VIEWER"};
            JComboBox<String> roleComboBox = new JComboBox<>(roles);
            
            // Add components to panel
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("User ID:"), gbc);
            gbc.gridx = 1;
            panel.add(userIdField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            panel.add(usernameField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            panel.add(passwordField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("Full Name:"), gbc);
            gbc.gridx = 1;
            panel.add(fullNameField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 4;
            panel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            panel.add(emailField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 5;
            panel.add(new JLabel("Department:"), gbc);
            gbc.gridx = 1;
            panel.add(departmentField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 6;
            panel.add(new JLabel("Role:"), gbc);
            gbc.gridx = 1;
            panel.add(roleComboBox, gbc);
            
            // Add buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton saveButton = ModernUIUtils.createModernButton("Save", ModernUIUtils.SUCCESS_GREEN);
            JButton cancelButton = ModernUIUtils.createModernButton("Cancel", ModernUIUtils.DANGER_RED);
            
            saveButton.addActionListener(e -> {
                // Create new user
                User newUser = new User();
                newUser.setUserId(userIdField.getText());
                newUser.setUsername(usernameField.getText());
                newUser.setPassword(new String(passwordField.getPassword()));
                newUser.setFullName(fullNameField.getText());
                newUser.setEmail(emailField.getText());
                newUser.setDepartment(departmentField.getText());
                newUser.setRole(User.Role.fromString((String) roleComboBox.getSelectedItem()));
                newUser.setActive(true);
                
                // Save to database
                if (DataManager.getInstance().registerUser(newUser)) {
                    parent.showSuccessMessage("User added successfully!");
                    loadUsersData();
                    dialog.dispose();
                } else {
                    parent.showWarningMessage("Failed to add user. Please try again.");
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);
            
            dialog.add(panel);
            dialog.setVisible(true);
        }

        private void removeSelectedUser() {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int result = JOptionPane.showConfirmDialog(parent,
                    "Are you sure you want to remove this user?\nThis action cannot be undone.",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION) {
                    String userId = (String) usersTableModel.getValueAt(selectedRow, 0);
                    // Delete user from database
                    if (DataManager.getInstance().deleteUser(userId)) {
                        parent.showSuccessMessage("User removed successfully!");
                        loadUsersData(); // Refresh the table
                    } else {
                        parent.showWarningMessage("Failed to remove user. Please try again.");
                    }
                }
            } else {
                parent.showWarningMessage("Please select a user to remove.");
            }
        }

        public void refreshData() {
            loadUsersData();
        }
    }
}
