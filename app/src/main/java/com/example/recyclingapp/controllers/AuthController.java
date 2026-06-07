package com.example.recyclingapp.controllers;

import com.example.recyclingapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthController {
    private FirebaseAuth mAuth;

    private volatile FirebaseFirestore db;

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public AuthController() {
        mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    private FirebaseFirestore getDb() {
        if (this.db == null) {
            this.db = FirebaseFirestore.getInstance();
        }
        return this.db;
    }

    public void login(String email, String pass, AuthCallback callback) {
        if (email.isEmpty() || pass.isEmpty()) {
            callback.onFailure("Email and password cannot be empty");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("E-Mail oder Passwort ist falsch.");
                    }
                });
    }

    public void register(String name, String email, String password, AuthCallback callback) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            callback.onFailure("Bitte alle Felder ausfüllen");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        User newUser = new User(uid, email, name, "Keine Adresse", 0);

                        getDb().collection("users").document(uid)
                                .set(newUser.toMap())
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure("Datenbank-Fehler: " + e.getMessage()));
                    } else {
                        Exception e = task.getException();
                        if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            callback.onFailure("Diese E-Mail ist bereits registriert.");
                        } else {
                            callback.onFailure("Fehler: " + e.getMessage());
                        }
                    }
                });
    }

    public void logout() {
        mAuth.signOut();
    }
}

