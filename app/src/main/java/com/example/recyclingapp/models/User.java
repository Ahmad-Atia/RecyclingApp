package com.example.recyclingapp.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String email;
    private String address;
    private int ecoScore;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String email, String address, int ecoScore) {
        this.uid = uid;
        this.email = email;
        this.address = address;
        this.ecoScore = ecoScore;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getEcoScore() { return ecoScore; }
    public void setEcoScore(int ecoScore) { this.ecoScore = ecoScore; }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("address", address);
        result.put("ecoScore", ecoScore);
        return result;
    }

    public static User fromMap(Map<String, Object> data) {
        if (data == null) return null;
        User user = new User();
        user.setUid((String) data.get("uid"));
        user.setEmail((String) data.get("email"));
        user.setAddress((String) data.get("address"));
        Long score = (Long) data.get("ecoScore");
        user.setEcoScore(score != null ? score.intValue() : 0);
        return user;
    }
}
