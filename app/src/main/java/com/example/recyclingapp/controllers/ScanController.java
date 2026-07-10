package com.example.recyclingapp.controllers;

import android.util.Base64;
import android.util.Log;

import com.example.recyclingapp.BuildConfig;
import com.example.recyclingapp.models.DisposalPointsManager;
import com.example.recyclingapp.models.Item;
import com.example.recyclingapp.models.ScanResult;
import com.example.recyclingapp.models.ScanVerlaufEintrag;
import com.example.recyclingapp.network.MistralApiService;
import com.example.recyclingapp.network.MistralClient;
import com.example.recyclingapp.network.MistralMessage;
import com.example.recyclingapp.network.MistralRequest;
import com.example.recyclingapp.network.MistralResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanController {
    private static final String TAG = "ScanController";
    private FirebaseFirestore db;
    private Gson gson;

    public interface ScanCallback {
        void onScanCompleted(ScanResult result);
        void onScanFailed(Exception e);
    }

    public ScanController() {
        db = FirebaseFirestore.getInstance();
        gson = new Gson();
    }

    public void uploadAndAnalyzeImage(File imageFile, ScanCallback callback) {
        String base64Image = encodeFileToBase64(imageFile);
        if (base64Image == null) {
            callback.onScanFailed(new Exception("Failed to encode image"));
            return;
        }

        MistralApiService service = MistralClient.getService();
        String apiKey = "Bearer " + BuildConfig.MISTRAL_API_KEY;

        List<MistralMessage.ContentPart> contentParts = new ArrayList<>();
        contentParts.add(new MistralMessage.ContentPart("text", 
            "Analysiere dieses Bild und identifiziere Müll-Items. " +
            "Antworte NUR mit einem JSON-Array von Objekten im folgenden Format: " +
            "[{\"id\": \"uuid\", \"name\": \"item_name\", \"category\": \"entsorgungskategorie\"}]. " +
            "Kategorien sollten sein: Gelbe Tonne, Papiertonne, Restmüll, Bioabfall, Glasmüll oder Sperrmüll. " +
            "Wenn kein Müll erkannt wird, antworte mit einem leeren Array []."));
        
        contentParts.add(new MistralMessage.ContentPart("image_url", 
            new MistralMessage.ImageUrl("data:image/jpeg;base64," + base64Image)));

        MistralMessage message = new MistralMessage("user", contentParts);
        MistralRequest request = new MistralRequest("pixtral-12b-2409", Collections.singletonList(message));
        request.setResponseFormat(new MistralRequest.ResponseFormat("json_object"));

        service.chat(apiKey, request).enqueue(new Callback<MistralResponse>() {
            @Override
            public void onResponse(Call<MistralResponse> call, Response<MistralResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().choices.isEmpty()) {
                    String jsonResponse = response.body().choices.get(0).message.getContent();
                    Log.d(TAG, "Mistral Response: " + jsonResponse);
                    processAnalysisResult(jsonResponse, callback);
                } else {
                    Log.e(TAG, "Mistral API Error: " + response.code());
                    callback.onScanFailed(new Exception("API Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<MistralResponse> call, Throwable t) {
                Log.e(TAG, "Mistral Network Error", t);
                callback.onScanFailed(new Exception(t));
            }
        });
    }

    private void processAnalysisResult(String json, ScanCallback callback) {
        try {
            // Mistral might return the list directly or wrapped in an object depending on the prompt
            List<Item> items;
            if (json.trim().startsWith("{")) {
                // Handle case where it's an object with a field
                java.util.Map<String, List<Item>> map = gson.fromJson(json, new TypeToken<java.util.Map<String, List<Item>>>(){}.getType());
                items = map.values().iterator().next();
            } else {
                items = gson.fromJson(json, new TypeToken<List<Item>>(){}.getType());
            }

            if (items == null) items = new ArrayList<>();

            ScanResult result = new ScanResult();
            result.setId(UUID.randomUUID().toString());
            result.setTimestamp(new Date());
            result.setDetectedItems(items);
            result.setImageUrl(""); // TODO: Upload to Firebase Storage if needed
            result.setDepositFound(checkForDeposit(items));

            saveScanToFirestore(result, callback);
        } catch (Exception e) {
            Log.e(TAG, "Parsing error", e);
            callback.onScanFailed(e);
        }
    }

    private boolean checkForDeposit(List<Item> items) {
        for (Item item : items) {
            if (item.getName().toLowerCase().contains("pfand") || 
                item.getName().toLowerCase().contains("flasche")) {
                return true;
            }
        }
        return false;
    }

    private void saveScanToFirestore(ScanResult result, ScanCallback callback) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            callback.onScanFailed(new Exception("User not logged in"));
            return;
        }

        double co2Saved = calculateCo2Savings(result.getDetectedItems());

        // Speichern als Array-Eintrag im User-Dokument (statt Subcollection),
        // da die Firestore-Regeln aktuell nur Zugriff auf das User-Dokument erlauben.
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("scans", FieldValue.arrayUnion(result.toMap()));
        updates.put("gescannteGegenstaende", FieldValue.increment(1));
        updates.put("ecoScore", FieldValue.increment(10)); // 10 Punkte pro Scan
        updates.put("co2Eingespart", FieldValue.increment(co2Saved));

        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Scan saved to user document");
                    callback.onScanCompleted(result);
                })
                .addOnFailureListener(e -> {
                    // Falls update() fehlschlägt (z.B. Dokument existiert noch nicht), versuchen wir set() mit merge
                    db.collection("users").document(uid)
                            .set(updates, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Scan saved to user document via set/merge");
                                callback.onScanCompleted(result);
                            })
                            .addOnFailureListener(e2 -> {
                                Log.e(TAG, "Error saving scan", e2);
                                callback.onScanFailed(e2);
                            });
                });
    }

    private double calculateCo2Savings(List<Item> items) {
        double total = 0;
        if (items == null) return 0;
        
        for (Item item : items) {
            String cat = item.getCategory().toLowerCase();
            if (cat.contains("glas")) {
                total += 0.5;
            } else if (cat.contains("gelb") || cat.contains("plastik") || cat.contains("metall")) {
                total += 0.4;
            } else if (cat.contains("papier")) {
                total += 0.3;
            } else if (cat.contains("bio")) {
                total += 0.1;
            } else if (cat.contains("pfand")) {
                total += 0.2;
            }
        }
        return total;
    }

    private String encodeFileToBase64(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IOException e) {
            Log.e(TAG, "Base64 encoding failed", e);
            return null;
        }
    }

    public void fetchScanVerlauf(String uid, Consumer<List<ScanVerlaufEintrag>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    List<ScanVerlaufEintrag> eintraege = new ArrayList<>();
                    if (doc.exists()) {
                        Object scansObj = doc.get("scans");
                        if (scansObj instanceof List) {
                            List<Map<String, Object>> scansList = (List<Map<String, Object>>) scansObj;
                            // In umgekehrter Reihenfolge (neueste zuerst)
                            java.util.Collections.reverse(scansList);
                            for (Map<String, Object> scanData : scansList) {
                                ScanResult res = ScanResult.fromMap(scanData);
                                eintraege.add(ScanVerlaufEintrag.fromScanResult(res));
                            }
                        }
                    }
                    onSuccess.accept(eintraege);
                })
                .addOnFailureListener(onFailure::accept);
    }
}
