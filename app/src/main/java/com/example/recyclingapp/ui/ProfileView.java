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
import com.example.recyclingapp.models.User;
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

        binding.profileName.setText("Lade...");
        binding.profileLevelText.setText("...");
        binding.profileCo2Text.setText("0.0kg CO2");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && isAdded() && binding != null) {
                        User user = User.fromMap(documentSnapshot.getData());
                        if (user != null) {
                            binding.profileName.setText(user.getName() != null ? user.getName() : "User");
                            binding.profileLevelText.setText("Level " + user.getUmweltheldLevel());
                            binding.profileLevelTitle.setText(user.getUmweltheldTitel());
                            binding.profileCo2Text.setText(String.format(java.util.Locale.getDefault(), "%.1fkg CO2", user.getCo2Eingespart()));
                        }
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
            androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build();
            Navigation.findNavController(view).navigate(R.id.loginView, null, navOptions);
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