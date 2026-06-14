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

public class RegisterView extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthController authController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        authController = new AuthController();

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.registerName.getText().toString().trim();
            String email = binding.registerEmail.getText().toString().trim();
            String pass = binding.registerPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getContext(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            authController.register(name, email, pass, new AuthController.AuthCallback() {
                @Override
                public void onSuccess() {
                    if (isAdded() && getView() != null) {
                        Navigation.findNavController(getView()).navigate(R.id.action_registerView_to_dashboardView);
                    }
                }
                @Override
                public void onFailure(String message) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
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