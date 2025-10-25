package com.satya.portal;

import com.satya.portal.models.User;
import com.satya.portal.PortalSecurityManager;
import static com.satya.portal.utils.DataManager.getInstance;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main Application Class for SATYA Portal - Layout Verification System
 * A comprehensive Java Swing application for managing property layouts,
 * court cases, document verification, and administrative tasks.
 */
public class SATYAPortalApp {
    private static final String VERSION = "1.0.0";

    private JFrame mainFrame;
    private LoginDialog loginDialog;
    private ResourceBundle messages;
    private User currentUser;
    private boolean isDarkTheme = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new NimbusLookAndFeel());
                SATYAPortalApp app = new SATYAPortalApp();
                app.initializeApplication();
            } catch (UnsupportedLookAndFeelException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize application: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void initializeApplication() {
        loadLocalization("en");
        PortalSecurityManager.getInstance().initialize();
        showSplashScreen();
        showLoginDialog();
    }

    private void loadLocalization(String language) {
        Locale locale = new Locale(language);
        messages = ResourceBundle.getBundle("messages", locale);
    }

    private void showSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(400, 300);
        splash.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2));

        JLabel titleLabel = new JLabel("SATYA Portal", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("సత్య పోర్టల్", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.WHITE);

        JLabel versionLabel = new JLabel("Version " + VERSION, JLabel.CENTER);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(Color.WHITE);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");

        JPanel textPanel = new JPanel(new GridLayout(3, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        textPanel.add(versionLabel);

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        splash.add(panel);
        splash.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            splash.setVisible(false);
            splash.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showLoginDialog() {
        loginDialog = new LoginDialog(null, this);
        loginDialog.setVisible(true);
    }

    public void onLoginSuccess(User user) {
        this.currentUser = user;
        loginDialog.dispose();

        mainFrame = new MainFrame(this, user);
        mainFrame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(mainFrame,
                    "Welcome, " + user.getUsername() + "!\nRole: " + user.getRole(),
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void logout() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
        currentUser = null;
        showLoginDialog();
    }

    public void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        applyTheme();
    }

    private void applyTheme() {
        if (isDarkTheme) {
            UIManager.put("control", new Color(64, 64, 64));
            UIManager.put("text", Color.WHITE);
            UIManager.put("nimbusBase", new Color(32, 32, 32));
            UIManager.put("nimbusFocus", new Color(41, 128, 185));
        } else {
            UIManager.put("control", new Color(240, 240, 240));
            UIManager.put("text", Color.BLACK);
            UIManager.put("nimbusBase", Color.WHITE);
            UIManager.put("nimbusFocus", new Color(41, 128, 185));
        }

        if (mainFrame != null) {
            SwingUtilities.updateComponentTreeUI(mainFrame);
        }
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isDarkTheme() {
        return isDarkTheme;
    }
}