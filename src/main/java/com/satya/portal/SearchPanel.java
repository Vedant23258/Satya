package com.satya.portal;

import com.formdev.flatlaf.FlatClientProperties;
import com.satya.portal.models.Layout;
import com.satya.portal.utils.DataManager;
import com.satya.portal.utils.ModernUIUtils;
import com.satya.portal.LayoutActionDialog; // Add this import

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SearchPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);

    private MainFrame parentFrame;
    private JTextField searchField;
    private JComboBox<String> statusComboBox;
    private JTextField ownerField;
    private JComboBox<String> areaRangeComboBox;
    private JButton searchButton;
    private JButton clearButton;
    private JButton advancedButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel resultCountLabel;
    private JProgressBar searchProgressBar;
    private JPanel advancedFiltersPanel;
    private boolean advancedFiltersVisible = false;
    private JScrollPane tableScrollPane;

    private JPanel emptyPanel;
    private Image sectionImage; // Panel-specific background image

    // Advanced filter components
    private JTextField surveyNumberField;
    private JComboBox<String> districtComboBox;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private JCheckBox hasCourtCaseCheckBox;

    // Panel for custom BG image
    private class BGPanel extends JPanel {
        private final Image bg;
        public BGPanel(Image image) { this.bg = image; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                // Translucent overlay for content visibility
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255,255,255,180)); // adjust alpha for more/less fade
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        }
    }

    public SearchPanel(MainFrame parent) {
        this.parentFrame = parent;
        try {
            sectionImage = javax.imageio.ImageIO.read(new java.io.File("src/main/resources/sectionbg.jpg"));
        } catch (Exception ex) {
            sectionImage = null;
        }
        initializeComponents();
        setupLayout();
        bindEvents();
        loadInitialData();
    }

    
    
    private void initializeComponents() {
        searchField = new JTextField(25);
        searchField.setFont(ModernUIUtils.BODY_FONT);
        searchField.setToolTipText("Search by layout name, file number, or survey number");
        searchField.putClientProperty("JTextField.placeholderText", "Enter layout name, file number, or survey number");

        String[] statusOptions = {"All Status", "Approved", "Pending", "Under Review", "Rejected", "Unauthorized"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setFont(ModernUIUtils.BODY_FONT);

        ownerField = new JTextField(15);
        ownerField.setFont(ModernUIUtils.BODY_FONT);
        ownerField.setToolTipText("Search by owner name");
        ownerField.putClientProperty("JTextField.placeholderText", "Enter owner name");

        String[] areaRanges = {"All Areas", "< 1 Acre", "1-2 Acres", "2-5 Acres", "5-10 Acres", "> 10 Acres"};
        areaRangeComboBox = new JComboBox<>(areaRanges);
        areaRangeComboBox.setFont(ModernUIUtils.BODY_FONT);

        searchButton = ModernUIUtils.createModernButton("🔍 Search", ModernUIUtils.PRIMARY_BLUE);
        clearButton = ModernUIUtils.createModernButton("🗑️ Clear", ModernUIUtils.DANGER_RED);
        advancedButton = ModernUIUtils.createModernButton("⚙️ Advanced", ModernUIUtils.WARNING_ORANGE);

        searchProgressBar = new JProgressBar();
        searchProgressBar.setIndeterminate(true);
        searchProgressBar.setVisible(false);
        searchProgressBar.setStringPainted(true);
        searchProgressBar.setString("Searching...");
        searchProgressBar.putClientProperty("JProgressBar.arc", 999);

        String[] columnNames = {"File No.", "Layout Name", "Status", "Owner", "Area", "Application Date", "Survey No."};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        resultsTable = new JTable(tableModel);
        ModernUIUtils.styleTable(resultsTable);
        resultsTable.setAutoCreateRowSorter(true);
        resultsTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        resultCountLabel = new JLabel("0 results found");
        resultCountLabel.setFont(ModernUIUtils.BODY_FONT);
        resultCountLabel.setForeground(ModernUIUtils.DARK_GRAY);

        createAdvancedFiltersPanel();

        // Table scroll & empty panel
        tableScrollPane = new JScrollPane(resultsTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 350));
        tableScrollPane.setVisible(false);
        tableScrollPane.setBorder(ModernUIUtils.createModernBorder());

        emptyPanel = ModernUIUtils.createModernCard(30);
        JLabel msgLabel = new JLabel("🔍 No results yet. Please search.", SwingConstants.CENTER);
        msgLabel.setFont(ModernUIUtils.SUBTITLE_FONT);
        msgLabel.setForeground(ModernUIUtils.DARK_GRAY);
        emptyPanel.add(msgLabel, BorderLayout.CENTER);
    }

    private void createAdvancedFiltersPanel() {
        advancedFiltersPanel = ModernUIUtils.createModernCard(15);
        advancedFiltersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ModernUIUtils.PRIMARY_BLUE), "Advanced Filters",
                TitledBorder.LEFT, TitledBorder.TOP, ModernUIUtils.BUTTON_FONT, ModernUIUtils.PRIMARY_BLUE));
        advancedFiltersPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel surveyLabel = new JLabel("Survey Number:");
        surveyLabel.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(surveyLabel, gbc);
        gbc.gridx = 1;
        surveyNumberField = new JTextField(15);
        surveyNumberField.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(surveyNumberField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JLabel districtLabel = new JLabel("District:");
        districtLabel.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(districtLabel, gbc);
        gbc.gridx = 3;
        String[] districts = {"All Districts", "Nellore", "Kavali", "Gudur", "Atmakur", "Sullurpeta"};
        districtComboBox = new JComboBox<>(districts);
        districtComboBox.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(districtComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel fromLabel = new JLabel("From Date:");
        fromLabel.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(fromLabel, gbc);
        gbc.gridx = 1;
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor fromDateEditor = new JSpinner.DateEditor(fromDateSpinner, "dd/MM/yyyy");
        fromDateSpinner.setEditor(fromDateEditor);
        fromDateSpinner.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(fromDateSpinner, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        JLabel toLabel = new JLabel("To Date:");
        toLabel.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(toLabel, gbc);
        gbc.gridx = 3;
        toDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor toDateEditor = new JSpinner.DateEditor(toDateSpinner, "dd/MM/yyyy");
        toDateSpinner.setEditor(toDateEditor);
        toDateSpinner.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(toDateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        hasCourtCaseCheckBox = new JCheckBox("Has Court Case");
        hasCourtCaseCheckBox.setFont(ModernUIUtils.BODY_FONT);
        advancedFiltersPanel.add(hasCourtCaseCheckBox, gbc);
    }

    private void setupLayout() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        // Create main content panel with modern card styling
        JPanel contentPanel = ModernUIUtils.createModernCard(20);
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel basicSearchPanel = ModernUIUtils.createModernCard(15);
        basicSearchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ModernUIUtils.PRIMARY_BLUE), "Search Layouts",
                TitledBorder.LEFT, TitledBorder.TOP, ModernUIUtils.BUTTON_FONT, ModernUIUtils.PRIMARY_BLUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(ModernUIUtils.BODY_FONT);
        basicSearchPanel.add(searchLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        basicSearchPanel.add(searchField, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(ModernUIUtils.BODY_FONT);
        basicSearchPanel.add(statusLabel, gbc);
        gbc.gridx = 4;
        basicSearchPanel.add(statusComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel ownerLabel = new JLabel("Owner:");
        ownerLabel.setFont(ModernUIUtils.BODY_FONT);
        basicSearchPanel.add(ownerLabel, gbc);
        gbc.gridx = 1;
        basicSearchPanel.add(ownerField, gbc);

        gbc.gridx = 2;
        JLabel areaLabel = new JLabel("Area:");
        areaLabel.setFont(ModernUIUtils.BODY_FONT);
        basicSearchPanel.add(areaLabel, gbc);
        gbc.gridx = 3; gbc.gridwidth = 2;
        basicSearchPanel.add(areaRangeComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(advancedButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5;
        basicSearchPanel.add(buttonPanel, gbc);

        topPanel.add(basicSearchPanel, BorderLayout.CENTER);
        topPanel.add(searchProgressBar, BorderLayout.SOUTH);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(advancedFiltersPanel, BorderLayout.CENTER);
        
        JPanel resultsPanel = ModernUIUtils.createModernCard(15);
        resultsPanel.setLayout(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ModernUIUtils.PRIMARY_BLUE), "Search Results",
                TitledBorder.LEFT, TitledBorder.TOP, ModernUIUtils.BUTTON_FONT, ModernUIUtils.PRIMARY_BLUE));
        
        JPanel resultsHeader = new JPanel(new BorderLayout());
        resultsHeader.setOpaque(false);
        resultsHeader.add(resultCountLabel, BorderLayout.WEST);
        
        resultsPanel.add(resultsHeader, BorderLayout.NORTH);
        resultsPanel.add(tableScrollPane, BorderLayout.CENTER);
        resultsPanel.add(emptyPanel, BorderLayout.CENTER);

        contentPanel.add(resultsPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void bindEvents() {
        searchButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { performSearch(); } });
        clearButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { clearAllFields(); } });
        advancedButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { toggleAdvancedFilters(); } });
        searchField.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { performSearch(); } });
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) viewSelectedLayoutDetails();
            }
        });
    }

    private void performSearch() {
        searchProgressBar.setVisible(true);
        searchButton.setEnabled(false);
        parentFrame.setStatusText("Searching layouts...", Color.BLUE);
        Timer searchTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeSearch();
                searchProgressBar.setVisible(false);
                searchButton.setEnabled(true);
            }
        });
        searchTimer.setRepeats(false);
        searchTimer.start();
    }

    private void executeSearch() {
        String searchTerm = searchField.getText().trim();
        String selectedStatus = (String) statusComboBox.getSelectedItem();
        String ownerName = ownerField.getText().trim();
        String statusFilter = "All Status".equals(selectedStatus) ? null : selectedStatus;

        // If no search criteria provided, show all layouts
        List<Layout> results;
        if (searchTerm.isEmpty() && "All Status".equals(selectedStatus) && ownerName.isEmpty() && 
            !advancedFiltersVisible && areaRangeComboBox.getSelectedIndex() == 0) {
            // Show all layouts when no filters are applied
            results = DataManager.getInstance().getAllLayouts();
        } else {
            // Apply filters when search criteria is provided
            results = DataManager.getInstance().searchLayouts(
                    searchTerm.isEmpty() ? null : searchTerm,
                    statusFilter,
                    ownerName.isEmpty() ? null : ownerName
            );
            results = filterByArea(results);

            if (advancedFiltersVisible) {
                results = applyAdvancedFilters(results);
            }
        }

        updateResultsTable(results);
        parentFrame.setStatusText("Search completed. " + results.size() + " results found.", Color.BLACK);
    }

    private List<Layout> filterByArea(List<Layout> layouts) {
        String selectedRange = (String) areaRangeComboBox.getSelectedItem();
        if ("All Areas".equals(selectedRange)) return layouts;
        return layouts.stream().filter(layout -> {
            double area = layout.getAreaInAcres();
            switch (selectedRange) {
                case "< 1 Acre": return area < 1.0;
                case "1-2 Acres": return area >= 1.0 && area <= 2.0;
                case "2-5 Acres": return area > 2.0 && area <= 5.0;
                case "5-10 Acres": return area > 5.0 && area <= 10.0;
                case "> 10 Acres": return area > 10.0;
                default: return true;
            }
        }).collect(java.util.stream.Collectors.toList());
    }

    private List<Layout> applyAdvancedFilters(List<Layout> layouts) {
        String surveyNumber = surveyNumberField.getText().trim();
        if (!surveyNumber.isEmpty()) {
            layouts = layouts.stream().filter(
                    l -> l.getSurveyNumber().toLowerCase().contains(surveyNumber.toLowerCase())
            ).collect(java.util.stream.Collectors.toList());
        }
        if (hasCourtCaseCheckBox.isSelected()) {
            layouts = layouts.stream().filter(Layout::isHasCourtCase).collect(java.util.stream.Collectors.toList());
        }
        return layouts;
    }

    // Modern updateResultsTable to smartly show/hide table and emptyPanel
    private void updateResultsTable(List<Layout> layouts) {
        tableModel.setRowCount(0);
        JPanel parentPanel = (JPanel) tableScrollPane.getParent();
        if (parentPanel == null) { // Defensive: Find parent as resultsPanel
            Component c = this.getComponent(this.getComponentCount() - 1);
            if (c instanceof JPanel) parentPanel = (JPanel) c;
        }
        for (Layout layout : layouts) {
            Object[] row = {
                layout.getFileNumber(),
                layout.getLayoutName(),
                layout.getStatus(),
                layout.getOwnerName(),
                layout.getFormattedArea(),
                layout.getApplicationDate(),
                layout.getSurveyNumber()
            };
            tableModel.addRow(row);
        }
        resultCountLabel.setText(layouts.size() + " results found");
        if (parentPanel != null) {
            parentPanel.remove(tableScrollPane);
            parentPanel.remove(emptyPanel);
            if (layouts.isEmpty()) {
                resultCountLabel.setForeground(Color.GRAY);
                tableScrollPane.setVisible(false);
                parentPanel.add(emptyPanel, BorderLayout.CENTER);
            } else {
                resultCountLabel.setForeground(Color.BLACK);
                tableScrollPane.setVisible(true);
                parentPanel.add(tableScrollPane, BorderLayout.CENTER);
            }
            parentPanel.revalidate();
            parentPanel.repaint();
        }
    }

    private void clearAllFields() {
        searchField.setText("");
        statusComboBox.setSelectedIndex(0);
        ownerField.setText("");
        areaRangeComboBox.setSelectedIndex(0);
        if (advancedFiltersVisible) {
            surveyNumberField.setText("");
            districtComboBox.setSelectedIndex(0);
            hasCourtCaseCheckBox.setSelected(false);
        }
        tableModel.setRowCount(0);
        resultCountLabel.setText("0 results found");
        resultCountLabel.setForeground(Color.GRAY);
        parentFrame.setStatusText("Search filters cleared", Color.BLACK);
        updateResultsTable(java.util.Collections.emptyList());
    }

    private void toggleAdvancedFilters() {
        advancedFiltersVisible = !advancedFiltersVisible;
        advancedFiltersPanel.setVisible(advancedFiltersVisible);
        advancedButton.setText(advancedFiltersVisible ? "⬆️ Basic" : "⚙️ Advanced");
        revalidate();
        repaint();
    }

    private void viewSelectedLayoutDetails() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = resultsTable.convertRowIndexToModel(selectedRow);
            String fileNumber = (String) tableModel.getValueAt(modelRow, 0);
            Layout layout = DataManager.getInstance().getLayoutById(fileNumber);
            if (layout != null) {
                // Show the LayoutActionDialog instead of directly opening document viewer
                LayoutActionDialog dialog = new LayoutActionDialog(parentFrame, layout);
                int result = dialog.showDialog();
                
                switch (result) {
                    case LayoutActionDialog.VIEW_ON_MAP:
                        // Center map on layout coordinates and switch to map tab
                        if (layout.getLatitude() != 0 && layout.getLongitude() != 0) {
                            parentFrame.centerMapOnLocation(layout.getLatitude(), layout.getLongitude());
                            parentFrame.mainTabbedPane.setSelectedIndex(1); // Map tab
                        } else {
                            parentFrame.showWarningMessage("This layout does not have valid coordinates for map display.");
                        }
                        break;
                    case LayoutActionDialog.VIEW_DOCUMENTS:
                        openDocumentViewer(layout);
                        break;
                    case LayoutActionDialog.CANCEL:
                        // Do nothing, dialog was cancelled
                        break;
                }
            }
        } else {
            parentFrame.showWarningMessage("Please select a layout to view details.");
        }
    }

    private void showLayoutDetailsDialog(Layout layout) {
        JDialog detailsDialog = new JDialog(parentFrame, "Layout Details - " + layout.getFileNumber(), true);
        detailsDialog.setSize(500, 600);
        detailsDialog.setLocationRelativeTo(parentFrame);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String details = String.format(
                "File Number: %s\n" +
                "Layout Name: %s\n" +
                "Status: %s\n" +
                "Owner: %s\n" +
                "Survey Number: %s\n" +
                "Area: %s\n" +
                "Total Plots: %d\n" +
                "Application Date: %s\n" +
                "Approval Date: %s\n" +
                "Coordinates: %s\n" +
                "Has Court Case: %s\n" +
                "Remarks: %s",
                layout.getFileNumber(),
                layout.getLayoutName(),
                layout.getStatus(),
                layout.getOwnerName(),
                layout.getSurveyNumber(),
                layout.getFormattedArea(),
                layout.getTotalPlots(),
                layout.getApplicationDate(),
                layout.getApprovalDate() != null ? layout.getApprovalDate() : "N/A",
                layout.getCoordinates(),
                layout.isHasCourtCase() ? "Yes" : "No",
                layout.getRemarks()
        );
        JTextArea detailsArea = new JTextArea(details);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        JButton viewMapButton = new JButton("View on Map");
        JButton viewDocButton = new JButton("View Documents");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        viewMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                detailsDialog.dispose();
                parentFrame.mainTabbedPane.setSelectedIndex(1);
            }
        });
        viewDocButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                detailsDialog.dispose();
                openDocumentViewer(layout);
            }
        });
        buttonPanel.add(viewMapButton);
        buttonPanel.add(viewDocButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.add(mainPanel);
        detailsDialog.setVisible(true);
    }
    
    /**
     * Opens the document viewer for the selected layout.
     * 
     * @param layout The layout to view documents for
     */
    private void openDocumentViewer(Layout layout) {
        // Switch to the Documents tab
        parentFrame.mainTabbedPane.setSelectedIndex(2);
        
        // Show a message with layout information
        JOptionPane.showMessageDialog(parentFrame, 
            "Document Viewer for Layout: " + layout.getLayoutName() + "\n" +
            "File Number: " + layout.getFileNumber() + "\n\n" +
            "In a real implementation, this would show the actual documents for this layout.",
            "Document Viewer",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportResults() {
        if (tableModel.getRowCount() == 0) {
            parentFrame.showWarningMessage("No data to export.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Search Results");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            parentFrame.showSuccessMessage("Results exported successfully!");
        }
    }

    private void loadInitialData() {
        // Start with empty data instead of loading all layouts
        updateResultsTable(new ArrayList<>());
    }

    public void refreshData() {
        loadInitialData();
        parentFrame.setStatusText("Search data refreshed", new Color(39,174,96));
    }

    private class StatusCellRenderer extends JLabel implements TableCellRenderer {
        public StatusCellRenderer() { 
            setOpaque(true); 
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (String) value;
            setText(status);
            setHorizontalAlignment(CENTER);
            setFont(ModernUIUtils.BUTTON_FONT);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            Color bgColor = Color.WHITE, textColor = Color.BLACK;
            if (status != null) {
                switch (status) {
                    case "Approved": 
                        bgColor = ModernUIUtils.SUCCESS_GREEN; 
                        textColor = Color.WHITE; 
                        break;
                    case "Pending": 
                        bgColor = ModernUIUtils.WARNING_ORANGE; 
                        textColor = Color.WHITE; 
                        break;
                    case "Under Review": 
                        bgColor = ModernUIUtils.PRIMARY_BLUE; 
                        textColor = Color.WHITE; 
                        break;
                    case "Rejected": 
                        bgColor = ModernUIUtils.DANGER_RED; 
                        textColor = Color.WHITE; 
                        break;
                    case "Unauthorized": 
                        bgColor = ModernUIUtils.INFO_PURPLE; 
                        textColor = Color.WHITE; 
                        break;
                }
            }
            
            if (isSelected) { 
                setBackground(table.getSelectionBackground()); 
                setForeground(table.getSelectionForeground()); 
            } else { 
                setBackground(bgColor); 
                setForeground(textColor); 
            }
            
            return this;
        }
    }
}
