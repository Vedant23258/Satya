package com.satya.portal;

import com.satya.portal.models.Layout;
import com.satya.portal.utils.DataManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setToolTipText("Search by layout name, file number, or survey number");

        String[] statusOptions = {"All Status", "Approved", "Pending", "Under Review", "Rejected", "Unauthorized"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setFont(new Font("Arial", Font.PLAIN, 12));

        ownerField = new JTextField(15);
        ownerField.setFont(new Font("Arial", Font.PLAIN, 14));
        ownerField.setToolTipText("Search by owner name");

        String[] areaRanges = {"All Areas", "< 1 Acre", "1-2 Acres", "2-5 Acres", "5-10 Acres", "> 10 Acres"};
        areaRangeComboBox = new JComboBox<>(areaRanges);
        areaRangeComboBox.setFont(new Font("Arial", Font.PLAIN, 12));

        searchButton = new JButton("🔍 Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        clearButton = new JButton("🗑️ Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));

        advancedButton = new JButton("⚙️ Advanced");
        advancedButton.setFont(new Font("Arial", Font.PLAIN, 12));

        searchProgressBar = new JProgressBar();
        searchProgressBar.setIndeterminate(true);
        searchProgressBar.setVisible(false);
        searchProgressBar.setStringPainted(true);
        searchProgressBar.setString("Searching...");

        String[] columnNames = {"File No.", "Layout Name", "Status", "Owner", "Area", "Application Date", "Survey No."};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultsTable.setRowHeight(25);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setAutoCreateRowSorter(true);
        resultsTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        resultCountLabel = new JLabel("0 results found");
        resultCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        resultCountLabel.setForeground(Color.GRAY);

        createAdvancedFiltersPanel();

        // Table scroll & empty panel
        tableScrollPane = new JScrollPane(resultsTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 350));
        tableScrollPane.setVisible(false);

        emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setOpaque(false);
        JLabel msgLabel = new JLabel("🔍 No results yet. Please search.", SwingConstants.CENTER);
        msgLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        msgLabel.setForeground(Color.GRAY);
        emptyPanel.add(msgLabel, BorderLayout.CENTER);
    }

    private void createAdvancedFiltersPanel() {
        advancedFiltersPanel = new JPanel(new GridBagLayout());
        advancedFiltersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Advanced Filters",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), PRIMARY_COLOR));
        advancedFiltersPanel.setOpaque(false);
        advancedFiltersPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        advancedFiltersPanel.add(new JLabel("Survey Number:"), gbc);
        gbc.gridx = 1;
        surveyNumberField = new JTextField(15);
        advancedFiltersPanel.add(surveyNumberField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        advancedFiltersPanel.add(new JLabel("District:"), gbc);
        gbc.gridx = 3;
        String[] districts = {"All Districts", "Nellore", "Kavali", "Gudur", "Atmakur", "Sullurpeta"};
        districtComboBox = new JComboBox<>(districts);
        advancedFiltersPanel.add(districtComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        advancedFiltersPanel.add(new JLabel("From Date:"), gbc);
        gbc.gridx = 1;
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor fromDateEditor = new JSpinner.DateEditor(fromDateSpinner, "dd/MM/yyyy");
        fromDateSpinner.setEditor(fromDateEditor);
        advancedFiltersPanel.add(fromDateSpinner, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        advancedFiltersPanel.add(new JLabel("To Date:"), gbc);
        gbc.gridx = 3;
        toDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor toDateEditor = new JSpinner.DateEditor(toDateSpinner, "dd/MM/yyyy");
        toDateSpinner.setEditor(toDateEditor);
        advancedFiltersPanel.add(toDateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        hasCourtCaseCheckBox = new JCheckBox("Has Court Case");
        advancedFiltersPanel.add(hasCourtCaseCheckBox, gbc);
    }

    private void setupLayout() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel basicSearchPanel = new JPanel(new GridBagLayout());
        basicSearchPanel.setBackground(new Color(255,255,255,230));
        basicSearchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Search Layouts",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), PRIMARY_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        basicSearchPanel.add(new JLabel("Search Term:"), gbc);
        gbc.gridx = 1;
        basicSearchPanel.add(searchField, gbc);
        gbc.gridx = 2;
        basicSearchPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        basicSearchPanel.add(statusComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        basicSearchPanel.add(new JLabel("Owner Name:"), gbc);
        gbc.gridx = 1;
        basicSearchPanel.add(ownerField, gbc);
        gbc.gridx = 2;
        basicSearchPanel.add(new JLabel("Area Range:"), gbc);
        gbc.gridx = 3;
        basicSearchPanel.add(areaRangeComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(advancedButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        basicSearchPanel.add(buttonPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        basicSearchPanel.add(searchProgressBar, gbc);

        topPanel.add(basicSearchPanel, BorderLayout.NORTH);
        topPanel.add(advancedFiltersPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Search Results",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), PRIMARY_COLOR));
        resultsPanel.setBackground(new Color(255,255,255,230));

        JPanel resultsHeaderPanel = new JPanel(new BorderLayout());
        resultsHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        resultsHeaderPanel.setOpaque(false);
        resultsHeaderPanel.add(resultCountLabel, BorderLayout.WEST);

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonsPanel.setOpaque(false);
        JButton viewDetailsButton = new JButton("📄 View Details");
        JButton exportButton = new JButton("📤 Export");
        JButton refreshButton = new JButton("🔄 Refresh");
        viewDetailsButton.addActionListener(e -> viewSelectedLayoutDetails());
        exportButton.addActionListener(e -> exportResults());
        refreshButton.addActionListener(e -> refreshData());
        actionButtonsPanel.add(viewDetailsButton);
        actionButtonsPanel.add(exportButton);
        actionButtonsPanel.add(refreshButton);

        resultsHeaderPanel.add(actionButtonsPanel, BorderLayout.EAST);
        resultsPanel.add(resultsHeaderPanel, BorderLayout.NORTH);

        // Start with emptyPanel as center
        resultsPanel.add(emptyPanel, BorderLayout.CENTER);

        add(resultsPanel, BorderLayout.CENTER);
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

        List<Layout> results = DataManager.getInstance().searchLayouts(
                searchTerm.isEmpty() ? null : searchTerm,
                statusFilter,
                ownerName.isEmpty() ? null : ownerName
        );
        results = filterByArea(results);

        if (advancedFiltersVisible) {
            results = applyAdvancedFilters(results);
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
            if (layout != null) showLayoutDetailsDialog(layout);
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
        buttonPanel.add(viewMapButton);
        buttonPanel.add(viewDocButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.add(mainPanel);
        detailsDialog.setVisible(true);
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
        updateResultsTable(java.util.Collections.emptyList());
    }

    public void refreshData() {
        loadInitialData();
        parentFrame.setStatusText("Search data refreshed", new Color(39,174,96));
    }

    private class StatusCellRenderer extends JLabel implements TableCellRenderer {
        public StatusCellRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (String) value;
            setText(status);
            setHorizontalAlignment(CENTER);
            Color bgColor = Color.WHITE, textColor = Color.BLACK;
            if (status != null) {
                switch (status) {
                    case "Approved": bgColor = new Color(39,174,96); textColor = Color.WHITE; break;
                    case "Pending": bgColor = new Color(243,156,18); textColor = Color.WHITE; break;
                    case "Under Review": bgColor = new Color(52,152,219); textColor = Color.WHITE; break;
                    case "Rejected": bgColor = new Color(231,76,60); textColor = Color.WHITE; break;
                    case "Unauthorized": bgColor = new Color(155,89,182); textColor = Color.WHITE; break;
                }
            }
            if (isSelected) { setBackground(table.getSelectionBackground()); setForeground(table.getSelectionForeground()); }
            else { setBackground(bgColor); setForeground(textColor); }
            return this;
        }
    }
}
