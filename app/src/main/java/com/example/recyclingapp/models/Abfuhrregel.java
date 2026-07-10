package com.example.recyclingapp.models;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Wiederkehrende Abfuhr-Regel für eine Adresse (z.B. "Biotonne, alle 2 Wochen,
 * ausgehend vom 8. Januar 2026"). Aus rhythmusWochen + Ankerdatum lässt sich jeder
 * zukünftige Abholtermin berechnen, ohne dass echte Termine einzeln gespeichert werden müssen.
 */
public class Abfuhrregel {
    private String tonnenTyp;
    private int rhythmusWochen;
    private int ankerJahr;
    private int ankerMonat;
    private int ankerTag;

    public Abfuhrregel() {
        // Für Firestore
    }

    public Abfuhrregel(String tonnenTyp, int rhythmusWochen, Calendar ankerDatum) {
        this.tonnenTyp = tonnenTyp;
        this.rhythmusWochen = rhythmusWochen;
        this.ankerJahr = ankerDatum.get(Calendar.YEAR);
        this.ankerMonat = ankerDatum.get(Calendar.MONTH);
        this.ankerTag = ankerDatum.get(Calendar.DAY_OF_MONTH);
    }

    public String getTonnenTyp() { return tonnenTyp; }
    public int getRhythmusWochen() { return rhythmusWochen; }

    public Calendar getAnkerDatum() {
        Calendar cal = Calendar.getInstance();
        cal.set(ankerJahr, ankerMonat, ankerTag, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /** Prüft, ob diese Regel genau auf den übergebenen Kalendertag fällt. */
    public boolean trifftAufTag(Calendar tag) {
        long tageDiff = tageZwischen(getAnkerDatum(), tag);
        if (tageDiff < 0) return false;
        long rhythmusTage = rhythmusWochen * 7L;
        return tageDiff % rhythmusTage == 0;
    }

    /** Nächster Termin dieser Regel am oder nach 'ab'. */
    public Calendar naechsterTerminAb(Calendar ab) {
        long rhythmusTage = rhythmusWochen * 7L;
        long tageDiff = tageZwischen(getAnkerDatum(), ab);
        long schritte = tageDiff <= 0 ? 0 : (long) Math.ceil(tageDiff / (double) rhythmusTage);

        Calendar termin = getAnkerDatum();
        termin.add(Calendar.DAY_OF_MONTH, (int) (schritte * rhythmusTage));
        return termin;
    }

    /** Anzahl volle Kalendertage zwischen zwei Terminen, unabhängig von Uhrzeit/DST. */
    private static long tageZwischen(Calendar von, Calendar bis) {
        Calendar a = mitternacht(von);
        Calendar b = mitternacht(bis);
        return Math.round((b.getTimeInMillis() - a.getTimeInMillis()) / 86400000.0);
    }

    private static Calendar mitternacht(Calendar quelle) {
        Calendar kopie = (Calendar) quelle.clone();
        kopie.set(Calendar.HOUR_OF_DAY, 0);
        kopie.set(Calendar.MINUTE, 0);
        kopie.set(Calendar.SECOND, 0);
        kopie.set(Calendar.MILLISECOND, 0);
        return kopie;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tonnenTyp", tonnenTyp);
        result.put("rhythmusWochen", rhythmusWochen);
        result.put("ankerJahr", ankerJahr);
        result.put("ankerMonat", ankerMonat);
        result.put("ankerTag", ankerTag);
        return result;
    }

    public static Abfuhrregel fromMap(Map<String, Object> data) {
        if (data == null) return null;
        Object tonnenTyp = data.get("tonnenTyp");
        String tonnenTypBereinigt = tonnenTyp instanceof String ? ((String) tonnenTyp).trim() : "";
        if (tonnenTypBereinigt.isEmpty()) {
            // Unvollständiger/fehlerhafter Eintrag (z.B. Tippfehler bei manueller Firestore-Eingabe) - ignorieren.
            return null;
        }
        Abfuhrregel regel = new Abfuhrregel();
        regel.tonnenTyp = tonnenTypBereinigt;
        regel.rhythmusWochen = intWert(data.get("rhythmusWochen"), 2);
        regel.ankerJahr = intWert(data.get("ankerJahr"), Calendar.getInstance().get(Calendar.YEAR));
        regel.ankerMonat = intWert(data.get("ankerMonat"), 0);
        regel.ankerTag = intWert(data.get("ankerTag"), 1);
        return regel;
    }

    private static int intWert(Object value, int fallback) {
        return value instanceof Number ? ((Number) value).intValue() : fallback;
    }
}
