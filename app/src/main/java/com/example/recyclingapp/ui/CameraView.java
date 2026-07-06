package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.concurrent.ExecutionException;

public class CameraView extends Fragment {
    private FragmentCameraBinding binding;
    private ScanController scanController;
    private ImageCapture imageCapture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        scanController = new ScanController();

        startCamera();

        binding.captureButton.setOnClickListener(v -> onCaptureButtonPressed());

        return binding.getRoot();
    }

    private void startCamera() {
        if (getContext() == null) return;
        
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                
                // Ensure previewView is ready
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);
                
                Log.d("CameraView", "Camera bound to lifecycle successfully");

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraView", "Use case binding failed", e);
            } catch (IllegalArgumentException e) {
                Log.e("CameraView", "Binding failed due to invalid selector or use case", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    public void onCaptureButtonPressed() {
        if (imageCapture == null) return;

        File imageFile = new File(requireContext().getCacheDir(), "capture.jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(imageFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        scanController.uploadAndAnalyzeImage(imageFile);
                        if (getView() != null) {
                            Navigation.findNavController(getView()).navigate(R.id.action_cameraView_to_resultView);
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraView", "Photo capture failed: " + exception.getMessage(), exception);
                        Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
