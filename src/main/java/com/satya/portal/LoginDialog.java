package com.satya.portal;

import com.satya.portal.models.User;
import com.satya.portal.utils.DataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static java.lang.constant.ConstantDescs.NULL;
import java.util.List;

/**
 * Login Dialog for SATYA Portal
 * Provides authentication interface with role-based access
 */
public class LoginDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    
    private final SATYAPortalApp parentApp;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton cancelButton;
    private JCheckBox rememberMeCheckBox;
    private JLabel statusLabel;
    
    private List<User> mockUsers;
    private int loginAttempts = 0;
    private final int MAX_LOGIN_ATTEMPTS = 3;
    private User user;
    
    public LoginDialog(Frame parent, SATYAPortalApp app) {
        super(parent, "SATYA Portal - Login", true);
        this.parentApp = app;
        this.mockUsers = DataManager.getInstance().getMockUsers();
        
        initializeComponents();
        setupLayout();
        bindEvents();
        setupDialog();
    }
    
    private void initializeComponents() {
        // Username field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Role selection
        String[] roles = {"Select Role", "Admin", "User", "Viewer"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Buttons
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Remember me checkbox
        rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("SATYA Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("సత్య పోర్టల్ - Layout Verification System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username row
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        // Password row
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Role row
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);
        
        // Remember me row
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(rememberMeCheckBox, gbc);
        
        // Status row
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
        
        // Demo credentials panel
        JPanel demoPanel = createDemoCredentialsPanel();
        
        // Add all panels
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(demoPanel, BorderLayout.WEST);
    }
    
    private JPanel createDemoCredentialsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Demo Credentials"));
        panel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Test Accounts:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        
        // Admin account
        panel.add(new JLabel("Admin:"));
        panel.add(new JLabel("  Username: admin"));
        panel.add(new JLabel("  Password: admin123"));
        panel.add(Box.createVerticalStrut(10));
        
        // User account
        panel.add(new JLabel("User:"));
        panel.add(new JLabel("  Username: user"));
        panel.add(new JLabel("  Password: user123"));
        panel.add(Box.createVerticalStrut(10));
        
        // Viewer account
        panel.add(new JLabel("Viewer:"));
        panel.add(new JLabel("  Username: viewer"));
        panel.add(new JLabel("  Password: viewer123"));
        
        return panel;
    }
    
    private void bindEvents() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Cancel button action
        cancelButton.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        // Enter key listener for password field
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        // Role selection auto-fill for demo
        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) roleComboBox.getSelectedItem();
                if (selectedRole != null && !selectedRole.equals("Select Role")) {
                    autoFillCredentials(selectedRole.toLowerCase());
                }
            }
        });
    }
    
    private void autoFillCredentials(String role) {
        switch (role) {
            case "admin":
                usernameField.setText("admin");
                passwordField.setText("admin123");
                break;
            case "user":
                usernameField.setText("user");
                passwordField.setText("user123");
                break;
            case "viewer":
                usernameField.setText("viewer");
                passwordField.setText("viewer123");
                break;
        }
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty() || 
            selectedRole == null || selectedRole.equals("Select Role")) {
            showStatus("Please fill all fields", ERROR_COLOR);
            return;
        }
        
        // Show loading
        showStatus("Authenticating...", Color.BLUE);
        loginButton.setEnabled(false);
        
        // Simulate authentication delay
        Timer timer = new Timer(1500, (ActionEvent e) -> {
            authenticateUser(username, password, selectedRole);
            loginButton.setEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void authenticateUser(String username, String password, String roleStr) {
        User.Role expectedRole = User.Role.fromString(roleStr);
        User matchedUser = null;
        
        // Find user in mock data
     User authenticatedUser = null;

for (User user : mockUsers) {
    if (user.getUsername().equals(username) &&
        user.authenticate(password) &&
        user.getRole() == expectedRole) {
        authenticatedUser = user;
        break;
    }
}

if (authenticatedUser != null) {
    authenticatedUser.updateLastLogin();

    // Wrap in final array to bypass inner class restriction
    final User[] wrapper = new User[]{authenticatedUser};

    Timer successTimer = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            parentApp.onLoginSuccess(wrapper[0]); // ✅ safe access
        }
    });
    successTimer.setRepeats(false);
    successTimer.start();

        } else {
            // Login failed
            loginAttempts++;
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                showStatus("Too many failed attempts. Application will close.", ERROR_COLOR);
                Timer closeTimer = new Timer(2000, (ActionEvent e) -> {
                    System.exit(0);
                });
                closeTimer.setRepeats(false);
                closeTimer.start();
            } else {
                showStatus("Invalid credentials. Attempt " + loginAttempts + " of " + MAX_LOGIN_ATTEMPTS, ERROR_COLOR);
                passwordField.selectAll();
                passwordField.requestFocus();
            }
        }
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        
        // Set focus to username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
}