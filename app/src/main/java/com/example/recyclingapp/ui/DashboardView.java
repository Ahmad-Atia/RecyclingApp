package com.example.recyclingapp.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentDashboardBinding;
import com.example.recyclingapp.controllers.AdressenController;
import com.example.recyclingapp.controllers.NotificationController;
import com.example.recyclingapp.controllers.ProfileController;
import com.example.recyclingapp.controllers.ScanController;
import com.example.recyclingapp.models.Abfuhrregel;
import com.example.recyclingapp.models.AbholTermin;
import com.example.recyclingapp.models.Adresse;
import com.example.recyclingapp.models.AppNotification;
import com.example.recyclingapp.models.ScanResult;
import com.example.recyclingapp.models.User;
import com.example.recyclingapp.ui.adapters.AbholTerminAdapter;
import com.example.recyclingapp.ui.adapters.ScanVerlaufAdapter;
import com.example.recyclingapp.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DashboardView extends Fragment {
    private FragmentDashboardBinding binding;
    private ProfileController profileController;
    private ScanController scanController;
    private NotificationController notificationController;
    private AdressenController adressenController;
    private ScanVerlaufAdapter scanAdapter;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    navigateToCamera();
                } else {
                    Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    processGalleryImage(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        profileController = new ProfileController();
        scanController = new ScanController();
        notificationController = new NotificationController();
        adressenController = new AdressenController();

        setupRecentScans();


        binding.startScanButton.setOnClickListener(v -> showScanOptionsDialog());
        binding.btnAlleAnsehen.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_dashboardView_to_scanVerlaufView));
        binding.btnNotifications.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_dashboardView_to_benachrichtigungenView));

        loadUserData();
        loadRecentScans();
        checkUnreadNotifications();
        ladeNaechsteAbholung();

        return binding.getRoot();
    }

    private void ladeNaechsteAbholung() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        adressenController.fetchAdressen(uid, adressen -> {
            if (!isAdded() || binding == null) return;
            
            Adresse standard = null;
            for (Adresse a : adressen) {
                if (a.isIstStandard()) {
                    standard = a;
                    break;
                }
            }
            if (standard == null && !adressen.isEmpty()) {
                standard = adressen.get(0);
            }

            if (standard != null && !standard.getAbfuhrregeln().isEmpty()) {
                updateAbfuhrBanner(standard.getAbfuhrregeln());
            } else {
                binding.nextPickupNameDate.setText("Keine Termine");
                binding.nextPickupCountdownContainer.setVisibility(View.GONE);
            }
        }, e -> Log.e("DashboardView", "Fehler beim Laden der Adressen", e));
    }

    private void updateAbfuhrBanner(List<Abfuhrregel> regeln) {
        Calendar heute = Calendar.getInstance();
        List<AbholTermin> termine = new ArrayList<>();
        
        for (Abfuhrregel regel : regeln) {
            termine.add(new AbholTermin(regel.getTonnenTyp(), regel.naechsterTerminAb(heute)));
        }
        
        Collections.sort(termine, (a, b) -> a.getDatum().compareTo(b.getDatum()));

        if (!termine.isEmpty()) {
            AbholTermin naechster = termine.get(0);
            SimpleDateFormat fmt = new SimpleDateFormat("EEEE, d. MMM", Locale.GERMANY);
            
            String text = naechster.getTonnenTyp() + " –\n" + fmt.format(naechster.getDatum().getTime());
            binding.nextPickupNameDate.setText(text);
            
            long tage = tageZwischen(heute, naechster.getDatum());
            String countdown;
            if (tage == 0) {
                countdown = "Heute";
            } else if (tage == 1) {
                countdown = "Morgen";
            } else {
                countdown = "in " + tage + " Tagen";
            }
            
            binding.nextPickupCountdown.setText(countdown);
            binding.nextPickupCountdownContainer.setVisibility(View.VISIBLE);
            
            // Optional: Banner Farbe an Tonnen-Typ anpassen
            int farbe = AbholTerminAdapter.punktFarbeFuer(naechster.getTonnenTyp());
            // Wir machen sie etwas dunkler für bessere Lesbarkeit von weißem Text, 
            // oder nutzen sie direkt wenn sie kräftig genug ist.
            binding.viewCalendarButton.setCardBackgroundColor(farbe);
        }
    }

    private long tageZwischen(Calendar von, Calendar bis) {
        Calendar a = (Calendar) von.clone();
        a.set(Calendar.HOUR_OF_DAY, 0); a.set(Calendar.MINUTE, 0); a.set(Calendar.SECOND, 0); a.set(Calendar.MILLISECOND, 0);
        Calendar b = (Calendar) bis.clone();
        b.set(Calendar.HOUR_OF_DAY, 0); b.set(Calendar.MINUTE, 0); b.set(Calendar.SECOND, 0); b.set(Calendar.MILLISECOND, 0);
        return Math.round((b.getTimeInMillis() - a.getTimeInMillis()) / 86400000.0);
    }

    private void checkUnreadNotifications() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            notificationController.fetchNotifications(uid, list -> {
                if (isAdded() && binding != null) {
                    boolean hasUnread = false;
                    for (AppNotification n : list) {
                        if (!n.isRead()) {
                            hasUnread = true;
                            break;
                        }
                    }
                    binding.notificationBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
                }
            }, e -> Log.e("DashboardView", "Fehler beim Laden der Benachrichtigungen", e));
        }
    }

    private void setupRecentScans() {
        scanAdapter = new ScanVerlaufAdapter(eintrag -> {
            if (getView() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("scanId", eintrag.getId());
                Navigation.findNavController(getView()).navigate(R.id.action_dashboardView_to_resultView, bundle);
            }
        });
        binding.rvRecentScans.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentScans.setAdapter(scanAdapter);
    }

    private void loadRecentScans() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            scanController.fetchScanVerlauf(uid, eintraege -> {
                if (isAdded() && binding != null) {
                    // Show only the last 3 scans on home page
                    scanAdapter.submitList(eintraege.stream().limit(3).collect(Collectors.toList()));
                }
            }, e -> Log.e("DashboardView", "Fehler beim Laden des Verlaufs", e));
        }
    }

    private void showScanOptionsDialog() {
        String[] options = {"Kamera öffnen", "Bild aus Galerie hochladen", "Schließen"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Scan-Optionen");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Camera
                    if (checkCameraPermission()) {
                        navigateToCamera();
                    } else {
                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                    break;
                case 1: // Gallery
                    galleryLauncher.launch("image/*");
                    break;
                case 2: // Close
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void navigateToCamera() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_dashboardView_to_cameraView);
        }
    }

    private void processGalleryImage(Uri uri) {
        if (!NetworkUtils.isOnline(requireContext())) {
            Toast.makeText(requireContext(), "Internetverbindung erforderlich für KI-Analyse", Toast.LENGTH_LONG).show();
            return;
        }
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

            Toast.makeText(requireContext(), "Analysiere Bild...", Toast.LENGTH_LONG).show();
            
            scanController.uploadAndAnalyzeImage(tempFile, new ScanController.ScanCallback() {
                @Override
                public void onScanCompleted(ScanResult result) {
                    if (isAdded() && getView() != null) {
                        Bundle bundle = new Bundle();
                        // Passing ID to ResultView if it fetches data by ID, or we could pass the whole object
                        bundle.putString("scanId", result.getId());
                        Navigation.findNavController(getView()).navigate(R.id.action_dashboardView_to_resultView, bundle);
                    }
                }

                @Override
                public void onScanFailed(Exception e) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Analyse fehlgeschlagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("DashboardView", "Error processing gallery image", e);
            Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
        }
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
        
        String name = user.getName();
        String email = user.getEmail();

        if (name != null && !name.isEmpty()) {
            binding.userGreetingTextView.setText("Hallo " + name + "!");
        } else {
            binding.userGreetingTextView.setText("Hallo!");
        }

        if (email != null) {
            binding.userEmailTextView.setText(email);
            binding.userEmailTextView.setVisibility(View.VISIBLE);
        } else {
            binding.userEmailTextView.setVisibility(View.GONE);
        }

        binding.ecoScoreTextView.setText(String.valueOf(user.getEcoScore()));
        binding.co2TextView.setText(String.format(java.util.Locale.getDefault(), "%.1fkg", user.getCo2Eingespart()));
    }

    public void onViewCalendarPressed() {
        if (getView() != null) {
            // Use NavOptions to mimic bottom navigation behavior (top-level navigation)
            androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.dashboardView, false)
                    .build();
            Navigation.findNavController(getView()).navigate(R.id.calendarFragment, null, navOptions);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
