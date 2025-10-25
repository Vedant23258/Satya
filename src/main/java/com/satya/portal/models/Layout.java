package com.satya.portal.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Layout model class representing property layouts in the system
 */
public class Layout {
    private String fileNumber;
    private String layoutName;
    private String status;
    private String ownerName;
    private String surveyNumber;
    private double areaInAcres;
    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private double latitude;
    private double longitude;
    private String remarks;
    private int totalPlots;
    private String approvedBy;
    private String documentPath;
    private boolean hasCourtCase;
    private String zoneClassification;
    private double setbackCompliance;
    
    // Status constants
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String STATUS_UNDER_REVIEW = "Under Review";
    public static final String STATUS_UNAUTHORIZED = "Unauthorized";
    
    // Default constructor
    public Layout() {
        this.applicationDate = LocalDate.now();
        this.hasCourtCase = false;
        this.setbackCompliance = 100.0;
    }
    
    // Basic constructor
    public Layout(String fileNumber, String layoutName, String status, String ownerName,
                  String surveyNumber, double areaInAcres, LocalDate applicationDate,
                  double latitude, double longitude, String remarks, int totalPlots) {
        this();
        this.fileNumber = fileNumber;
        this.layoutName = layoutName;
        this.status = status;
        this.ownerName = ownerName;
        this.surveyNumber = surveyNumber;
        this.areaInAcres = areaInAcres;
        this.applicationDate = applicationDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.remarks = remarks;
        this.totalPlots = totalPlots;
    }
    
    // Full constructor
    public Layout(String fileNumber, String layoutName, String status, String ownerName,
                  String surveyNumber, double areaInAcres, LocalDate applicationDate,
                  LocalDate approvalDate, double latitude, double longitude, String remarks,
                  int totalPlots, String approvedBy, String documentPath, boolean hasCourtCase,
                  String zoneClassification, double setbackCompliance) {
        this(fileNumber, layoutName, status, ownerName, surveyNumber, areaInAcres,
             applicationDate, latitude, longitude, remarks, totalPlots);
        this.approvalDate = approvalDate;
        this.approvedBy = approvedBy;
        this.documentPath = documentPath;
        this.hasCourtCase = hasCourtCase;
        this.zoneClassification = zoneClassification;
        this.setbackCompliance = setbackCompliance;
    }
    
    // Getters and Setters
    public String getFileNumber() {
        return fileNumber;
    }
    
    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }
    
    public String getLayoutName() {
        return layoutName;
    }
    
    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        if (STATUS_APPROVED.equals(status) && approvalDate == null) {
            this.approvalDate = LocalDate.now();
        }
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getSurveyNumber() {
        return surveyNumber;
    }
    
    public void setSurveyNumber(String surveyNumber) {
        this.surveyNumber = surveyNumber;
    }
    
    public double getAreaInAcres() {
        return areaInAcres;
    }
    
    public void setAreaInAcres(double areaInAcres) {
        this.areaInAcres = areaInAcres;
    }
    
    public LocalDate getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public LocalDate getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public int getTotalPlots() {
        return totalPlots;
    }
    
    public void setTotalPlots(int totalPlots) {
        this.totalPlots = totalPlots;
    }
    
    public String getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public String getDocumentPath() {
        return documentPath;
    }
    
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
    
    public boolean isHasCourtCase() {
        return hasCourtCase;
    }
    
    public void setHasCourtCase(boolean hasCourtCase) {
        this.hasCourtCase = hasCourtCase;
    }
    
    public String getZoneClassification() {
        return zoneClassification;
    }
    
    public void setZoneClassification(String zoneClassification) {
        this.zoneClassification = zoneClassification;
    }
    
    public double getSetbackCompliance() {
        return setbackCompliance;
    }
    
    public void setSetbackCompliance(double setbackCompliance) {
        this.setbackCompliance = setbackCompliance;
    }
    
    // Utility methods
    public boolean isApproved() {
        return STATUS_APPROVED.equals(status);
    }
    
    public boolean isPending() {
        return STATUS_PENDING.equals(status) || STATUS_UNDER_REVIEW.equals(status);
    }
    
    public boolean isRejected() {
        return STATUS_REJECTED.equals(status);
    }
    
    public boolean isUnauthorized() {
        return STATUS_UNAUTHORIZED.equals(status);
    }
    
    public String getStatusColor() {
        switch (status) {
            case STATUS_APPROVED:
                return "#27ae60"; // Green
            case STATUS_PENDING:
                return "#f39c12"; // Orange
            case STATUS_UNDER_REVIEW:
                return "#3498db"; // Blue
            case STATUS_REJECTED:
                return "#e74c3c"; // Red
            case STATUS_UNAUTHORIZED:
                return "#8e44ad"; // Purple
            default:
                return "#95a5a6"; // Gray
        }
    }
    
    public double getAreaInSqFt() {
        return areaInAcres * 43560; // 1 acre = 43560 sq ft
    }
    
    public String getFormattedArea() {
        if (areaInAcres >= 1) {
            return String.format("%.2f acres", areaInAcres);
        } else {
            return String.format("%.0f sq ft", getAreaInSqFt());
        }
    }
    
    public String getCoordinates() {
        return String.format("%.4f, %.4f", latitude, longitude);
    }
    
    public int getDaysFromApplication() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(applicationDate, LocalDate.now());
    }
    
    public int getDaysFromApproval() {
        if (approvalDate == null) return -1;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(approvalDate, LocalDate.now());
    }
    
    public String getProcessingTime() {
        if (approvalDate != null) {
            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(applicationDate, approvalDate);
            return days + " days";
        }
        return getDaysFromApplication() + " days (ongoing)";
    }
    
    // Validation methods
    public boolean isValidCoordinates() {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    public boolean isValidArea() {
        return areaInAcres > 0;
    }
    
    public boolean hasCompleteInformation() {
        return fileNumber != null && !fileNumber.trim().isEmpty() &&
               layoutName != null && !layoutName.trim().isEmpty() &&
               ownerName != null && !ownerName.trim().isEmpty() &&
               surveyNumber != null && !surveyNumber.trim().isEmpty() &&
               status != null && !status.trim().isEmpty() &&
               isValidArea() && isValidCoordinates();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layout layout = (Layout) o;
        return Objects.equals(fileNumber, layout.fileNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(fileNumber);
    }
    
    @Override
    public String toString() {
        return "Layout{" +
                "fileNumber='" + fileNumber + '\'' +
                ", layoutName='" + layoutName + '\'' +
                ", status='" + status + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", surveyNumber='" + surveyNumber + '\'' +
                ", areaInAcres=" + areaInAcres +
                ", applicationDate=" + applicationDate +
                ", approvalDate=" + approvalDate +
                ", totalPlots=" + totalPlots +
                ", hasCourtCase=" + hasCourtCase +
                '}';
    }
}