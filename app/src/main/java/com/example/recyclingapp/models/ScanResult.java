package com.example.recyclingapp.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanResult {
    private String id;
    private Date timestamp;
    private String imageUrl;
    private List<Item> detectedItems;
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

    public List<Item> getDetectedItems() { return detectedItems; }
    public void setDetectedItems(List<Item> detectedItems) { this.detectedItems = detectedItems; }

    public boolean isDepositFound() { return depositFound; }
    public void setDepositFound(boolean depositFound) { this.depositFound = depositFound; }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("timestamp", timestamp);
        result.put("imageUrl", imageUrl);
        
        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (detectedItems != null) {
            for (Item item : detectedItems) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("name", item.getName());
                itemMap.put("category", item.getCategory());
                itemsList.add(itemMap);
            }
        }
        result.put("detectedItems", itemsList);
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
        
        List<Item> items = new ArrayList<>();
        Object itemsObj = data.get("detectedItems");
        if (itemsObj instanceof List) {
            List<?> list = (List<?>) itemsObj;
            for (Object obj : list) {
                if (obj instanceof Map) {
                    Map<String, Object> itemMap = (Map<String, Object>) obj;
                    Item item = new Item();
                    item.setId((String) itemMap.get("id"));
                    item.setName((String) itemMap.get("name"));
                    item.setCategory((String) itemMap.get("category"));
                    items.add(item);
                }
            }
        }
        scanResult.setDetectedItems(items);

        Boolean deposit = (Boolean) data.get("depositFound");
        scanResult.setDepositFound(deposit != null ? deposit : false);
        return scanResult;
    }
}
