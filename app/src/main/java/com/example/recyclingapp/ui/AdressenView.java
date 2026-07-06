package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclingapp.ui.adapters.AdressenAdapter;
import com.example.recyclingapp.controllers.AdressenController;
import com.example.recyclingapp.databinding.DialogAdresseBinding;
import com.example.recyclingapp.databinding.FragmentAdressenBinding;
import com.example.recyclingapp.models.Adresse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdressenView extends Fragment implements AdressenAdapter.Listener {

    private FragmentAdressenBinding binding;
    private AdressenController adressenController;
    private AdressenAdapter adapter;
    private String uid;
    private List<Adresse> aktuelleAdressen = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdressenBinding.inflate(inflater, container, false);
        adressenController = new AdressenController();

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnNeuerStandort.setOnClickListener(v -> zeigeAdressDialog(null));

        adapter = new AdressenAdapter(this);
        binding.rvAdressen.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAdressen.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ladeAdressen();
        }

        return binding.getRoot();
    }

    private void ladeAdressen() {
        adressenController.fetchAdressen(uid, adressen -> {
            if (binding == null) return;
            aktuelleAdressen = adressen;
            adapter.submitList(adressen);
        }, this::zeigeFehler);
    }

    @Override
    public void onEdit(Adresse adresse) {
        zeigeAdressDialog(adresse);
    }

    @Override
    public void onDelete(Adresse adresse) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Adresse löschen")
                .setMessage("Möchtest du \"" + adresse.getLabel() + "\" wirklich löschen?")
                .setPositiveButton("Löschen", (dialog, which) ->
                        adressenController.deleteAdresse(uid, adresse.getId(), this::ladeAdressen, this::zeigeFehler))
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void zeigeAdressDialog(@Nullable Adresse bestehendeAdresse) {
        DialogAdresseBinding dialogBinding = DialogAdresseBinding.inflate(LayoutInflater.from(requireContext()));

        if (bestehendeAdresse != null) {
            dialogBinding.etDialogLabel.setText(bestehendeAdresse.getLabel());
            dialogBinding.etDialogAdresse.setText(bestehendeAdresse.getAdresse());
            dialogBinding.cbDialogStandard.setChecked(bestehendeAdresse.isIstStandard());
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(bestehendeAdresse == null ? "Neuen Standort hinzufügen" : "Standort bearbeiten")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Speichern", (dialog, which) ->
                        speichereAdresse(bestehendeAdresse, dialogBinding.etDialogLabel, dialogBinding.etDialogAdresse, dialogBinding.cbDialogStandard))
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void speichereAdresse(@Nullable Adresse bestehendeAdresse, EditText etLabel, EditText etAdresse, CheckBox cbStandard) {
        String label = etLabel.getText().toString().trim();
        String adresseText = etAdresse.getText().toString().trim();
        boolean standardGewaehlt = cbStandard.isChecked();

        if (TextUtils.isEmpty(label) || TextUtils.isEmpty(adresseText)) {
            Toast.makeText(getContext(), "Bitte Name und Adresse ausfüllen.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bestehendeAdresse == null) {
            Adresse neueAdresse = new Adresse(label, adresseText, standardGewaehlt);
            adressenController.addAdresse(uid, neueAdresse, neueId -> {
                if (standardGewaehlt) {
                    adressenController.setAlsStandard(uid, aktuelleAdressen, neueId, this::ladeAdressen, this::zeigeFehler);
                } else {
                    ladeAdressen();
                }
            }, this::zeigeFehler);
        } else {
            bestehendeAdresse.setLabel(label);
            bestehendeAdresse.setAdresse(adresseText);
            bestehendeAdresse.setIstStandard(standardGewaehlt);
            adressenController.updateAdresse(uid, bestehendeAdresse, () -> {
                if (standardGewaehlt) {
                    adressenController.setAlsStandard(uid, aktuelleAdressen, bestehendeAdresse.getId(), this::ladeAdressen, this::zeigeFehler);
                } else {
                    ladeAdressen();
                }
            }, this::zeigeFehler);
        }
    }

    private void zeigeFehler(Exception e) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
