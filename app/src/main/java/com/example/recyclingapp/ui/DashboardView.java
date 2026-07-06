package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentDashboardBinding;
import com.example.recyclingapp.controllers.ProfileController;
import com.example.recyclingapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardView extends Fragment {
    private FragmentDashboardBinding binding;
    private ProfileController profileController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        profileController = new ProfileController();

        binding.viewCalendarButton.setOnClickListener(v -> onViewCalendarPressed());
        binding.startScanButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboardView_to_cameraView);
        });

        loadUserData();

        return binding.getRoot();
    }

    private void loadUserData() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && isAdded() && binding != null) {
                            User user = User.fromMap(documentSnapshot.getData());
                            render(user);
                        }
                    });
        }
    }

    public void render(User user) {
        if (binding == null) return;
        binding.userEmailTextView.setText("Email: " + user.getEmail());
        binding.ecoScoreTextView.setText("Eco Score: " + user.getEcoScore());
    }

    public void onViewCalendarPressed() {
        profileController.fetchWasteCalendar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
