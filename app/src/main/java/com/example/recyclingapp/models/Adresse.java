package com.example.recyclingapp.models;

import java.util.HashMap;
import java.util.Map;

public class Adresse {
    private String id;
    private String label;
    private String adresse;
    private boolean istStandard;

    public Adresse() {
        // Required for Firestore
    }

    public Adresse(String label, String adresse, boolean istStandard) {
        this.label = label;
        this.adresse = adresse;
        this.istStandard = istStandard;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public boolean isIstStandard() { return istStandard; }
    public void setIstStandard(boolean istStandard) { this.istStandard = istStandard; }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("label", label);
        result.put("adresse", adresse);
        result.put("istStandard", istStandard);
        return result;
    }

    public static Adresse fromMap(Map<String, Object> data) {
        if (data == null) return null;
        Adresse adresse = new Adresse();
        adresse.setId((String) data.get("id"));
        adresse.setLabel((String) data.get("label"));
        adresse.setAdresse((String) data.get("adresse"));
        Boolean standard = (Boolean) data.get("istStandard");
        adresse.setIstStandard(standard != null && standard);
        return adresse;
    }
}
