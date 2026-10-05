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

    public static Report create(String id,String reporterId,ReportTargetType type,String targetId,String reason,String description,Date now){Report v=new Report();v.id=required(id,"Report");v.reporterId=required(reporterId,"Reporter");if(type==null)throw new IllegalArgumentException("Target type is required");v.targetType=type;v.targetId=required(targetId,"Target");v.reason=required(reason,"Reason");v.description=clean(description);if(v.reason.length()>120||v.description.length()>1000)throw new IllegalArgumentException("Report text is too long");v.status=ReportStatus.PENDING;v.resolvedBy="";v.resolutionNote="";v.createdAt=copy(now==null?new Date():now);return v;}
    public static Report restore(String id,String reporterId,ReportTargetType type,String targetId,String reason,String description,ReportStatus status,String resolvedBy,String resolutionNote,Date createdAt){Report v=create(id,reporterId,type,targetId,reason,description,createdAt);v.status=status==null?ReportStatus.PENDING:status;v.resolvedBy=clean(resolvedBy);v.resolutionNote=clean(resolutionNote);return v;}

    public void startReview(String adminId){if(status!=ReportStatus.PENDING)throw new IllegalStateException("REPORT_TRANSITION_INVALID");resolvedBy=required(adminId,"Admin");status=ReportStatus.IN_REVIEW;}

    public void resolve(String adminId, String note) {
        if(status!=ReportStatus.IN_REVIEW)throw new IllegalStateException("REPORT_TRANSITION_INVALID");
        resolvedBy = adminId;
        resolutionNote = note;
        status = ReportStatus.RESOLVED;
    }

    public void reject(String adminId, String note) {
        if(status!=ReportStatus.IN_REVIEW)throw new IllegalStateException("REPORT_TRANSITION_INVALID");
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
    public Date getCreatedAt() { return copy(createdAt); }
    private static String required(String v,String f){if(v==null||v.trim().isEmpty())throw new IllegalArgumentException(f+" is required");return v.trim();}private static String clean(String v){return v==null?"":v.trim();}private static Date copy(Date v){return v==null?null:new Date(v.getTime());}
}
