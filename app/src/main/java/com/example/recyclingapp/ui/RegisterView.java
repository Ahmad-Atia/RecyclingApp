package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RegisterView extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthController authController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        authController = new AuthController();

        setupTextWatchers();

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.registerName.getText().toString().trim();
            String email = binding.registerEmail.getText().toString().trim();
            String pass = binding.registerPassword.getText().toString().trim();

            boolean isValid = true;

            if (name.isEmpty()) {
                binding.nameErrorText.setVisibility(View.VISIBLE);
                isValid = false;
            }
            if (email.isEmpty()) {
                binding.emailErrorText.setText("Bitte E-Mail Adresse eingeben.");
                binding.emailErrorText.setVisibility(View.VISIBLE);
                isValid = false;
            }
            if (pass.isEmpty()) {
                binding.passwordErrorText.setVisibility(View.VISIBLE);
                isValid = false;
            }

            if (!isValid) {
                return;
            }

            authController.register(name, email, pass, new AuthController.AuthCallback() {
                @Override
                public void onSuccess() {
                    if (isAdded() && getView() != null) {
                        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE);
                        prefs.edit().putLong("login_timestamp", System.currentTimeMillis()).apply();

                        Navigation.findNavController(getView()).navigate(R.id.action_registerView_to_dashboardView);
                    }
                }
                @Override
                public void onFailure(String message) {
                    if (isAdded()) {
                        if (message.contains("bereits registriert")) {
                            binding.emailErrorText.setText("Diese E-Mail existiert bereits.");
                            binding.emailErrorText.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        binding.btnBackToLogin.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.loginView);
        });

        return binding.getRoot();
    }

    private void setupTextWatchers() {
        binding.registerName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.nameErrorText.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) {}
        });

        binding.registerEmail.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.emailErrorText.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) {}
        });

        binding.registerPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.passwordErrorText.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}