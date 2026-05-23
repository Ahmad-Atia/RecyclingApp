package com.example.recyclingapp.controllers;

import com.google.firebase.auth.FirebaseAuth;

public class AuthController {
    private FirebaseAuth mAuth;

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public AuthController() {
        mAuth = FirebaseAuth.getInstance();
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
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Login failed");
                    }
                });
    }

    public void register(String email, String pass, AuthCallback callback) {
        if (email.isEmpty() || pass.isEmpty()) {
            callback.onFailure("Email and password cannot be empty");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Registration failed");
                    }
                });
    }

    public void logout() {
        mAuth.signOut();
    }
}
