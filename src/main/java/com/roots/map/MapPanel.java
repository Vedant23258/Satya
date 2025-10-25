package com.roots.map;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    public MapPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Map Panel Placeholder", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    public void refreshData() {
        System.out.println("MapPanel refreshed.");
    }

    public void cleanup() {
        System.out.println("MapPanel cleaned up.");
    }
}