package com.example.recyclingapp.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String email;
    private String name;
    private String address;
    private int ecoScore;
    private double co2Eingespart;
    private int gescannteGegenstaende;
    private int tagesStreak;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String email, String name, String address, int ecoScore) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.address = address;
        this.ecoScore = ecoScore;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getEcoScore() { return ecoScore; }
    public void setEcoScore(int ecoScore) { this.ecoScore = ecoScore; }

    public double getCo2Eingespart() { return co2Eingespart; }
    public void setCo2Eingespart(double co2Eingespart) { this.co2Eingespart = co2Eingespart; }

    public int getGescannteGegenstaende() { return gescannteGegenstaende; }
    public void setGescannteGegenstaende(int gescannteGegenstaende) { this.gescannteGegenstaende = gescannteGegenstaende; }

    public int getTagesStreak() { return tagesStreak; }
    public void setTagesStreak(int tagesStreak) { this.tagesStreak = tagesStreak; }

    /** Umweltheld-Level, abgeleitet aus den Eco-Punkten (alle 200 Punkte ein Level). */
    public int getUmweltheldLevel() {
        return 1 + (ecoScore / 200);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("name", name);
        result.put("address", address);
        result.put("ecoScore", ecoScore);
        result.put("co2Eingespart", co2Eingespart);
        result.put("gescannteGegenstaende", gescannteGegenstaende);
        result.put("tagesStreak", tagesStreak);
        return result;
    }

    public static User fromMap(Map<String, Object> data) {
        if (data == null) return null;
        User user = new User();
        user.setUid((String) data.get("uid"));
        user.setEmail((String) data.get("email"));
        user.setName((String) data.get("name"));
        user.setAddress((String) data.get("address"));

        Long score = (Long) data.get("ecoScore");
        user.setEcoScore(score != null ? score.intValue() : 0);

        Number co2 = (Number) data.get("co2Eingespart");
        user.setCo2Eingespart(co2 != null ? co2.doubleValue() : 0);

        Long scanned = (Long) data.get("gescannteGegenstaende");
        user.setGescannteGegenstaende(scanned != null ? scanned.intValue() : 0);

        Long streak = (Long) data.get("tagesStreak");
        user.setTagesStreak(streak != null ? streak.intValue() : 0);

        return user;
    }
}
