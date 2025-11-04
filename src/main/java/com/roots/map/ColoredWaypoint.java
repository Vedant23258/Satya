package com.roots.map;

import java.awt.Color;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A waypoint that has a color associated with it based on layout status
 */
public class ColoredWaypoint extends DefaultWaypoint {
    private final String label;
    private final Color color;
    private final String status;
    
    /**
     * Create a new colored waypoint
     * @param latitude the latitude
     * @param longitude the longitude
     * @param label the label to display
     * @param status the status of the layout
     */
    public ColoredWaypoint(double latitude, double longitude, String label, String status) {
        super(new GeoPosition(latitude, longitude));
        this.label = label;
        this.status = status;
        this.color = getColorForStatus(status);
    }
    
    /**
     * Create a new colored waypoint
     * @param position the geo position
     * @param label the label to display
     * @param status the status of the layout
     */
    public ColoredWaypoint(GeoPosition position, String label, String status) {
        super(position);
        this.label = label;
        this.status = status;
        this.color = getColorForStatus(status);
    }
    
    /**
     * Get the label for this waypoint
     * @return the label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Get the color for this waypoint based on status
     * @return the color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Get the status for this waypoint
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Get the color for a given status
     * @param status the status
     * @return the corresponding color
     */
    public static Color getColorForStatus(String status) {
        if (status == null) {
            return Color.GRAY;
        }
        
        switch (status.toLowerCase()) {
            case "approved":
                return Color.GREEN;
            case "pending":
                return Color.ORANGE;
            case "under review":
                return Color.BLUE;
            case "rejected":
                return Color.RED;
            case "unauthorized":
                return Color.MAGENTA;
            default:
                return Color.GRAY;
        }
    }
}