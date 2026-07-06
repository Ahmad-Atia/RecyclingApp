package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclingapp.ui.adapters.ScanVerlaufAdapter;
import com.example.recyclingapp.controllers.ScanController;
import com.example.recyclingapp.databinding.FragmentScanVerlaufBinding;
import com.example.recyclingapp.models.ScanVerlaufEintrag;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScanVerlaufView extends Fragment {
    private FragmentScanVerlaufBinding binding;
    private ScanController scanController;
    private ScanVerlaufAdapter adapter;
    private final List<ScanVerlaufEintrag> alleEintraege = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScanVerlaufBinding.inflate(inflater, container, false);
        scanController = new ScanController();

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        adapter = new ScanVerlaufAdapter();
        binding.rvScanVerlauf.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvScanVerlauf.setAdapter(adapter);

        binding.etSuche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterListe(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ladeScanVerlauf();

        return binding.getRoot();
    }

    private void ladeScanVerlauf() {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        scanController.fetchScanVerlauf(uid, eintraege -> {
            if (binding == null) return;
            alleEintraege.clear();
            alleEintraege.addAll(eintraege);
            filterListe(binding.etSuche.getText().toString());
        }, e -> {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Scan-Verlauf konnte nicht geladen werden: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterListe(String suchtext) {
        String suche = suchtext.trim().toLowerCase(Locale.GERMANY);
        List<ScanVerlaufEintrag> gefiltert = new ArrayList<>();
        for (ScanVerlaufEintrag eintrag : alleEintraege) {
            if (suche.isEmpty() || eintrag.getTitel().toLowerCase(Locale.GERMANY).contains(suche)) {
                gefiltert.add(eintrag);
            }
        }
        adapter.submitList(gefiltert);
        binding.tvScanAnzahl.setText(alleEintraege.size() + " Scans");
        binding.tvLeerZustand.setVisibility(gefiltert.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
