package com.satya.portal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.satya.portal.models.User;
import com.satya.portal.utils.DataManager;
import com.satya.portal.utils.ModernUIUtils;

public class AdminPanel extends JPanel {
    private MainFrame parent;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    
    public AdminPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setOpaque(false);

        add(createStatsPanel(), BorderLayout.NORTH);
        add(createUsersPanel(), BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 18, 0));
        panel.setOpaque(false);

        // Get statistics from DataManager
        java.util.Map<String, Object> stats = DataManager.getInstance().getStatisticsData();
        
        int totalLayouts = (Integer) stats.getOrDefault("totalLayouts", 0);
        int activeUsers = (Integer) stats.getOrDefault("totalUsers", 0);
        int activeCases = (Integer) stats.getOrDefault("activeCases", 0);

        JLabel layoutStats = statCard("Total Layouts", String.valueOf(totalLayouts));
        JLabel usersStats = statCard("Active Users", String.valueOf(activeUsers));
        JLabel logsStats = statCard("Active Cases", String.valueOf(activeCases));

        panel.add(layoutStats);
        panel.add(usersStats);
        panel.add(logsStats);

        return panel;
    }

    private JLabel statCard(String title, String value) {
        JLabel label = new JLabel("<html><center><span style='font-size:13px;'>" + title + "</span><br>"
                + "<span style='font-size:24px;font-weight:bold;color:#2980b9;'>" + value + "</span></center></html>");
        label.setOpaque(true);
        label.setBackground(ModernUIUtils.CARD_BACKGROUND);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIUtils.PRIMARY_BLUE, 2, true),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel createUsersPanel() {
        JPanel panel = ModernUIUtils.createModernCard(15);

        JLabel header = new JLabel("Registered Users");
        header.setFont(ModernUIUtils.HEADER_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(header, BorderLayout.NORTH);

        // Create users table
        String[] columns = { "User ID", "Username", "Full Name", "Role", "Email", "Department", "Status" };
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(usersTableModel);
        ModernUIUtils.styleTable(usersTable);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Load users data
        loadUsersData();

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(ModernUIUtils.createModernBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton refreshBtn = ModernUIUtils.createModernButton("🔄 Refresh", ModernUIUtils.PRIMARY_BLUE);
        JButton addUserBtn = ModernUIUtils.createModernButton("➕ Add User", ModernUIUtils.SUCCESS_GREEN);
        JButton removeUserBtn = ModernUIUtils.createModernButton("❌ Remove User", ModernUIUtils.DANGER_RED);
        
        refreshBtn.addActionListener(e -> loadUsersData());
        addUserBtn.addActionListener(e -> showAddUserDialog());
        removeUserBtn.addActionListener(e -> removeSelectedUser());
        
        actions.add(refreshBtn);
        actions.add(addUserBtn);
        actions.add(removeUserBtn);

        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsersData() {
        // Clear existing data
        usersTableModel.setRowCount(0);
        
        // Load users from database
        List<User> users = DataManager.getInstance().getAllUsers();
        
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().toString(),
                user.getEmail(),
                user.getDepartment(),
                user.isActive() ? "Active" : "Inactive"
            };
            usersTableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(parent, "Add New User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);
        
        JPanel panel = ModernUIUtils.createModernCard(20);
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Create input fields
        JTextField userIdField = new JTextField(20);
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField departmentField = new JTextField(20);
        
        String[] roles = {"ADMIN", "USER", "VIEWER"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        
        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        panel.add(userIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(departmentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = ModernUIUtils.createModernButton("Save", ModernUIUtils.SUCCESS_GREEN);
        JButton cancelButton = ModernUIUtils.createModernButton("Cancel", ModernUIUtils.DANGER_RED);
        
        saveButton.addActionListener(e -> {
            // Validate input
            if (userIdField.getText().trim().isEmpty() || 
                usernameField.getText().trim().isEmpty() || 
                passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields (User ID, Username, Password).", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setUserId(userIdField.getText());
            newUser.setUsername(usernameField.getText());
            newUser.setPassword(new String(passwordField.getPassword()));
            newUser.setFullName(fullNameField.getText());
            newUser.setEmail(emailField.getText());
            newUser.setDepartment(departmentField.getText());
            newUser.setRole(User.Role.fromString((String) roleComboBox.getSelectedItem()));
            newUser.setActive(true);
            
            // Save to database
            if (DataManager.getInstance().registerUser(newUser)) {
                parent.showSuccessMessage("User added successfully!");
                loadUsersData();
                dialog.dispose();
            } else {
                parent.showWarningMessage("Failed to add user. Please try again.");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void removeSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            int result = JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to remove this user?\nThis action cannot be undone.",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                String userId = (String) usersTableModel.getValueAt(selectedRow, 0);
                // Delete user from database
                if (DataManager.getInstance().deleteUser(userId)) {
                    parent.showSuccessMessage("User removed successfully!");
                    loadUsersData(); // Refresh the table
                } else {
                    parent.showWarningMessage("Failed to remove user. Please try again.");
                }
            }
        } else {
            parent.showWarningMessage("Please select a user to remove.");
        }
    }

    public void refreshData() {
        loadUsersData();
    }
}