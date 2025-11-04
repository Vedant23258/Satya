package com.satya.portal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.satya.portal.models.User;
import com.satya.portal.utils.DataManager;
import com.satya.portal.utils.ModernUIUtils;

public class LoginDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color CARD_BG = new Color(255, 255, 255, 235);

    private final SATYAPortalApp parentApp;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton cancelButton;
    private JCheckBox rememberMeCheckBox;
    private JLabel statusLabel;

    private int loginAttempts = 0;
    private final int MAX_LOGIN_ATTEMPTS = 3;

    public LoginDialog(Frame parent, SATYAPortalApp app) {
        super(parent, "SATYA Portal - Login", true);
        this.parentApp = app;

        initializeComponents();
        setupLayout();
        bindEvents();
        setupDialog();
    }

    private void initializeComponents() {
        usernameField = new JTextField(18);
        usernameField.setFont(ModernUIUtils.BODY_FONT);
        usernameField.putClientProperty("JTextField.placeholderText", "Enter username");

        passwordField = new JPasswordField(18);
        passwordField.setFont(ModernUIUtils.BODY_FONT);
        passwordField.putClientProperty("JTextField.placeholderText", "Enter password");

        String[] roles = {"Select Role", "Admin", "User", "Viewer"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(ModernUIUtils.BODY_FONT);

        loginButton = ModernUIUtils.createModernButton("Login", ModernUIUtils.PRIMARY_BLUE);
        cancelButton = ModernUIUtils.createModernButton("Cancel", ModernUIUtils.DANGER_RED);

        rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setFont(ModernUIUtils.BODY_FONT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(ModernUIUtils.BODY_FONT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

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
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 8, 24));
        JLabel titleLabel = new JLabel("SATYA Portal", SwingConstants.CENTER);
        titleLabel.setFont(ModernUIUtils.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        JLabel subtitleLabel = new JLabel("సత్య పోర్టల్ - Layout Verification System", SwingConstants.CENTER);
        subtitleLabel.setFont(ModernUIUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Create a modern card for the login form
        JPanel cardPanel = ModernUIUtils.createModernCard(32);
        cardPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(ModernUIUtils.BODY_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        cardPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        cardPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(ModernUIUtils.BODY_FONT);
        gbc.gridx = 0; gbc.gridy = 1;
        cardPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        cardPanel.add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(ModernUIUtils.BODY_FONT);
        gbc.gridx = 0; gbc.gridy = 2;
        cardPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        cardPanel.add(roleComboBox, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(rememberMeCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(statusLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 18));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void bindEvents() {
        loginButton.addActionListener(e -> performLogin());
        cancelButton.addActionListener(e -> System.exit(0));
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        roleComboBox.addActionListener(e -> {
            String selectedRole = String.valueOf(roleComboBox.getSelectedItem());
            if (selectedRole != null && !selectedRole.equals("Select Role")) autoFillCredentials(selectedRole.toLowerCase());
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
        String selectedRole = String.valueOf(roleComboBox.getSelectedItem());
        if (username.isEmpty() || password.isEmpty() || selectedRole == null || selectedRole.equals("Select Role")) {
            showStatus("Please fill all fields", ERROR_COLOR);
            return;
        }
        showStatus("Authenticating...", PRIMARY_COLOR);
        loginButton.setEnabled(false);

        // Directly authenticate, no timer/recursion = NO LOOP BUG
        authenticateUser(username, password, selectedRole);
        loginButton.setEnabled(true);
    }

    private void authenticateUser(String username, String password, String roleStr) {
        // Use database authentication through DataManager
        User.Role role = User.Role.USER; // default
        switch (roleStr.toLowerCase()) {
            case "admin":
                role = User.Role.ADMIN;
                break;
            case "user":
                role = User.Role.USER;
                break;
            case "viewer":
                role = User.Role.VIEWER;
                break;
        }
        
        User authenticatedUser = DataManager.getInstance().authenticateUser(username, password, role);

        if (authenticatedUser != null) {
            authenticatedUser.updateLastLogin();
            dispose(); // CLOSE login dialog BEFORE opening main window
            User finalAuthenticatedUser = authenticatedUser; // 👈 assign to final-style variable

            // ✅ Now use in lambda safely
            SwingUtilities.invokeLater(() -> parentApp.onLoginSuccess(finalAuthenticatedUser));
        } else {
            loginAttempts++;
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                showStatus("Too many failed attempts. Application will close.", ERROR_COLOR);
                new Timer(2000, e -> System.exit(0)).start();
            } else {
                showStatus("Invalid credentials. Attempt " + loginAttempts
                        + " of " + MAX_LOGIN_ATTEMPTS, ERROR_COLOR);
                passwordField.selectAll();
                passwordField.requestFocus();
            }
        }
    }

    private void showStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
        // Add a subtle animation effect
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
}