package com.example.recyclingapp.models;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScanVerlaufEintrag {
    private final String titel;
    private final String zeitangabe;
    private final String kategorie;

    public ScanVerlaufEintrag(String titel, String zeitangabe, String kategorie) {
        this.titel = titel;
        this.zeitangabe = zeitangabe;
        this.kategorie = kategorie;
    }

    public String getTitel() { return titel; }
    public String getZeitangabe() { return zeitangabe; }
    public String getKategorie() { return kategorie; }

    /**
     * Übersetzt ein ScanResult (Scan-Feature) in einen anzeigbaren Verlaufs-Eintrag.
     * Die Kategorie-Erkennung ist ein Platzhalter-Heuristik über detectedItems, solange
     * ScanResult kein eigenes Tonnentyp-Feld hat - anpassen, sobald das Scan-Feature
     * eine feste Kategorie liefert (z.B. ein zusätzliches Feld auf ScanResult).
     */
    public static ScanVerlaufEintrag fromScanResult(ScanResult result) {
        String titel = "Gescannter Gegenstand";
        String kategorie = "SONSTIGES";

        List<String> detectedItems = result.getDetectedItems();
        if (detectedItems != null) {
            for (String item : detectedItems) {
                String erkannteKategorie = kategorieAusText(item);
                if (erkannteKategorie != null) {
                    kategorie = erkannteKategorie;
                } else {
                    titel = item;
                }
            }
        }

        String zeitangabe = result.getTimestamp() != null
                ? new SimpleDateFormat("EEEE, HH:mm", Locale.GERMANY).format(result.getTimestamp())
                : "";

        return new ScanVerlaufEintrag(titel, zeitangabe, kategorie);
    }

    private static String kategorieAusText(String text) {
        String t = text.toLowerCase(Locale.GERMANY);
        if (t.contains("gelb")) return "GELBER SACK";
        if (t.contains("papier")) return "PAPIERTONNE";
        if (t.contains("bio")) return "BIOTONNE";
        if (t.contains("restmüll") || t.contains("restabfall")) return "RESTMÜLL";
        return null;
    }
}
