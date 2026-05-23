package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentLoginBinding;
import com.example.recyclingapp.controllers.AuthController;

public class LoginView extends Fragment {
    private FragmentLoginBinding binding;
    private AuthController authController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        authController = new AuthController();

        binding.loginButton.setOnClickListener(v -> onLoginButtonPressed());
        binding.registerButton.setOnClickListener(v -> onRegisterButtonPressed());

        return binding.getRoot();
    }

    public void onLoginButtonPressed() {
        String email = binding.emailEditText.getText().toString();
        String pass = binding.passwordEditText.getText().toString();

        authController.login(email, pass, new AuthController.AuthCallback() {
            @Override
            public void onSuccess() {
                if (isAdded()) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_loginView_to_dashboardView);
                }
            }

            @Override
            public void onFailure(String message) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Login failed: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onRegisterButtonPressed() {
        String email = binding.emailEditText.getText().toString();
        String pass = binding.passwordEditText.getText().toString();

        authController.register(email, pass, new AuthController.AuthCallback() {
            @Override
            public void onSuccess() {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_loginView_to_dashboardView);
                }
            }

            @Override
            public void onFailure(String message) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Registration failed: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
