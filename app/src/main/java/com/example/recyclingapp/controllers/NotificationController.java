package com.example.recyclingapp.controllers;

import android.util.Log;
import com.example.recyclingapp.models.AppNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NotificationController {
    private static final String TAG = "NotificationController";
    private final FirebaseFirestore db;

    public NotificationController() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void sendNotification(String uid, AppNotification notification) {
        db.collection("users").document(uid)
                .update("notifications", FieldValue.arrayUnion(notification.toMap()))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification sent"))
                .addOnFailureListener(e -> Log.e(TAG, "Error sending notification", e));
    }

    public void fetchNotifications(String uid, Consumer<List<AppNotification>> onSuccess, Consumer<Exception> onFailure) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    List<AppNotification> list = new ArrayList<>();
                    if (doc.exists() && doc.contains("notifications")) {
                        List<Map<String, Object>> raw = (List<Map<String, Object>>) doc.get("notifications");
                        if (raw != null) {
                            for (Map<String, Object> map : raw) {
                                list.add(AppNotification.fromMap(map));
                            }
                        }
                    }
                    // Sort by timestamp (newest first)
                    list.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));
                    onSuccess.accept(list);
                })
                .addOnFailureListener(onFailure::accept);
    }
    
    public void markAllAsRead(String uid) {
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("notifications")) {
                 List<Map<String, Object>> raw = (List<Map<String, Object>>) doc.get("notifications");
                 if (raw != null) {
                     for (Map<String, Object> map : raw) {
                         map.put("read", true);
                     }
                     db.collection("users").document(uid).update("notifications", raw);
                 }
            }
        });
    }
}
