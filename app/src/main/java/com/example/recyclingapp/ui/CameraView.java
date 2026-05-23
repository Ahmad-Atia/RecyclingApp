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
import com.example.recyclingapp.databinding.FragmentCameraBinding;
import com.example.recyclingapp.controllers.ScanController;
import java.io.File;

public class CameraView extends Fragment {
    private FragmentCameraBinding binding;
    private ScanController scanController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        scanController = new ScanController();

        binding.captureButton.setOnClickListener(v -> onCaptureButtonPressed());

        return binding.getRoot();
    }

    public void onCaptureButtonPressed() {
        // Logic to capture image from PreviewView
        File imageFile = new File(requireContext().getCacheDir(), "capture.jpg");
        scanController.uploadAndAnalyzeImage(imageFile);

        Navigation.findNavController(requireView()).navigate(R.id.action_cameraView_to_resultView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
