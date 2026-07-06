package com.example.recyclingapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recyclingapp.databinding.FragmentBenachrichtigungenBinding;

public class BenachrichtigungenView extends Fragment {

    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String KEY_ABHOLTERMINE = "abholtermine_aktiv";
    private static final String KEY_TIPPS = "tipps_aktiv";
    private static final String KEY_WOCHENBERICHT = "wochenbericht_aktiv";
    private static final String KEY_ZEITPUNKT = "erinnerungszeitpunkt";

    private static final String[] ZEITPUNKT_OPTIONEN = {
            "Am Vorabend, 18:00 Uhr",
            "Am Abholtag, 06:00 Uhr",
            "Am Abholtag, 07:00 Uhr"
    };

    private FragmentBenachrichtigungenBinding binding;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBenachrichtigungenBinding.inflate(inflater, container, false);
        prefs = requireContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.switchAbholtermine.setChecked(prefs.getBoolean(KEY_ABHOLTERMINE, true));
        binding.switchTipps.setChecked(prefs.getBoolean(KEY_TIPPS, true));
        binding.switchWochenbericht.setChecked(prefs.getBoolean(KEY_WOCHENBERICHT, false));
        binding.tvZeitpunktWert.setText(prefs.getString(KEY_ZEITPUNKT, ZEITPUNKT_OPTIONEN[0]));

        binding.switchAbholtermine.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_ABHOLTERMINE, isChecked).apply());
        binding.switchTipps.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_TIPPS, isChecked).apply());
        binding.switchWochenbericht.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_WOCHENBERICHT, isChecked).apply());

        binding.rowZeitpunkt.setOnClickListener(v -> zeigeZeitpunktAuswahl());

        return binding.getRoot();
    }

    private void zeigeZeitpunktAuswahl() {
        String aktuell = prefs.getString(KEY_ZEITPUNKT, ZEITPUNKT_OPTIONEN[0]);
        int ausgewaehlt = 0;
        for (int i = 0; i < ZEITPUNKT_OPTIONEN.length; i++) {
            if (ZEITPUNKT_OPTIONEN[i].equals(aktuell)) {
                ausgewaehlt = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Erinnerungszeitpunkt")
                .setSingleChoiceItems(ZEITPUNKT_OPTIONEN, ausgewaehlt, (dialog, which) -> {
                    String auswahl = ZEITPUNKT_OPTIONEN[which];
                    prefs.edit().putString(KEY_ZEITPUNKT, auswahl).apply();
                    binding.tvZeitpunktWert.setText(auswahl);
                    dialog.dismiss();
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
