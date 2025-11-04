package com.roots.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

/**
 * Renderer for colored waypoints on the map
 */
public class ColoredWaypointRenderer implements WaypointRenderer<ColoredWaypoint> {
    private static final int PIN_RADIUS = 8;
    private static final int LABEL_OFFSET = 5;
    
    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer viewer, ColoredWaypoint waypoint) {
        // Get the position of the waypoint on the map
        Point2D point2d = viewer.getTileFactory().geoToPixel(waypoint.getPosition(), viewer.getZoom());
        Point position = new Point((int) point2d.getX(), (int) point2d.getY());
        
        // Enable anti-aliasing for smoother rendering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the pin as a colored circle
        Color pinColor = waypoint.getColor();
        g.setColor(pinColor);
        
        // Fill the circle
        Ellipse2D circle = new Ellipse2D.Double(
            position.x - PIN_RADIUS, 
            position.y - PIN_RADIUS, 
            PIN_RADIUS * 2, 
            PIN_RADIUS * 2
        );
        g.fill(circle);
        
        // Draw border around the circle
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.0f));
        g.draw(circle);
        
        // Draw the label below the pin
        String label = waypoint.getLabel();
        if (label != null && !label.isEmpty()) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Get font metrics to center the text
            int labelWidth = g.getFontMetrics().stringWidth(label);
            
            // Draw the label centered below the pin
            g.drawString(
                label, 
                position.x - labelWidth / 2, 
                position.y + PIN_RADIUS + LABEL_OFFSET + g.getFontMetrics().getAscent()
            );
        }
    }
}