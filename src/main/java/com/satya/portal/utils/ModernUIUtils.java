package com.satya.portal.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Utility class for modern UI styling in the SATYA Portal application.
 * Provides consistent styling for buttons, panels, tables, and other UI components.
 */
public class ModernUIUtils {
    
    // Color palette for the modern UI
    public static final Color PRIMARY_BLUE = new Color(41, 128, 185);
    public static final Color SECONDARY_BLUE = new Color(52, 152, 219);
    public static final Color SUCCESS_GREEN = new Color(39, 174, 96);
    public static final Color WARNING_ORANGE = new Color(243, 156, 18);
    public static final Color DANGER_RED = new Color(231, 76, 60);
    public static final Color INFO_PURPLE = new Color(155, 89, 182);
    public static final Color LIGHT_GRAY = new Color(236, 240, 241);
    public static final Color DARK_GRAY = new Color(127, 140, 141);
    public static final Color CARD_BACKGROUND = new Color(255, 255, 255, 230);
    public static final Color CARD_BORDER = new Color(220, 220, 220);
    
    // Font preferences for better emoji support
    private static final String[] FONT_PREFERENCES = {
        "Segoe UI Emoji",
        "Segoe UI",
        "Arial Unicode MS",
        "DejaVu Sans",
        "Noto Color Emoji",
        "Apple Color Emoji",
        "Noto Emoji",
        "Symbola",
        "Android Emoji",
        "EmojiOne Color",
        "Twemoji Mozilla",
        "Noto Sans CJK",
        "Microsoft YaHei",
        "SimHei",
        "PMingLiU",
        "sans-serif",
        "serif",
        "monospaced"
    };
    
    /**
     * Gets the best available font with emoji support
     * 
     * @param style Font style (Font.PLAIN, Font.BOLD, etc.)
     * @param size Font size
     * @return Font with emoji support
     */
    public static Font getEmojiSupportedFont(int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        // Check if preferred fonts are available
        for (String fontName : FONT_PREFERENCES) {
            for (String availableFont : availableFonts) {
                if (availableFont.equalsIgnoreCase(fontName)) {
                    Font font = new Font(fontName, style, size);
                    // Test if font can display emojis
                    if (font.canDisplayUpTo("🔍") < 0) {
                        return font;
                    }
                }
            }
        }
        // Fallback to default font
        return new Font("Dialog", style, size);
    }
    
    // Fonts with emoji support
    public static final Font HEADER_FONT = getEmojiSupportedFont(Font.BOLD, 18);
    public static final Font TITLE_FONT = getEmojiSupportedFont(Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = getEmojiSupportedFont(Font.PLAIN, 16);
    public static final Font BODY_FONT = getEmojiSupportedFont(Font.PLAIN, 14);
    public static final Font BUTTON_FONT = getEmojiSupportedFont(Font.BOLD, 14);
    
    /**
     * Creates a modern styled button with the specified text and color.
     *
     * @param text The text to display on the button
     * @param color The background color for the button
     * @return A styled JButton
     */
    public static JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        styleButton(button, color);
        return button;
    }
    
    /**
     * Styles a button with modern UI properties.
     *
     * @param button The button to style
     * @param color The background color for the button
     */
    public static void styleButton(JButton button, Color color) {
        button.setFont(BUTTON_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Make button rounded
        button.putClientProperty("JButton.buttonType", "roundRect");
    }
    
    /**
     * Creates a modern card panel with specified padding.
     *
     * @param padding The padding around the panel content
     * @return A styled JPanel
     */
    public static JPanel createModernCard(int padding) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createModernBorder());
        if (padding > 0) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                createModernBorder(),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
            ));
        }
        return panel;
    }
    
    /**
     * Creates a modern border with subtle shadow effect.
     *
     * @return A styled border
     */
    public static Border createModernBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)
        );
    }
    
    /**
     * Styles a table with modern UI properties.
     *
     * @param table The table to style
     */
    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Style the table header
        table.getTableHeader().setFont(BUTTON_FONT);
        table.getTableHeader().setBackground(PRIMARY_BLUE);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Set alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(BODY_FONT);
                
                if (isSelected) {
                    c.setBackground(PRIMARY_BLUE);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    c.setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
    }
    
    /**
     * Creates a status badge with the specified text and color.
     *
     * @param text The text to display on the badge
     * @param color The background color for the badge
     * @return A styled JLabel
     */
    public static JLabel createStatusBadge(String text, Color color) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setFont(BUTTON_FONT);
        badge.setBackground(color);
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return badge;
    }
}