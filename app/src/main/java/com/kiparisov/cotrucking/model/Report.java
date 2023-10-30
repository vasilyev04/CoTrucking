package com.kiparisov.cotrucking.model;

public class Report {
    private long timestamp;
    private String reportFlag;
    private String description;
    private String adKey;
    private String ownerAdKey;
    private String ownerReportKey;


    public Report(long timestamp, String reportFlag, String description, String adKey, String ownerAdKey,
                  String ownerReportKey) {
        this.timestamp = timestamp;
        this.reportFlag = reportFlag;
        this.description = description;
        this.adKey = adKey;
        this.ownerAdKey = ownerAdKey;
        this.ownerReportKey = ownerReportKey;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReportFlag() {
        return reportFlag;
    }

    public void setReportFlag(String reportFlag) {
        this.reportFlag = reportFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdKey() {
        return adKey;
    }

    public void setAdKey(String adKey) {
        this.adKey = adKey;
    }

    public String getOwnerAdKey() {
        return ownerAdKey;
    }

    public void setOwnerAdKey(String ownerKey) {
        this.ownerAdKey = ownerKey;
    }

    public String getOwnerReportKey() {
        return ownerReportKey;
    }

    public void setOwnerReportKey(String ownerReportKey) {
        this.ownerReportKey = ownerReportKey;
    }
}
