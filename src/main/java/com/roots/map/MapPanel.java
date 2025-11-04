package com.roots.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import com.satya.portal.DBConnection;
import com.roots.map.ColoredWaypoint;

/**
 * MapPanel that displays OpenStreetMap tiles with proper HTTPS support
 */
public class MapPanel extends JPanel {
    private JXMapViewer mapViewer;
    private WaypointPainter<ColoredWaypoint> waypointPainter;
    private List<ColoredWaypoint> waypoints;
    private static final Logger logger = Logger.getLogger(MapPanel.class.getName());
    
    public MapPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create and configure the map viewer
        createMapViewer();
        
        // Add to frame
        add(mapViewer, BorderLayout.CENTER);
    }
    
    private void createMapViewer() {
        try {
            // Create a JXMapViewer
            mapViewer = new JXMapViewer();
            
            // Setup tile factory using OpenStreetMap with HTTPS
            TileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", 
                "https://tile.openstreetmap.org");
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            
            // Set user agent to comply with OSM tile usage policy
            tileFactory.setUserAgent("SATYA-Portal/1.0");
            tileFactory.setThreadPoolSize(8); // Increase thread pool for better performance
            
            mapViewer.setTileFactory(tileFactory);
            
            // Set initial zoom and position (centered on Andhra Pradesh, India)
            GeoPosition center = new GeoPosition(15.9129, 79.7400); // Approximate center of Andhra Pradesh
            mapViewer.setZoom(7);
            mapViewer.setAddressLocation(center);
            
            // Add mouse interactions for pan and zoom
            PanMouseInputListener panListener = new PanMouseInputListener(mapViewer);
            mapViewer.addMouseListener(panListener);
            mapViewer.addMouseMotionListener(panListener);
            mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
            
            // Create a simple painter for demonstration
            Painter<JXMapViewer> painter = new Painter<JXMapViewer>() {
                @Override
                public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
                    // This is where you would draw custom elements on the map
                    // For now, we'll just leave it empty
                }
            };
            
            // Initialize waypoints list
            waypoints = new ArrayList<>();
            
            // Set up waypoint painter
            waypointPainter = new WaypointPainter<>();
            waypointPainter.setRenderer(new ColoredWaypointRenderer());
            waypointPainter.setWaypoints(new java.util.HashSet<>(waypoints));
            
            // Set up compound painter
            CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>();
            compoundPainter.setPainters(painter, waypointPainter);
            mapViewer.setOverlayPainter(compoundPainter);
            
            // Load waypoints from database
            loadWaypointsFromDatabase();
            
        } catch (Exception e) {
            logger.severe("Error creating map viewer: " + e.getMessage());
            e.printStackTrace();
            // Create a fallback panel with error message
            createFallbackPanel();
        }
    }
    
    private void createFallbackPanel() {
        // Remove the map viewer if it was added
        removeAll();
        
        // Create a user-friendly error panel
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel errorLabel = new JLabel("<html><center>" +
            "<h2>🗺️ Map Loading Issue</h2>" +
            "<p>Unable to load OpenStreetMap tiles.</p>" +
            "<p>Possible causes:</p>" +
            "<ul>" +
            "<li>No internet connection</li>" +
            "<li>Firewall blocking access</li>" +
            "<li>OpenStreetMap server temporarily unavailable</li>" +
            "</ul>" +
            "<p><i>Please check your network connection and try again.</i></p>" +
            "</center></html>", 
            SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        add(errorPanel, BorderLayout.CENTER);
        
        // Create a minimal map viewer that at least shows the UI
        mapViewer = new JXMapViewer();
    }

    public void refreshData() {
        // Load new data from database and update the map
        System.out.println("MapPanel refreshed.");
        loadWaypointsFromDatabase();
    }

    /**
     * Load waypoints from the database and add them to the map
     */
    private void loadWaypointsFromDatabase() {
        waypoints.clear();
        
        String query = "SELECT layout_name, latitude, longitude, status FROM layouts WHERE latitude IS NOT NULL AND longitude IS NOT NULL AND latitude != 0 AND longitude != 0";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                String layoutName = resultSet.getString("layout_name");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                String status = resultSet.getString("status");
                
                // Create a colored waypoint and add it to the list
                ColoredWaypoint waypoint = new ColoredWaypoint(latitude, longitude, layoutName, status);
                waypoints.add(waypoint);
            }
            
            // Update the waypoint painter with the new waypoints
            waypointPainter.setWaypoints(new java.util.HashSet<>(waypoints));
            
            // Repaint the map to show the new waypoints
            mapViewer.repaint();
            
        } catch (SQLException e) {
            logger.severe("Error loading waypoints from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Center the map on a specific latitude and longitude
     * @param latitude the latitude to center on
     * @param longitude the longitude to center on
     */
    public void centerOnLocation(double latitude, double longitude) {
        if (mapViewer != null) {
            GeoPosition position = new GeoPosition(latitude, longitude);
            
            // Use SwingUtilities.invokeLater to ensure UI updates happen on EDT
            SwingUtilities.invokeLater(() -> {
                // Set the position first
                mapViewer.setAddressLocation(position);
                // Then set zoom level
                mapViewer.setZoom(17); // Zoom in closer to show the specific location clearly
                
                // Force immediate update
                mapViewer.repaint();
                mapViewer.revalidate();
                
                // Schedule additional updates to ensure proper rendering
                SwingUtilities.invokeLater(() -> {
                    mapViewer.repaint();
                    mapViewer.revalidate();
                    
                    // Final update after a short delay
                    Timer timer = new Timer(200, e -> {
                        mapViewer.setAddressLocation(position);
                        mapViewer.setZoom(17);
                        mapViewer.repaint();
                        mapViewer.revalidate();
                    });
                    timer.setRepeats(false);
                    timer.start();
                });
            });
        }
    }
    
    public void cleanup() {
        System.out.println("MapPanel cleaned up.");
        // Clean up resources if needed
    }
}