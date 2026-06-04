package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                    binding.loginErrorText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupTextWatchers() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.loginErrorText.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) {}
        };

        binding.emailEditText.addTextChangedListener(clearErrorWatcher);
        binding.passwordEditText.addTextChangedListener(clearErrorWatcher);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}