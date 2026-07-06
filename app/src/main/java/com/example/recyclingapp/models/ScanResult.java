package com.example.recyclingapp.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanResult {
    private String id;
    private Date timestamp;
    private String imageUrl;
    private List<String> detectedItems;
    private boolean depositFound;

    public ScanResult() {
        // Required for Firestore
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getDetectedItems() { return detectedItems; }
    public void setDetectedItems(List<String> detectedItems) { this.detectedItems = detectedItems; }

    public boolean isDepositFound() { return depositFound; }
    public void setDepositFound(boolean depositFound) { this.depositFound = depositFound; }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("timestamp", timestamp);
        result.put("imageUrl", imageUrl);
        result.put("detectedItems", detectedItems);
        result.put("depositFound", depositFound);
        return result;
    }

    public static ScanResult fromMap(Map<String, Object> data) {
        if (data == null) return null;
        ScanResult scanResult = new ScanResult();
        scanResult.setId((String) data.get("id"));

        Object rohZeitstempel = data.get("timestamp");
        if (rohZeitstempel instanceof com.google.firebase.Timestamp) {
            scanResult.setTimestamp(((com.google.firebase.Timestamp) rohZeitstempel).toDate());
        } else if (rohZeitstempel instanceof Date) {
            scanResult.setTimestamp((Date) rohZeitstempel);
        }

        scanResult.setImageUrl((String) data.get("imageUrl"));
        scanResult.setDetectedItems((List<String>) data.get("detectedItems"));
        Boolean deposit = (Boolean) data.get("depositFound");
        scanResult.setDepositFound(deposit != null ? deposit : false);
        return scanResult;
    }
}
