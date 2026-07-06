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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.recyclingapp.databinding.FragmentProfileBinding;
import com.example.recyclingapp.R;

public class ProfileView extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin(binding.getRoot());
            return binding.getRoot();
        }

        String userId = currentUser.getUid();

        String authName = currentUser.getDisplayName();
        if (authName != null && !authName.isEmpty()) {
            binding.profileName.setText(authName);
        } else {
            binding.profileName.setText("Neuer User");
        }

        binding.profileLevelText.setText("Level 1");
        binding.profileCo2Text.setText("0.0kg CO2");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && isAdded() && binding != null) {
                        String name = documentSnapshot.getString("name");
                        Long level = documentSnapshot.getLong("level");
                        Double co2 = documentSnapshot.getDouble("co2");

                        if (name != null && !name.isEmpty()) binding.profileName.setText(name);
                        if (level != null) binding.profileLevelText.setText("Level " + level);
                        if (co2 != null) binding.profileCo2Text.setText(co2 + "kg CO2");
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("ProfileFirestore", "Datenbank-Abfrage blockiert: " + e.getMessage());
                });

        binding.btnStats.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_statistikView));

        binding.rowStandorte.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_adressenView));

        binding.rowScanVerlauf.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_scanVerlaufView));

        binding.rowBenachrichtigung.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_benachrichtigungenView));

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
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Navigationsfehler", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}