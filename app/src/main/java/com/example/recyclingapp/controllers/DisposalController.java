package com.example.recyclingapp.controllers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.example.recyclingapp.models.Adresse;
import com.example.recyclingapp.models.DisposalPoint;
import com.example.recyclingapp.models.DisposalPointsManager;
import com.example.recyclingapp.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DisposalController {
    private final AdressenController adressenController;
    private final Context context;

    public DisposalController(Context context) {
        this.context = context;
        this.adressenController = new AdressenController();
    }

    public void fetchDisposalPointsForCurrentUser(DisposalPointsManager.PointsCallback callback) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            callback.onError("Bitte melde dich an, um Entsorgungsstellen zu sehen.");
            return;
        }

        adressenController.fetchAdressen(uid, adressen -> {
            if (adressen == null || adressen.isEmpty()) {
                callback.onError("Bitte hinterlege eine Adresse in deinem Profil.");
                return;
            }

            // Standardadresse finden oder die erste aus der Liste nehmen
            Adresse standard = null;
            for (Adresse a : adressen) {
                if (a.isIstStandard()) {
                    standard = a;
                    break;
                }
            }
            if (standard == null) standard = adressen.get(0);

            // Adresse in Koordinaten umwandeln und Punkte laden
            geocodeAndFetch(standard.getAdresse(), callback);

        }, e -> callback.onError("Fehler beim Laden der Adressen: " + e.getMessage()));
    }

    private void geocodeAndFetch(String addressStr, DisposalPointsManager.PointsCallback callback) {
        if (!NetworkUtils.isOnline(context)) {
            callback.onError("Internetverbindung erforderlich für die Suche nach Entsorgungsstellen.");
            return;
        }
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                // Geocoding ist ein blockierender Netzwerkaufruf, daher im eigenen Thread
                List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    double userLat = address.getLatitude();
                    double userLon = address.getLongitude();

                    new DisposalPointsManager().fetchPoints(userLat, userLon, new DisposalPointsManager.PointsCallback() {
                        @Override
                        public void onSuccess(List<DisposalPoint> points) {
                            if (points != null && !points.isEmpty()) {
                                // 1. Distanz für jeden Punkt berechnen
                                float[] results = new float[1];
                                for (DisposalPoint p : points) {
                                    if (p.getLocation() != null) {
                                        android.location.Location.distanceBetween(
                                                userLat, userLon,
                                                p.getLocation().getLatitude(), p.getLocation().getLongitude(),
                                                results
                                        );
                                        p.setDistance(results[0]);
                                    }
                                }

                                // 2. Nach Distanz sortieren
                                points.sort(Comparator.comparingDouble(DisposalPoint::getDistance));

                                // 3. Nur die ersten zwei Stellen nehmen
                                List<DisposalPoint> topTwo = points.subList(0, Math.min(2, points.size()));
                                callback.onSuccess(topTwo);
                            } else {
                                callback.onSuccess(points);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            callback.onError(error);
                        }
                    });
                } else {
                    callback.onError("Adresse konnte nicht gefunden werden: " + addressStr);
                }
            } catch (IOException e) {
                callback.onError("Netzwerkfehler beim Geocoding: " + e.getMessage());
            }
        }).start();
    }
}
