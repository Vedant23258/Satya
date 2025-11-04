package com.satya.portal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
import com.satya.portal.models.User;
import com.satya.portal.utils.ModernUIUtils;

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
                UIManager.setLookAndFeel(new FlatLightLaf());
                SATYAPortalApp app = new SATYAPortalApp();
                app.initializeApplication();
            } catch (Exception e) {
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
        splash.setSize(500, 350);
        splash.setLocationRelativeTo(null);

        // Create a modern gradient panel for the splash screen
        JPanel panel = new JPanel(new BorderLayout()) {
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
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createLineBorder(ModernUIUtils.DARK_GRAY, 1));

        JLabel titleLabel = new JLabel("SATYA Portal", JLabel.CENTER);
        titleLabel.setFont(ModernUIUtils.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("సత్య పోర్టల్", JLabel.CENTER);
        subtitleLabel.setFont(ModernUIUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(Color.WHITE);

        JLabel versionLabel = new JLabel("Version " + VERSION, JLabel.CENTER);
        versionLabel.setFont(ModernUIUtils.BODY_FONT);
        versionLabel.setForeground(Color.WHITE);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        progressBar.putClientProperty("JProgressBar.arc", 999);
        progressBar.setFont(ModernUIUtils.BODY_FONT);
        progressBar.setForeground(Color.WHITE);
        progressBar.setBackground(new Color(0, 0, 0, 30));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
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
        try {
            if (isDarkTheme) {
                com.formdev.flatlaf.FlatDarkLaf.setup();
            } else {
                com.formdev.flatlaf.FlatLightLaf.setup();
            }
            if (mainFrame != null) {
                SwingUtilities.updateComponentTreeUI(mainFrame);
            }
        } catch (Exception e) {
            e.printStackTrace();
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