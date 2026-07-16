package com.example.recyclingapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
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
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentCameraBinding;
import com.example.recyclingapp.controllers.ScanController;
import com.example.recyclingapp.models.ScanResult;
import com.example.recyclingapp.utils.NetworkUtils;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.concurrent.ExecutionException;

public class CameraView extends Fragment {
    private FragmentCameraBinding binding;
    private ScanController scanController;
    private ImageCapture imageCapture;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(requireContext(), "Camera permission is required to scan items", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        scanController = new ScanController();

        if (checkPermission()) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        binding.captureButton.setOnClickListener(v -> onCaptureButtonPressed());
        binding.closeButton.setOnClickListener(v -> {
            if (getView() != null) {
                Navigation.findNavController(getView()).navigateUp();
            }
        });

        return binding.getRoot();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        if (getContext() == null) return;

        if (!requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(requireContext(), "No camera hardware detected", Toast.LENGTH_LONG).show();
            return;
        }
        
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            if (binding == null || getContext() == null) return;
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                try {
                    if (!cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                        if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                        } else {
                            Toast.makeText(requireContext(), "No camera available", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } catch (CameraInfoUnavailableException e) {
                    Log.e("CameraView", "Camera info unavailable", e);
                }

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);
                
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraView", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    public void onCaptureButtonPressed() {
        if (imageCapture == null || getContext() == null) return;

        if (!NetworkUtils.isOnline(requireContext())) {
            Toast.makeText(requireContext(), "Internetverbindung erforderlich für KI-Analyse", Toast.LENGTH_LONG).show();
            return;
        }

        File imageFile = new File(requireContext().getCacheDir(), "capture.jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(imageFile).build();

        Toast.makeText(requireContext(), "Bild wird verarbeitet...", Toast.LENGTH_SHORT).show();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        scanController.uploadAndAnalyzeImage(imageFile, new ScanController.ScanCallback() {
                            @Override
                            public void onScanCompleted(ScanResult result) {
                                if (isAdded() && getView() != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("scanId", result.getId());
                                    Navigation.findNavController(getView()).navigate(R.id.action_cameraView_to_resultView, bundle);
                                }
                            }

                            @Override
                            public void onScanFailed(Exception e) {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "Analyse fehlgeschlagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraView", "Photo capture failed", exception);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
