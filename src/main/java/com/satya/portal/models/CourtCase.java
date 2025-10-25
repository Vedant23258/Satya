package com.satya.portal.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CourtCase model class representing legal cases related to layouts
 */
public class CourtCase {
    private String caseId;
    private String caseTitle;
    private String caseType;
    private String status;
    private LocalDate filingDate;
    private LocalDate hearingDate;
    private LocalDate judgmentDate;
    private String description;
    private String relatedLayoutId;
    private String courtName;
    private String judgeName;
    private String petitioner;
    private String respondent;
    private String caseNumber;
    private String outcome;
    private String documentPath;
    
    // Case status constants
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CLOSED = "Closed";
    public static final String STATUS_DISMISSED = "Dismissed";
    public static final String STATUS_WITHDRAWN = "Withdrawn";
    
    // Case type constants
    public static final String TYPE_CIVIL = "Civil";
    public static final String TYPE_CRIMINAL = "Criminal";
    public static final String TYPE_REVENUE = "Revenue";
    public static final String TYPE_WRIT = "Writ Petition";
    
    // Default constructor
    public CourtCase() {
        this.filingDate = LocalDate.now();
        this.status = STATUS_PENDING;
    }
    
    // Basic constructor
    public CourtCase(String caseId, String caseTitle, String caseType, String status,
                     LocalDate filingDate, LocalDate hearingDate, String description,
                     String relatedLayoutId) {
        this();
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.caseType = caseType;
        this.status = status;
        this.filingDate = filingDate;
        this.hearingDate = hearingDate;
        this.description = description;
        this.relatedLayoutId = relatedLayoutId;
    }
    
    // Getters and Setters
    public String getCaseId() {
        return caseId;
    }
    
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
    
    public String getCaseTitle() {
        return caseTitle;
    }
    
    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }
    
    public String getCaseType() {
        return caseType;
    }
    
    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDate getFilingDate() {
        return filingDate;
    }
    
    public void setFilingDate(LocalDate filingDate) {
        this.filingDate = filingDate;
    }
    
    public LocalDate getHearingDate() {
        return hearingDate;
    }
    
    public void setHearingDate(LocalDate hearingDate) {
        this.hearingDate = hearingDate;
    }
    
    public LocalDate getJudgmentDate() {
        return judgmentDate;
    }
    
    public void setJudgmentDate(LocalDate judgmentDate) {
        this.judgmentDate = judgmentDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRelatedLayoutId() {
        return relatedLayoutId;
    }
    
    public void setRelatedLayoutId(String relatedLayoutId) {
        this.relatedLayoutId = relatedLayoutId;
    }
    
    public String getCourtName() {
        return courtName;
    }
    
    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }
    
    public String getJudgeName() {
        return judgeName;
    }
    
    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
    }
    
    public String getPetitioner() {
        return petitioner;
    }
    
    public void setPetitioner(String petitioner) {
        this.petitioner = petitioner;
    }
    
    public String getRespondent() {
        return respondent;
    }
    
    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }
    
    public String getCaseNumber() {
        return caseNumber;
    }
    
    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }
    
    public String getOutcome() {
        return outcome;
    }
    
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    
    public String getDocumentPath() {
        return documentPath;
    }
    
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
    
    // Utility methods
    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
    
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }
    
    public boolean isClosed() {
        return STATUS_CLOSED.equals(status) || STATUS_DISMISSED.equals(status) || STATUS_WITHDRAWN.equals(status);
    }
    
    public int getDaysFromFiling() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(filingDate, LocalDate.now());
    }
    
    public int getDaysToHearing() {
        if (hearingDate == null) return -1;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), hearingDate);
    }
    
    public String getStatusColor() {
        switch (status) {
            case STATUS_ACTIVE:
                return "#e74c3c"; // Red
            case STATUS_PENDING:
                return "#f39c12"; // Orange
            case STATUS_CLOSED:
                return "#27ae60"; // Green
            case STATUS_DISMISSED:
                return "#95a5a6"; // Gray
            case STATUS_WITHDRAWN:
                return "#9b59b6"; // Purple
            default:
                return "#34495e"; // Dark gray
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtCase courtCase = (CourtCase) o;
        return Objects.equals(caseId, courtCase.caseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(caseId);
    }
    
    @Override
    public String toString() {
        return "CourtCase{" +
                "caseId='" + caseId + '\'' +
                ", caseTitle='" + caseTitle + '\'' +
                ", caseType='" + caseType + '\'' +
                ", status='" + status + '\'' +
                ", filingDate=" + filingDate +
                ", hearingDate=" + hearingDate +
                ", relatedLayoutId='" + relatedLayoutId + '\'' +
                '}';
    }
}

/**
 * Feedback model class representing user feedback
 */
class Feedback {
    private String feedbackId;
    private String userId;
    private String message;
    private String type;
    private LocalDateTime submittedDate;
    private boolean isResolved;
    private String response;
    private String category;
    private int rating;
    
    // Feedback type constants
    public static final String TYPE_POSITIVE = "Positive";
    public static final String TYPE_NEGATIVE = "Negative";
    public static final String TYPE_SUGGESTION = "Suggestion";
    public static final String TYPE_BUG_REPORT = "Bug Report";
    
    public Feedback() {
        this.submittedDate = LocalDateTime.now();
        this.isResolved = false;
        this.rating = 0;
    }
    
    public Feedback(String feedbackId, String userId, String message, String type,
                    LocalDateTime submittedDate, boolean isResolved) {
        this();
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.submittedDate = submittedDate;
        this.isResolved = isResolved;
    }
    
    // Getters and Setters
    public String getFeedbackId() {
        return feedbackId;
    }
    
    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }
    
    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }
    
    public boolean isResolved() {
        return isResolved;
    }
    
    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating)); // Ensure rating is between 1-5
    }
}

/**
 * Violation model class representing security violations
 */
class Violation {
    private String violationId;
    private String userId;
    private String violationType;
    private LocalDateTime timestamp;
    private String documentId;
    private String description;
    private String ipAddress;
    private String userAgent;
    private boolean isBlocked;
    
    // Violation type constants
    public static final String TYPE_SCREENSHOT = "Screenshot Attempt";
    public static final String TYPE_PRINT = "Print Attempt";
    public static final String TYPE_COPY = "Copy Attempt";
    public static final String TYPE_RIGHT_CLICK = "Right Click";
    public static final String TYPE_DRAG_DROP = "Drag Drop";
    public static final String TYPE_TAB_SWITCH = "Tab Switch";
    
    public Violation() {
        this.timestamp = LocalDateTime.now();
        this.isBlocked = false;
    }
    
    public Violation(String violationId, String userId, String violationType,
                     LocalDateTime timestamp, String documentId, String description) {
        this();
        this.violationId = violationId;
        this.userId = userId;
        this.violationType = violationType;
        this.timestamp = timestamp;
        this.documentId = documentId;
        this.description = description;
    }
    
    // Getters and Setters
    public String getViolationId() {
        return violationId;
    }
    
    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getViolationType() {
        return violationType;
    }
    
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public boolean isBlocked() {
        return isBlocked;
    }
    
    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}