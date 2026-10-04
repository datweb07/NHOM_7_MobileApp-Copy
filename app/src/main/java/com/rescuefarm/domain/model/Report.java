package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ReportStatus;
import com.rescuefarm.domain.enums.ReportTargetType;

import java.util.Date;

public class Report {
    private String id;
    private String reporterId;
    private ReportTargetType targetType;
    private String targetId;
    private String reason;
    private String description;
    private ReportStatus status;
    private String resolvedBy;
    private String resolutionNote;
    private Date createdAt;

    public Report() { status = ReportStatus.PENDING; }

    public void resolve(String adminId, String note) {
        resolvedBy = adminId;
        resolutionNote = note;
        status = ReportStatus.RESOLVED;
    }

    public void reject(String adminId, String note) {
        resolvedBy = adminId;
        resolutionNote = note;
        status = ReportStatus.REJECTED;
    }

    public String getId() { return id; }
    public String getReporterId() { return reporterId; }
    public ReportTargetType getTargetType() { return targetType; }
    public String getTargetId() { return targetId; }
    public String getReason() { return reason; }
    public String getDescription() { return description; }
    public ReportStatus getStatus() { return status; }
    public String getResolvedBy() { return resolvedBy; }
    public String getResolutionNote() { return resolutionNote; }
    public Date getCreatedAt() { return createdAt; }
}
