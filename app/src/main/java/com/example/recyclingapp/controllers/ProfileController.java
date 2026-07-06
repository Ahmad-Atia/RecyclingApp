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

    public List<Date> fetchWasteCalendar() {
        return new ArrayList<>();
    }
}
