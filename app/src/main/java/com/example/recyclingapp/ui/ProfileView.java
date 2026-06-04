package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.recyclingapp.R;
import com.example.recyclingapp.controllers.ProfileController;
import com.example.recyclingapp.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileView extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private ProfileController profileController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        profileController = new ProfileController();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin(binding.getRoot());
            return binding.getRoot();
        }

        String userId = currentUser.getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && isAdded() && binding != null) {
                        String name = documentSnapshot.getString("name");
                        String adresse = documentSnapshot.getString("address");
                        Long level = documentSnapshot.getLong("level");
                        Double co2 = documentSnapshot.getDouble("co2");

                        if (name != null) binding.profileName.setText(name);
                        if (adresse != null) binding.profileAddress.setText(adresse);
                        if (level != null) binding.profileLevelText.setText("Level " + level);
                        if (co2 != null) binding.profileCo2Text.setText(co2 + "kg CO2");
                    }
                });

        binding.btnSaveAddress.setOnClickListener(v -> {
            String neueAdresse = binding.profileAddress.getText().toString().trim();

            if (TextUtils.isEmpty(neueAdresse)) {
                binding.profileAddress.setError("Bitte gib eine Adresse ein.");
                return;
            }

            profileController.updateAddress(userId, neueAdresse);
            Toast.makeText(getActivity(), "Adresse erfolgreich aktualisiert!", Toast.LENGTH_SHORT).show();
            binding.profileAddress.clearFocus();
        });

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Erfolgreich abgemeldet!", Toast.LENGTH_SHORT).show();
            navigateToLogin(v);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void navigateToLogin(View view) {
        try {
            Navigation.findNavController(view).navigate(R.id.loginView);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Navigationsfehler", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}