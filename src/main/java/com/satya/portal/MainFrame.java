package com.satya.portal;

import com.roots.map.MapPanel;
import com.satya.portal.models.User;
import com.satya.portal.utils.DataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.io.File;




public class MainFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color HEADER_COLOR = new Color(22, 70, 134);   
    private static final Color HEADER_HOVER = new Color(30, 110, 180);

    private final SATYAPortalApp parentApp;
    private final User currentUser;
    JTabbedPane mainTabbedPane;
    private JLabel statusLabel;
    private JLabel userInfoLabel;
    private JLabel timeLabel;

    // Panel components
    private SearchPanel searchPanel;
    private MapPanel mapPanel;
    private JPanel documentViewer;
    private AdminPanel adminPanel;

    // Menu components
    private JMenuBar menuBar;
    private Timer clockTimer;

    private Image backgroundImg;

    public MainFrame(SATYAPortalApp app, User user) {
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

        setVisible(false);
    }

    private void initializeFrame() {
        setTitle("SATYA Portal - Layout Verification System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // All content goes to contentPane later.
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
         menuBar.setOpaque(true);
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(new Font("Arial", Font.BOLD, 12));
        JMenuItem newLayoutItem = new JMenuItem("New Layout Application");
        JMenuItem importItem = new JMenuItem("Import Data");
        JMenuItem exportItem = new JMenuItem("Export Reports");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmExit());
        fileMenu.add(newLayoutItem);
        fileMenu.addSeparator();
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(Color.WHITE);
        viewMenu.setFont(new Font("Arial", Font.BOLD, 12));
        JMenuItem refreshItem = new JMenuItem("Refresh Data");
        JMenuItem themeItem = new JMenuItem("Toggle Dark Theme");
        JMenuItem fullScreenItem = new JMenuItem("Toggle Full Screen");
        themeItem.addActionListener(e -> parentApp.toggleTheme());
        refreshItem.addActionListener(e -> refreshAllData());
        viewMenu.add(refreshItem);
        viewMenu.addSeparator();
        viewMenu.add(themeItem);
        viewMenu.add(fullScreenItem);

        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setForeground(Color.WHITE);
        toolsMenu.setFont(new Font("Arial", Font.BOLD, 12));
        JMenuItem calculatorItem = new JMenuItem("Area Calculator");
        JMenuItem validatorItem = new JMenuItem("Document Validator");
        JMenuItem reportsItem = new JMenuItem("Generate Reports");
        toolsMenu.add(calculatorItem);
        toolsMenu.add(validatorItem);
        toolsMenu.add(reportsItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("Arial", Font.BOLD, 12));
        JMenuItem userGuideItem = new JMenuItem("User Guide");
        JMenuItem shortcutsItem = new JMenuItem("Keyboard Shortcuts");
        JMenuItem aboutItem = new JMenuItem("About SATYA Portal");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(userGuideItem);
        helpMenu.add(shortcutsItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        JMenu userMenu = new JMenu(currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userMenu.setForeground(Color.WHITE);
        userMenu.setFont(new Font("Arial", Font.BOLD, 12));
        JMenuItem profileItem = new JMenuItem("My Profile");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem logoutItem = new JMenuItem("Logout");
        profileItem.addActionListener(e -> showUserProfile());
        logoutItem.addActionListener(e -> confirmLogout());
        userMenu.add(profileItem);
        userMenu.add(settingsItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userMenu);

        setJMenuBar(menuBar);
    }

   private void createMainInterface() {
    BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundImg, 0.18f); // fade as needed
    backgroundPanel.add(createHeaderPanel(), BorderLayout.NORTH);

    mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    mainTabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
    mainTabbedPane.setBackground(Color.WHITE);
    mainTabbedPane.setOpaque(false);

    searchPanel = new SearchPanel(this);
    mapPanel = new MapPanel();
    documentViewer = new JPanel();

    mainTabbedPane.addTab("🔍 Search", searchPanel);
    mainTabbedPane.addTab("🗺️ Map View", mapPanel);
    mainTabbedPane.addTab("📄 Documents", documentViewer);
    mainTabbedPane.addTab("⚖️ Court Cases", createCourtCasesPanel());
    mainTabbedPane.addTab("❓ Help", createHelpPanel());

    if (currentUser.isAdmin()) {
        adminPanel = new AdminPanel(this);
        mainTabbedPane.addTab("⚙️ Admin", adminPanel);
    }

    backgroundPanel.add(mainTabbedPane, BorderLayout.CENTER);

    setContentPane(backgroundPanel);
    mainTabbedPane.setSelectedIndex(0);
}


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("SATYA Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("సత్య పోర్టల్");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(subtitleLabel);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);

        userInfoLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        userInfoLabel.setForeground(Color.WHITE);

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(Color.WHITE);

        infoPanel.add(userInfoLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(timeLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCourtCasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Court Cases Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(headerLabel, BorderLayout.NORTH);

        String[] columnNames = {"Case ID", "Title", "Type", "Status", "Filing Date", "Hearing Date"};
        Object[][] data = DataManager.getInstance().getMockCourtCases().stream()
                .map(c -> new Object[]{
                        c.getCaseId(),
                        c.getCaseTitle(),
                        c.getCaseType(),
                        c.getStatus(),
                        c.getFilingDate(),
                        c.getHearingDate()
                }).toArray(Object[][]::new);

        JTable table = new JTable(data, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        viewButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String caseId = (String) table.getValueAt(selectedRow, 0);
                showCourtCaseDetails(caseId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a court case to view details.");
            }
        });

        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHelpPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("SATYA Portal - Help & Documentation");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(headerLabel, BorderLayout.NORTH);

        JTextArea helpText = new JTextArea();
        helpText.setText(
                "Welcome to SATYA Portal - Layout Verification System\n\n" +
                        "FEATURES:\n" +
                        "• Search and filter property layouts\n" +
                        "• View layout details and documents\n" +
                        "• Interactive map visualization\n" +
                        "• Court case management\n" +
                        "• Secure document viewing\n" +
                        "• Administrative tools (Admin users only)\n\n" +
                        "NAVIGATION:\n" +
                        "• Use the tabs above to switch between different sections\n" +
                        "• Search tab: Find and filter layouts\n" +
                        "• Map View: Visual representation of layout locations\n" +
                        "• Documents: Secure document viewing\n" +
                        "• Court Cases: Legal case management\n" +
                        "• Admin: Administrative functions (Admin only)\n\n" +
                        "SECURITY:\n" +
                        "• All documents are protected against copying, printing, and screenshots\n" +
                        "• Violations are logged and monitored\n" +
                        "• Role-based access control\n\n" +
                        "SUPPORT:\n" +
                        "For technical support, contact: support@satya.gov.in\n" +
                        "For administrative queries: admin@satya.gov.in"
        );
        helpText.setEditable(false);
        helpText.setFont(new Font("Arial", Font.PLAIN, 12));
        helpText.setBackground(panel.getBackground());

        JScrollPane scrollPane = new JScrollPane(helpText);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        versionLabel.setForeground(Color.GRAY);
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
        clockTimer = new Timer(1000, (ActionEvent e) -> {
            updateClock();
        });
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");
        timeLabel.setText(java.time.LocalDateTime.now().format(formatter));
    }

    private void showAboutDialog() {
        String aboutText =
                "SATYA Portal - Layout Verification System\n\n" +
                        "Version: 1.0.0\n" +
                        "Developed for: Government of Andhra Pradesh\n" +
                        "Technology: Java Swing\n\n" +
                        "A comprehensive solution for managing property layouts,\n" +
                        "legal cases, and administrative tasks with enhanced\n" +
                        "security and user management features.\n\n" +
                        "© 2023 SATYA Portal Team";
        JOptionPane.showMessageDialog(this, aboutText, "About SATYA Portal", JOptionPane.INFORMATION_MESSAGE);
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

    private void showCourtCaseDetails(String caseId) {
        JOptionPane.showMessageDialog(this, "Court Case Details for: " + caseId, "Case Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshAllData() {
        setStatusText("Refreshing data...", Color.BLUE);

        Timer refreshTimer;
        refreshTimer = new Timer(2000, (ActionEvent e) -> {
            if (searchPanel != null) searchPanel.refreshData();
            if (null != mapPanel) mapPanel.refreshData();
            if (adminPanel != null) adminPanel.refreshData();

            setStatusText("Data refreshed successfully", SUCCESS_COLOR);

            Timer clearTimer = new Timer(3000, evt -> setStatusText("Ready", Color.BLACK));
            clearTimer.setRepeats(false);
            clearTimer.start();
        });
        refreshTimer.setRepeats(false);
        refreshTimer.start();
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

    @SuppressWarnings("empty-statement")
    public void cleanup() {
        if (clockTimer != null) {
            clockTimer.stop();
        }

        if (mapPanel == null) {
        } else {
            ;
        }
        if (documentViewer == null) {
        } else {
            ;
        }
    }

    public void setStatusText(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public SATYAPortalApp getParentApp() {
        return parentApp;
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public class AdminPanel extends JPanel {
        public AdminPanel(MainFrame aThis) {}
        private void refreshData() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
