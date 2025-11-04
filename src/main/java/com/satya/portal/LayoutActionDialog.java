package com.satya.portal;

import com.satya.portal.models.Layout;
import com.satya.portal.utils.ModernUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A modern dialog that allows users to choose between viewing a layout on the map or viewing its documents
 */
public class LayoutActionDialog extends JDialog {
    private Layout layout;
    private MainFrame parentFrame;
    private int selectedOption = -1; // 0 = View on Map, 1 = View Documents
    
    public static final int VIEW_ON_MAP = 0;
    public static final int VIEW_DOCUMENTS = 1;
    public static final int CANCEL = -1;
    
    /**
     * Create a new layout action dialog
     * @param parent the parent frame
     * @param layout the layout to show actions for
     */
    public LayoutActionDialog(MainFrame parent, Layout layout) {
        super(parent, "Choose Action", true);
        this.parentFrame = parent;
        this.layout = layout;
        
        initializeComponents();
        setupLayout();
        bindEvents();
        
        // Center the dialog on the parent frame
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(400, 200);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel with modern styling
        JPanel mainPanel = ModernUIUtils.createModernCard(20);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BorderLayout());
        
        // Create header with layout information
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Choose Action for Layout");
        titleLabel.setFont(ModernUIUtils.HEADER_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel layoutLabel = new JLabel("<html><center><b>" + layout.getLayoutName() + "</b><br>" +
                "File No: " + layout.getFileNumber() + " | Status: " + layout.getStatus() + "</center></html>");
        layoutLabel.setFont(ModernUIUtils.BODY_FONT);
        layoutLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layoutLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        headerPanel.add(layoutLabel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        JButton viewMapButton = ModernUIUtils.createModernButton("🗺️ View on Map", ModernUIUtils.PRIMARY_BLUE);
        viewMapButton.setPreferredSize(new Dimension(150, 40));
        
        JButton viewDocumentsButton = ModernUIUtils.createModernButton("📄 View Documents", ModernUIUtils.SUCCESS_GREEN);
        viewDocumentsButton.setPreferredSize(new Dimension(150, 40));
        
        JButton cancelButton = ModernUIUtils.createModernButton("❌ Cancel", ModernUIUtils.DANGER_RED);
        cancelButton.setPreferredSize(new Dimension(150, 40));
        
        buttonPanel.add(viewMapButton);
        buttonPanel.add(viewDocumentsButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Store button references for event handling
        viewMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedOption = VIEW_ON_MAP;
                dispose();
            }
        });
        
        viewDocumentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedOption = VIEW_DOCUMENTS;
                dispose();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedOption = CANCEL;
                dispose();
            }
        });
    }
    
    private void bindEvents() {
        // Allow ESC key to close the dialog
        getRootPane().registerKeyboardAction(
            e -> {
                selectedOption = CANCEL;
                dispose();
            },
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    /**
     * Show the dialog and return the selected option
     * @return the selected option (VIEW_ON_MAP, VIEW_DOCUMENTS, or CANCEL)
     */
    public int showDialog() {
        setVisible(true);
        return selectedOption;
    }
    
    /**
     * Get the layout associated with this dialog
     * @return the layout
     */
    public Layout getLayoutData() {
        return layout;
    }
}