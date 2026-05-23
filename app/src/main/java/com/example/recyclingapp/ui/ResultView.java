package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.recyclingapp.databinding.FragmentResultBinding;
import com.example.recyclingapp.controllers.ScanController;
import com.example.recyclingapp.models.ScanResult;

public class ResultView extends Fragment {
    private FragmentResultBinding binding;
    private ScanController scanController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        scanController = new ScanController();

        binding.findDisposalButton.setOnClickListener(v -> onFindDisposalPointPressed());

        return binding.getRoot();
    }

    public void render(ScanResult result) {
        binding.detectedItemsTextView.setText("Detected: " + String.join(", ", result.getDetectedItems()));
        binding.depositTextView.setText("Deposit: " + (result.isDepositFound() ? "Yes" : "No"));
        // Load image into binding.resultImageView (e.g. using Glide)
    }

    public void onFindDisposalPointPressed() {
        scanController.getDisposalPoints("item_id");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
