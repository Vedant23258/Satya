package com.satya.portal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class PortalSecurityManager {
    private static final PortalSecurityManager INSTANCE = new PortalSecurityManager();
    private static final int MAX_VIOLATIONS = 3;
    private final List<String> violations = new ArrayList<>();

    private PortalSecurityManager() {}

    public static PortalSecurityManager getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        System.out.println("Portal Security Manager initialized");
    }

    public void logViolation(String user, String type, String details) {
        String entry = LocalDateTime.now() + " [" + user + "] " + type + " - " + details;
        violations.add(entry);
        System.err.println("[SECURITY] " + entry);
        if (violations.size() >= MAX_VIOLATIONS) {
            blockUser();
        }
    }

    public List<String> getViolations() {
        return List.copyOf(violations);
    }

    private void blockUser() {
        javax.swing.SwingUtilities.invokeLater(() ->
            javax.swing.JOptionPane.showMessageDialog(null,
                "Maximum security violations reached - access blocked.",
                "Security Alert", javax.swing.JOptionPane.ERROR_MESSAGE));
    }
}