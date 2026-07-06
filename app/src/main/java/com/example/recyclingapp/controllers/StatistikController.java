package com.example.recyclingapp.controllers;

import com.example.recyclingapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StatistikController {
    private final FirebaseFirestore db;

    public static class Meilenstein {
        public final String titel;
        public final String beschreibung;
        public final boolean erreicht;

        public Meilenstein(String titel, String beschreibung, boolean erreicht) {
            this.titel = titel;
            this.beschreibung = beschreibung;
            this.erreicht = erreicht;
        }
    }

    public StatistikController() {
        db = FirebaseFirestore.getInstance();
    }

    public void loadUserStats(String uid, Consumer<User> onSuccess, Consumer<Exception> onFailure) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onSuccess.accept(User.fromMap(documentSnapshot.getData()));
                    } else {
                        onSuccess.accept(new User());
                    }
                })
                .addOnFailureListener(onFailure::accept);
    }

    /** Leitet die Meilensteine anhand einfacher Schwellenwerte aus den Nutzerdaten ab. */
    public List<Meilenstein> berechneMeilensteine(User user) {
        List<Meilenstein> meilensteine = new ArrayList<>();

        meilensteine.add(new Meilenstein(
                "Erster Scan",
                "Du hast erfolgreich deinen ersten Gegenstand recycelt.",
                user.getGescannteGegenstaende() >= 1));

        meilensteine.add(new Meilenstein(
                "10kg CO2 gespart",
                "Dein Beitrag entspricht etwa einem jungen Baum.",
                user.getCo2Eingespart() >= 10));

        meilensteine.add(new Meilenstein(
                "7 Tage Streak",
                "Eine Woche am Stück aktiv recycelt.",
                user.getTagesStreak() >= 7));

        meilensteine.add(new Meilenstein(
                "50 Gegenstände recycelt",
                "Du bist auf dem besten Weg zum Recycling-Profi.",
                user.getGescannteGegenstaende() >= 50));

        return meilensteine;
    }
}
