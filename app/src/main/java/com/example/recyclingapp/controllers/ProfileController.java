package com.example.recyclingapp.controllers;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileController {
    private FirebaseFirestore db;

    public ProfileController() {
        db = FirebaseFirestore.getInstance();
    }

    public void updateAddress(String uid, String newAddress) {
        db.collection("users").document(uid)
                .update("address", newAddress);
    }

    public List<Date> fetchWasteCalendar() {
        // Mocked logic to fetch dates from Firestore
        return new ArrayList<>();
    }
}
