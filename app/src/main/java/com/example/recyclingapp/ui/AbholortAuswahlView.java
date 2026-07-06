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

import com.example.recyclingapp.ui.adapters.AbholortAuswahlAdapter;
import com.example.recyclingapp.controllers.AdressenController;
import com.example.recyclingapp.databinding.FragmentAbholortAuswahlBinding;
import com.example.recyclingapp.models.Adresse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbholortAuswahlView extends Fragment implements AbholortAuswahlAdapter.Listener {

    private FragmentAbholortAuswahlBinding binding;
    private AdressenController adressenController;
    private AbholortAuswahlAdapter adapter;
    private String uid;
    private final List<Adresse> alleAdressen = new ArrayList<>();
    private String ausgewaehlteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAbholortAuswahlBinding.inflate(inflater, container, false);
        adressenController = new AdressenController();

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.rowStandortVerwenden.setOnClickListener(v ->
                Toast.makeText(getContext(), "GPS-Standortbestimmung ist noch nicht angebunden.", Toast.LENGTH_SHORT).show());

        adapter = new AbholortAuswahlAdapter(this);
        binding.rvAdressen.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAdressen.setAdapter(adapter);

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

        binding.btnSpeichern.setOnClickListener(v -> speichereAuswahl());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ladeAdressen();
        }

        return binding.getRoot();
    }

    private void ladeAdressen() {
        adressenController.fetchAdressen(uid, adressen -> {
            if (binding == null) return;
            alleAdressen.clear();
            alleAdressen.addAll(adressen);

            for (Adresse adresse : adressen) {
                if (adresse.isIstStandard()) {
                    ausgewaehlteId = adresse.getId();
                    break;
                }
            }

            filterListe(binding.etSuche.getText().toString());
        }, e -> {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterListe(String suchtext) {
        String suche = suchtext.trim().toLowerCase(Locale.GERMANY);
        List<Adresse> gefiltert = new ArrayList<>();
        for (Adresse adresse : alleAdressen) {
            if (suche.isEmpty()
                    || adresse.getLabel().toLowerCase(Locale.GERMANY).contains(suche)
                    || adresse.getAdresse().toLowerCase(Locale.GERMANY).contains(suche)) {
                gefiltert.add(adresse);
            }
        }
        adapter.submitList(gefiltert, ausgewaehlteId);
    }

    @Override
    public void onAdresseAusgewaehlt(Adresse adresse) {
        ausgewaehlteId = adresse.getId();
        adapter.setAusgewaehlteId(ausgewaehlteId);
    }

    private void speichereAuswahl() {
        if (ausgewaehlteId == null) {
            Toast.makeText(getContext(), "Bitte einen Abholort auswählen.", Toast.LENGTH_SHORT).show();
            return;
        }
        adressenController.setAlsStandard(uid, alleAdressen, ausgewaehlteId, () -> {
            if (binding != null) {
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        }, e -> {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
