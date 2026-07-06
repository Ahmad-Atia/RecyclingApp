package com.example.recyclingapp.controllers;

import android.location.Location;
import com.example.recyclingapp.models.ScanResult;
import com.example.recyclingapp.models.ScanVerlaufEintrag;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScanController {
    private FirebaseFirestore db;

    public ScanController() {
        db = FirebaseFirestore.getInstance();
    }

    public void uploadAndAnalyzeImage(File imageFile) {
        // Mock analysis for now
        ScanResult result = new ScanResult();
        result.setId(java.util.UUID.randomUUID().toString());
        result.setTimestamp(new java.util.Date());
        result.setImageUrl("mock_url");
        result.setDetectedItems(java.util.Arrays.asList("Plastikflasche", "Gelbe Tonne"));
        result.setDepositFound(true);

        db.collection("scans").add(result.toMap());
    }

    public List<Location> getDisposalPoints(String itemId) {
        // Mocked for now
        return new ArrayList<>();
    }

    /**
     * Lädt die Scan-Historie für den Scan-Verlauf-Screen.
     * TODO: sobald Scans user-spezifisch abgelegt werden (z.B. eigenes "uid"-Feld auf
     * ScanResult oder eine Subcollection unter users/{uid}), hier nach uid filtern statt
     * die komplette "scans"-Collection zu lesen.
     */
    public void fetchScanVerlauf(String uid, Consumer<List<ScanVerlaufEintrag>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("scans")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ScanVerlaufEintrag> eintraege = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        ScanResult result = ScanResult.fromMap(doc.getData());
                        eintraege.add(ScanVerlaufEintrag.fromScanResult(result));
                    }
                    onSuccess.accept(eintraege);
                })
                .addOnFailureListener(onFailure::accept);
    }
}
