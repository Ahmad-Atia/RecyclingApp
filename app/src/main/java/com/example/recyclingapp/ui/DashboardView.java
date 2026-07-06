package com.example.recyclingapp.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentDashboardBinding;
import com.example.recyclingapp.controllers.ProfileController;
import com.example.recyclingapp.controllers.ScanController;
import com.example.recyclingapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DashboardView extends Fragment {
    private FragmentDashboardBinding binding;
    private ProfileController profileController;
    private ScanController scanController;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processGalleryImage(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        profileController = new ProfileController();
        scanController = new ScanController();

        binding.viewCalendarButton.setOnClickListener(v -> onViewCalendarPressed());
        binding.startScanButton.setOnClickListener(v -> showScanOptions());

        loadUserData();

        return binding.getRoot();
    }

    private void showScanOptions() {
        String[] options = {"Kamera öffnen", "Bild hochladen"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Scan Option wählen")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Navigation.findNavController(requireView()).navigate(R.id.action_dashboardView_to_cameraView);
                    } else {
                        galleryLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void processGalleryImage(Uri uri) {
        try {
            File tempFile = new File(requireContext().getCacheDir(), "gallery_upload.jpg");
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return;
            
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();

            scanController.uploadAndAnalyzeImage(tempFile);
            if (getView() != null) {
                Navigation.findNavController(getView()).navigate(R.id.action_dashboardView_to_resultView);
            }
            
        } catch (Exception e) {
            android.util.Log.e("DashboardView", "Error processing gallery image", e);
        }
    }

    private void loadUserData() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = User.fromMap(documentSnapshot.getData());
                            render(user);
                        }
                    });
        }
    }

    public void render(User user) {
        if (binding != null) {
            binding.userGreetingTextView.setText("Hallo, " + user.getName() + "!");
            binding.userEmailTextView.setText("Email: " + user.getEmail());
            binding.ecoScoreTextView.setText("Eco Score: " + user.getEcoScore());
        }
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
