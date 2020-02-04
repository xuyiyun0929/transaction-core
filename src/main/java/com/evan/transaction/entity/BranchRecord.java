package com.evan.transaction.entity;

public class BranchRecord {
    private String branchId;
    private String branchServiceUrl;

    public BranchRecord() {
    }

    public BranchRecord(String branchId, String branchServiceUrl) {
        this.branchId = branchId;
        this.branchServiceUrl = branchServiceUrl;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchServiceUrl() {
        return branchServiceUrl;
    }

    public void setBranchServiceUrl(String branchServiceUrl) {
        this.branchServiceUrl = branchServiceUrl;
    }
}
