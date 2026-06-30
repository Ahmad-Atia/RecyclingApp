package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recyclingapp.R;
import com.example.recyclingapp.controllers.AuthController;
import com.example.recyclingapp.databinding.FragmentRegisterBinding;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterView extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthController authController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        authController = new AuthController();

        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.registerEmail.getText().toString().trim();
            String password = binding.registerPassword.getText().toString().trim();
            String name = binding.registerName.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(getContext(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Registrierung läuft...", Toast.LENGTH_SHORT).show();

            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                                    new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                            .setDisplayName(name) // Hier setzen wir den echten Namen!
                                            .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        user.sendEmailVerification()
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(getContext(), "Bestätigungsmail versendet!", Toast.LENGTH_SHORT).show();
                                                    Navigation.findNavController(binding.getRoot())
                                                            .navigate(R.id.action_registerView_to_emailVerificationView);
                                                });
                                    });
                        }
                    })
                    .addOnFailureListener(regError -> {
                        Toast.makeText(getContext(), "Registrierung fehlgeschlagen: " + regError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        binding.btnBackToLogin.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.loginView);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}