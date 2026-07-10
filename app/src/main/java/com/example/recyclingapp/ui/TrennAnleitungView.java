package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.recyclingapp.databinding.FragmentTrennAnleitungBinding;

public class TrennAnleitungView extends Fragment {
    private FragmentTrennAnleitungBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTrennAnleitungBinding.inflate(inflater, container, false);
        
        String itemName = getArguments() != null ? getArguments().getString("itemName") : "Gegenstand";
        binding.itemName.setText(itemName);

        binding.backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.doneButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
