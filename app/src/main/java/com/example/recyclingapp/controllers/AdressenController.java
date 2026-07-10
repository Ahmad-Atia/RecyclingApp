package com.example.recyclingapp.controllers;

import com.example.recyclingapp.models.Adresse;
import com.example.recyclingapp.models.AppNotification;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Adressen werden als Array-Feld im vorhandenen users/{uid}-Dokument gespeichert,
 * statt in einer eigenen Subcollection. Firestore-Regeln erlauben bislang nur
 * Zugriff auf das User-Dokument selbst, nicht auf Subcollections darunter.
 */
public class AdressenController {
    private final FirebaseFirestore db;

    public AdressenController() {
        db = FirebaseFirestore.getInstance();
    }

    private DocumentReference userRef(String uid) {
        return db.collection("users").document(uid);
    }

    public void fetchAdressen(String uid, Consumer<List<Adresse>> onSuccess, Consumer<Exception> onFailure) {
        userRef(uid).get()
                .addOnSuccessListener(doc -> onSuccess.accept(leseAdressen(doc)))
                .addOnFailureListener(onFailure::accept);
    }

    public void addAdresse(String uid, Adresse neueAdresse, Consumer<String> onSuccess, Consumer<Exception> onFailure) {
        neueAdresse.setId(UUID.randomUUID().toString());
        userRef(uid).get()
                .addOnSuccessListener(doc -> {
                    List<Adresse> adressen = leseAdressen(doc);
                    adressen.add(neueAdresse);
                    speichereAdressen(uid, adressen, () -> onSuccess.accept(neueAdresse.getId()), onFailure);
                })
                .addOnFailureListener(onFailure::accept);
    }

    public void updateAdresse(String uid, Adresse geaenderteAdresse, Runnable onSuccess, Consumer<Exception> onFailure) {
        userRef(uid).get()
                .addOnSuccessListener(doc -> {
                    List<Adresse> adressen = leseAdressen(doc);
                    for (int i = 0; i < adressen.size(); i++) {
                        if (adressen.get(i).getId().equals(geaenderteAdresse.getId())) {
                            adressen.set(i, geaenderteAdresse);
                            break;
                        }
                    }
                    speichereAdressen(uid, adressen, onSuccess, onFailure);
                })
                .addOnFailureListener(onFailure::accept);
    }

    public void deleteAdresse(String uid, String adresseId, Runnable onSuccess, Consumer<Exception> onFailure) {
        userRef(uid).get()
                .addOnSuccessListener(doc -> {
                    List<Adresse> adressen = leseAdressen(doc);
                    adressen.removeIf(a -> a.getId().equals(adresseId));
                    speichereAdressen(uid, adressen, onSuccess, onFailure);
                })
                .addOnFailureListener(onFailure::accept);
    }

    /** Setzt die übergebene Adresse als Standard und alle anderen zurück. */
    public void setAlsStandard(String uid, List<Adresse> alleAdressen, String neueStandardId, Runnable onSuccess, Consumer<Exception> onFailure) {
        for (Adresse adresse : alleAdressen) {
            adresse.setIstStandard(adresse.getId().equals(neueStandardId));
        }
        speichereAdressen(uid, alleAdressen, onSuccess, onFailure);
    }

    @SuppressWarnings("unchecked")
    private List<Adresse> leseAdressen(DocumentSnapshot doc) {
        List<Adresse> ergebnis = new ArrayList<>();
        List<Map<String, Object>> rohdaten = (List<Map<String, Object>>) doc.get("adressen");
        if (rohdaten != null) {
            for (Map<String, Object> eintrag : rohdaten) {
                ergebnis.add(Adresse.fromMap(eintrag));
            }
        }
        return ergebnis;
    }

    private void speichereAdressen(String uid, List<Adresse> adressen, Runnable onSuccess, Consumer<Exception> onFailure) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Adresse adresse : adressen) {
            maps.add(adresse.toMap());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("adressen", maps);
        
        // Wenn eine Adresse geändert wurde, eine Benachrichtigung senden
        AppNotification notification = new AppNotification(
            "Adresse aktualisiert",
            "Deine Abholtermine werden nun für die neue Adresse berechnet.",
            AppNotification.Type.PICKUP
        );
        
        userRef(uid).set(data, SetOptions.merge())
                .addOnSuccessListener(v -> {
                    userRef(uid).update("notifications", FieldValue.arrayUnion(notification.toMap()));
                    onSuccess.run();
                })
                .addOnFailureListener(onFailure::accept);
    }
}
