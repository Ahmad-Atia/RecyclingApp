package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentItemSearchBinding;
import com.example.recyclingapp.models.Item;
import com.example.recyclingapp.ui.adapters.DetectedItemAdapter;
import java.util.ArrayList;
import java.util.List;

public class ItemSearchView extends Fragment {
    private FragmentItemSearchBinding binding;
    private List<Item> allItems; // Dummy database
    private DetectedItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentItemSearchBinding.inflate(inflater, container, false);
        
        setupDummyData();
        
        binding.backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        adapter = new DetectedItemAdapter(new ArrayList<>(), item -> {
            Bundle bundle = new Bundle();
            bundle.putString("itemName", item.getName());
            bundle.putString("itemCategory", item.getCategory());
            if (item.getCategory().toLowerCase().contains("sonder")) {
                Navigation.findNavController(requireView()).navigate(R.id.action_itemSearchView_to_disposalDetailView, bundle);
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_itemSearchView_to_trennAnleitungView, bundle);
            }
        });
        
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.searchResultsRecyclerView.setAdapter(adapter);
        
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        return binding.getRoot();
    }
    
    private void setupDummyData() {
        allItems = new ArrayList<>();
        allItems.add(new Item("1", "Joghurtbecher (Kunststoff)", "GELBE TONNE"));
        allItems.add(new Item("2", "Joghurtglas (Pfandfrei)", "ALTGLAS"));
        allItems.add(new Item("3", "Joghurtbecher (Pappe)", "PAPIERTONNE"));
        allItems.add(new Item("4", "Kaffeekapseln", "GELBE TONNE"));
        allItems.add(new Item("5", "Alte Batterien", "SONDERMÜLL"));
    }
    
    private void filterItems(String query) {
        List<Item> filtered = new ArrayList<>();
        if (query.isEmpty()) {
            binding.resultsHeader.setVisibility(View.GONE);
            binding.searchTitle.setText("Nicht erkannt?");
        } else {
            binding.resultsHeader.setVisibility(View.VISIBLE);
            binding.resultsHeader.setText("ERGEBNISSE FÜR \"" + query.toUpperCase() + "\"");
            binding.searchTitle.setText("Gegenstand wählen");
            
            for (Item item : allItems) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(item);
                }
            }
        }
        
        // Update adapter
        binding.searchResultsRecyclerView.setAdapter(new DetectedItemAdapter(filtered, item -> {
            Bundle bundle = new Bundle();
            bundle.putString("itemName", item.getName());
            bundle.putString("itemCategory", item.getCategory());
            if (item.getCategory().toLowerCase().contains("sonder")) {
                Navigation.findNavController(requireView()).navigate(R.id.action_itemSearchView_to_disposalDetailView, bundle);
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_itemSearchView_to_trennAnleitungView, bundle);
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
