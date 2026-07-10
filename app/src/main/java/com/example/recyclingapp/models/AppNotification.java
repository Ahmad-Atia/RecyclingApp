package com.example.recyclingapp.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppNotification {
    public enum Type {
        PICKUP, IMPACT, POINTS
    }

    private String id;
    private String title;
    private String message;
    private Date timestamp;
    private Type type;
    private boolean read;
    private String extraData; // For sub-types or specific values (e.g., "50kg")

    public AppNotification() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = new Date();
        this.read = false;
    }

    public AppNotification(String title, String message, Type type) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("message", message);
        map.put("timestamp", timestamp);
        map.put("type", type.name());
        map.put("read", read);
        map.put("extraData", extraData);
        return map;
    }

    public static AppNotification fromMap(Map<String, Object> map) {
        if (map == null) return null;
        AppNotification n = new AppNotification();
        n.setId((String) map.get("id"));
        n.setTitle((String) map.get("title"));
        n.setMessage((String) map.get("message"));
        
        Object ts = map.get("timestamp");
        if (ts instanceof com.google.firebase.Timestamp) {
            n.setTimestamp(((com.google.firebase.Timestamp) ts).toDate());
        }
        
        n.setType(Type.valueOf((String) map.get("type")));
        n.setRead((Boolean) map.get("read"));
        n.setExtraData((String) map.get("extraData"));
        return n;
    }
}
