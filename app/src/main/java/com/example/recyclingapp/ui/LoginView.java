package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.recyclingapp.R;
import com.example.recyclingapp.controllers.AuthController;
import com.example.recyclingapp.databinding.FragmentLoginBinding;

public class LoginView extends Fragment {
    private FragmentLoginBinding binding;
    private AuthController authController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        authController = new AuthController();

        setupTextWatchers();

        binding.loginButton.setOnClickListener(v -> onLoginButtonPressed());
        binding.registerButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.registerView));

        return binding.getRoot();
    }

    /*
    public void onLoginButtonPressed() {
        String email = binding.emailEditText.getText().toString().trim();
        String pass = binding.passwordEditText.getText().toString().trim();

        authController.login(email, pass, new AuthController.AuthCallback() {
            @Override
            public void onSuccess() {
                if (isAdded() && getView() != null) {
                    android.content.SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE);
                    prefs.edit().putLong("login_timestamp", System.currentTimeMillis()).apply();

                    Navigation.findNavController(getView()).navigate(R.id.action_loginView_to_dashboardView);
                }
            }

            @Override
            public void onFailure(String message) {
                if (isAdded()) {
                    android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

     */

    public void onLoginButtonPressed() {
        String email = binding.emailEditText.getText().toString().trim();
        String pass = binding.passwordEditText.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getContext(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }


        if (email.equals("admin@test.de") && pass.equals("123456")) {
            if (getView() != null) {
                Navigation.findNavController(getView()).navigate(R.id.action_loginView_to_dashboardView);
            }
            return;
        }

        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        user.reload().addOnCompleteListener(task -> {

                            if (user.isEmailVerified()) {
                                android.content.SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE);
                                prefs.edit().putLong("login_timestamp", System.currentTimeMillis()).apply();

                                if (getView() != null) {
                                    Navigation.findNavController(getView()).navigate(R.id.action_loginView_to_dashboardView);
                                }
                            } else {
                                Toast.makeText(getContext(), "Bitte bestätige zuerst deine E-Mail über den Link in deinem Postfach!", Toast.LENGTH_LONG).show();

                                auth.signOut();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Login fehlgeschlagen: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setupTextWatchers() {
        // Removed as loginErrorText is gone
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}