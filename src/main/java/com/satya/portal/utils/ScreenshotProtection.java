package com.satya.portal.utils;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Utility class to protect against screenshots in the SATYA Portal application.
 * Detects common screenshot key combinations and shows warning messages.
 */
public class ScreenshotProtection {
    
    /**
     * Protect a window from screenshot attempts by monitoring key combinations
     * @param frame the frame to protect
     * @param warningMessage the warning message to show when screenshot keys are detected
     */
    public static void protectWindow(JFrame frame, String warningMessage) {
        // Add a global key listener to detect screenshot attempts
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof KeyEvent) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    
                    // Check for common screenshot key combinations
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        // PrintScreen key
                        if (keyEvent.getKeyCode() == KeyEvent.VK_PRINTSCREEN) {
                            showWarningDialog(frame, warningMessage);
                            keyEvent.consume();
                        }
                        
                        // Alt + PrintScreen (active window screenshot)
                        if (keyEvent.getKeyCode() == KeyEvent.VK_PRINTSCREEN && keyEvent.isAltDown()) {
                            showWarningDialog(frame, warningMessage);
                            keyEvent.consume();
                        }
                        
                        // Windows + Shift + S (Windows 10/11 screenshot tool)
                        if (keyEvent.getKeyCode() == KeyEvent.VK_S && keyEvent.isShiftDown() && isWindowsKeyPresent(keyEvent)) {
                            showWarningDialog(frame, warningMessage);
                            keyEvent.consume();
                        }
                        
                        // Windows + PrintScreen (full screen screenshot)
                        if (keyEvent.getKeyCode() == KeyEvent.VK_PRINTSCREEN && isWindowsKeyPresent(keyEvent)) {
                            showWarningDialog(frame, warningMessage);
                            keyEvent.consume();
                        }
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }
    
    /**
     * Check if Windows key is pressed (this is a simplified check)
     * @param keyEvent the key event
     * @return true if Windows key is likely pressed
     */
    private static boolean isWindowsKeyPresent(KeyEvent keyEvent) {
        // This is a simplified approach since we can't directly detect Windows key
        // In a real implementation, you might need to use JNA or other native libraries
        return keyEvent.isMetaDown() || keyEvent.isControlDown();
    }
    
    /**
     * Show a warning dialog about screenshot protection
     * @param parent the parent frame
     * @param message the warning message
     */
    private static void showWarningDialog(JFrame parent, String message) {
        // Use invokeLater to ensure the dialog is shown on the EDT
        java.awt.EventQueue.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                parent,
                message,
                "Screenshot Protection",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }
}