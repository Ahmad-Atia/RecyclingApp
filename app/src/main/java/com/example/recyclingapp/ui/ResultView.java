package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentResultBinding;
import com.example.recyclingapp.models.ScanResult;
import com.example.recyclingapp.ui.adapters.DetectedItemAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Map;

public class ResultView extends BottomSheetDialogFragment {
    private FragmentResultBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);

        binding.rescanButton.setOnClickListener(v -> {
            dismiss();
        });

        String scanId = getArguments() != null ? getArguments().getString("scanId") : null;
        if (scanId != null) {
            loadScanResult(scanId);
        }

        return binding.getRoot();
    }

    private void loadScanResult(String scanId) {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists() && binding != null) {
                            Object scansObj = doc.get("scans");
                            if (scansObj instanceof List) {
                                for (Object obj : (List<?>) scansObj) {
                                    if (obj instanceof Map) {
                                        ScanResult res = ScanResult.fromMap((Map<String, Object>) obj);
                                        if (res != null && scanId.equals(res.getId())) {
                                            render(res);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void render(ScanResult result) {
        if (result == null || result.getDetectedItems() == null || binding == null) return;

        DetectedItemAdapter adapter = new DetectedItemAdapter(result.getDetectedItems(), item -> {
            Bundle bundle = new Bundle();
            bundle.putString("itemName", item.getName());
            bundle.putString("itemCategory", item.getCategory());
            
            // Navigate to DisposalDetailView as requested (NOT TrennAnleitungView)
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_resultView_to_disposalDetailView, bundle);
            
            dismiss();
        });

        binding.detectedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.detectedItemsRecyclerView.setAdapter(adapter);
        
        binding.searchEditText.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_resultView_to_itemSearchView);
            dismiss();
        });
        
        binding.searchEditText.setFocusable(false);
        binding.searchEditText.setClickable(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
