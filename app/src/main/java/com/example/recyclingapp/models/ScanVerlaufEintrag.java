package com.example.recyclingapp.models;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScanVerlaufEintrag {
    private final String id;
    private final String titel;
    private final String zeitangabe;
    private final String kategorie;

    public ScanVerlaufEintrag(String id, String titel, String zeitangabe, String kategorie) {
        this.id = id;
        this.titel = titel;
        this.zeitangabe = zeitangabe;
        this.kategorie = kategorie;
    }

    public String getId() { return id; }
    public String getTitel() { return titel; }
    public String getZeitangabe() { return zeitangabe; }
    public String getKategorie() { return kategorie; }

    /**
     * Übersetzt ein ScanResult (Scan-Feature) in einen anzeigbaren Verlaufs-Eintrag.
     */
    public static ScanVerlaufEintrag fromScanResult(ScanResult result) {
        String id = result.getId();
        String titel = "Gescannter Gegenstand";
        String kategorie = "SONSTIGES";

        List<Item> detectedItems = result.getDetectedItems();
        if (detectedItems != null && !detectedItems.isEmpty()) {
            titel = detectedItems.get(0).getName();
            kategorie = detectedItems.get(0).getCategory().toUpperCase();
        }

        String zeitangabe = result.getTimestamp() != null
                ? new SimpleDateFormat("EEEE, HH:mm", Locale.GERMANY).format(result.getTimestamp())
                : "";

        return new ScanVerlaufEintrag(id, titel, zeitangabe, kategorie);
    }
}
