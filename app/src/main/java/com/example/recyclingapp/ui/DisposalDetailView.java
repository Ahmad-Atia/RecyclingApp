package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclingapp.R;
import com.example.recyclingapp.controllers.DisposalController;
import com.example.recyclingapp.databinding.FragmentDisposalDetailBinding;
import com.example.recyclingapp.models.DisposalPoint;
import com.example.recyclingapp.models.DisposalPointsManager;
import com.example.recyclingapp.ui.adapters.DisposalPointAdapter;

import java.util.List;

public class DisposalDetailView extends Fragment {
    private FragmentDisposalDetailBinding binding;
    private DisposalController disposalController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDisposalDetailBinding.inflate(inflater, container, false);
        disposalController = new DisposalController(requireContext());
        
        String itemName = getArguments() != null ? getArguments().getString("itemName") : "Gegenstand";
        String itemCategory = getArguments() != null ? getArguments().getString("itemCategory") : "KATEGORIE";
        
        updateUI(itemName, itemCategory != null ? itemCategory : "KATEGORIE");

        binding.backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        loadNearbyPoints();
        
        return binding.getRoot();
    }

    private void updateUI(String name, String category) {
        binding.itemName.setText(name);
        binding.itemCategory.setText(category.toUpperCase());
        binding.mainTitle.setText(category);

        String catLower = category.toLowerCase();
        int colorRes = R.color.black;
        int iconRes = R.drawable.container;

        if (catLower.contains("sonder")) {
            colorRes = R.color.cat_sondermuell;
            iconRes = R.drawable.muell_icon_red;
            binding.instructionIcon.setImageResource(R.drawable.dot_red);
        } else if (catLower.contains("gelb") || catLower.contains("plastik") || catLower.contains("verpackung")) {
            colorRes = R.color.cat_gelbe_tonne;
            iconRes = R.drawable.ic_trash;
            binding.instructionIcon.setImageResource(R.drawable.dot_green);
        } else if (catLower.contains("papier")) {
            colorRes = R.color.cat_papier;
            iconRes = R.drawable.ic_trash;
            binding.instructionIcon.setImageResource(R.drawable.dot_green);
        } else if (catLower.contains("glas")) {
            colorRes = R.color.cat_glas;
            iconRes = R.drawable.ic_trash;
            binding.instructionIcon.setImageResource(R.drawable.dot_green);
        } else if (catLower.contains("bio")) {
            colorRes = R.color.cat_bio;
            iconRes = R.drawable.ic_trash;
            binding.instructionIcon.setImageResource(R.drawable.dot_green);
        } else if (catLower.contains("pfand") || catLower.contains("flasche")) {
            colorRes = R.color.cat_pfand;
            iconRes = R.drawable.container;
            binding.instructionIcon.setImageResource(R.drawable.dot_green);
        } else if (catLower.contains("rest")) {
            colorRes = R.color.cat_restmuell;
            iconRes = R.drawable.ic_trash;
            binding.instructionIcon.setImageResource(R.drawable.dot_red);
        } else if (catLower.contains("akku") || catLower.contains("batterie")) {
            colorRes = R.color.cat_sondermuell;
            iconRes = R.drawable.ic_battery;
            binding.instructionIcon.setImageResource(R.drawable.dot_red);
        }

        int color = getResources().getColor(colorRes, null);
        binding.mainTitle.setTextColor(color);
        binding.itemCategory.setTextColor(color);
        binding.statusCircle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        binding.mainIcon.setImageResource(iconRes);
        binding.mainIcon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        binding.backButton.setImageTintList(android.content.res.ColorStateList.valueOf(color));
        
        String instruction;
        if (catLower.contains("sonder") || catLower.contains("akku") || catLower.contains("batterie")) {
            instruction = name + " gehören nicht in den Hausmüll. Bitte an einer Rückgabestelle abgeben, um Umweltgefahren zu vermeiden.";
        } else if (catLower.contains("gelb")) {
            instruction = name + " gehört in die Gelbe Tonne oder den Gelben Sack.";
        } else if (catLower.contains("papier")) {
            instruction = name + " gehört in die Altpapiertonne.";
        } else if (catLower.contains("pfand")) {
            instruction = "Für dieses Item gibt es Pfand. Bitte im Supermarkt abgeben.";
        } else if (catLower.contains("bio")) {
            instruction = name + " gehört in den Bioabfall.";
        } else {
            instruction = "Bitte entsorgen Sie " + name + " fachgerecht laut lokaler Trennanleitung.";
        }
        binding.instructionText.setText(instruction);
    }

    private void loadNearbyPoints() {
        disposalController.fetchDisposalPointsForCurrentUser(new DisposalPointsManager.PointsCallback() {
            @Override
            public void onSuccess(List<DisposalPoint> points) {
                if (isAdded() && binding != null && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        DisposalPointAdapter adapter = new DisposalPointAdapter(points);
                        binding.disposalPointsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.disposalPointsRecyclerView.setAdapter(adapter);
                    });
                }
            }

            @Override
            public void onError(String error) {
                // Log or handle error
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
