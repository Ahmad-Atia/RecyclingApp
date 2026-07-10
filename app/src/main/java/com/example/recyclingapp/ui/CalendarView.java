package com.example.recyclingapp.ui;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclingapp.R;
import com.example.recyclingapp.ui.adapters.AbholTerminAdapter;
import com.example.recyclingapp.controllers.AdressenController;
import com.example.recyclingapp.databinding.FragmentHistoryBinding;
import com.example.recyclingapp.databinding.ItemKalenderTagBinding;
import com.example.recyclingapp.models.Abfuhrregel;
import com.example.recyclingapp.models.Adresse;
import com.example.recyclingapp.models.AbholTermin;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CalendarView extends Fragment implements AbholTerminAdapter.Listener {

    private static final String PREFS_NAME = "AbholungPrefs";
    private static final String KEY_PREFIX_AUSGEWAEHLT = "tonne_ausgewaehlt_";

    private FragmentHistoryBinding binding;
    private AdressenController adressenController;
    private AbholTerminAdapter adapter;
    private SharedPreferences prefs;
    private String uid;
    private Calendar angezeigterMonat;
    private List<Abfuhrregel> aktuelleRegeln = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        adressenController = new AdressenController();
        prefs = requireContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        angezeigterMonat = Calendar.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        binding.btnEditAbholort.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_calendarFragment_to_abholortAuswahlView));

        binding.ivMonatZurueck.setOnClickListener(v -> {
            angezeigterMonat.add(Calendar.MONTH, -1);
            renderKalender();
        });
        binding.ivMonatVor.setOnClickListener(v -> {
            angezeigterMonat.add(Calendar.MONTH, 1);
            renderKalender();
        });

        adapter = new AbholTerminAdapter(this);
        binding.rvAbholungen.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAbholungen.setAdapter(adapter);

        ladeAbholort();

        return binding.getRoot();
    }

    private void ladeAbholort() {
        if (uid == null) {
            binding.tvAbholort.setText("Keine Adresse hinterlegt");
            aktuelleRegeln = new ArrayList<>();
            aktualisiereAnzeige();
            return;
        }
        adressenController.fetchAdressen(uid, adressen -> {
            if (binding == null) return;
            Adresse standard = null;
            for (Adresse adresse : adressen) {
                if (adresse.isIstStandard()) {
                    standard = adresse;
                    break;
                }
            }
            if (standard == null && !adressen.isEmpty()) {
                standard = adressen.get(0);
            }
            binding.tvAbholort.setText(standard != null ? standard.getAdresse() : "Keine Adresse hinterlegt");
            aktuelleRegeln = standard != null ? standard.getAbfuhrregeln() : new ArrayList<>();
            aktualisiereAnzeige();
        }, e -> {
            if (binding != null) {
                binding.tvAbholort.setText("Keine Adresse hinterlegt");
                aktuelleRegeln = new ArrayList<>();
                aktualisiereAnzeige();
            }
        });
    }

    private void aktualisiereAnzeige() {
        adapter.submitList(kommendeTermineJeRegel());
        renderKalender();
    }

    /** Ein AbholTermin je Regel: der nächste anstehende Termin ab heute. */
    private List<AbholTermin> kommendeTermineJeRegel() {
        List<AbholTermin> termine = new ArrayList<>();
        Calendar heute = Calendar.getInstance();
        for (Abfuhrregel regel : aktuelleRegeln) {
            termine.add(new AbholTermin(regel.getTonnenTyp(), regel.naechsterTerminAb(heute)));
        }
        Collections.sort(termine, (a, b) -> a.getDatum().compareTo(b.getDatum()));
        return termine;
    }

    private Set<String> ladeAusgewaehlteTonnen() {
        Set<String> ausgewaehlt = new HashSet<>();
        for (Abfuhrregel regel : aktuelleRegeln) {
            String typ = regel.getTonnenTyp();
            if (prefs.getBoolean(KEY_PREFIX_AUSGEWAEHLT + typ, true)) {
                ausgewaehlt.add(typ);
            }
        }
        return ausgewaehlt;
    }

    private void renderKalender() {
        SimpleDateFormat monatFormat = new SimpleDateFormat("MMMM yyyy", Locale.GERMANY);
        binding.tvMonatJahr.setText(monatFormat.format(angezeigterMonat.getTime()));

        binding.calendarGridContainer.removeAllViews();

        Calendar monatsStart = (Calendar) angezeigterMonat.clone();
        monatsStart.set(Calendar.DAY_OF_MONTH, 1);
        int wochentagIndex = (monatsStart.get(Calendar.DAY_OF_WEEK) + 5) % 7; // Montag = 0
        int tageImMonat = monatsStart.getActualMaximum(Calendar.DAY_OF_MONTH);
        int gesamtZellen = wochentagIndex + tageImMonat;
        int zeilenAnzahl = (int) Math.ceil(gesamtZellen / 7.0);

        Calendar zellDatum = (Calendar) monatsStart.clone();
        zellDatum.add(Calendar.DAY_OF_MONTH, -wochentagIndex);

        Calendar heute = Calendar.getInstance();
        Set<String> ausgewaehlteTonnen = ladeAusgewaehlteTonnen();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int zeile = 0; zeile < zeilenAnzahl; zeile++) {
            LinearLayout reihe = new LinearLayout(getContext());
            reihe.setOrientation(LinearLayout.HORIZONTAL);
            reihe.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            for (int spalte = 0; spalte < 7; spalte++) {
                ItemKalenderTagBinding zellBinding = ItemKalenderTagBinding.inflate(inflater, reihe, false);
                zellBinding.tvTagNummer.setText(String.valueOf(zellDatum.get(Calendar.DAY_OF_MONTH)));

                boolean gehoertZuMonat = zellDatum.get(Calendar.MONTH) == monatsStart.get(Calendar.MONTH)
                        && zellDatum.get(Calendar.YEAR) == monatsStart.get(Calendar.YEAR);
                boolean istHeute = istGleicherTag(zellDatum, heute);

                if (istHeute) {
                    zellBinding.tvTagNummer.setBackgroundResource(R.drawable.bg_circle_dark_green);
                    zellBinding.tvTagNummer.setTextColor(0xFFFFFFFF);
                } else {
                    zellBinding.tvTagNummer.setBackground(null);
                    zellBinding.tvTagNummer.setTextColor(gehoertZuMonat ? 0xFF333333 : 0xFFC0C0C0);
                }

                Abfuhrregel treffer = findeRegelFuerTag(zellDatum, ausgewaehlteTonnen);
                if (treffer != null) {
                    zellBinding.vTagPunkt.setVisibility(View.VISIBLE);
                    GradientDrawable punkt = (GradientDrawable) zellBinding.vTagPunkt.getBackground().mutate();
                    punkt.setColor(AbholTerminAdapter.punktFarbeFuer(treffer.getTonnenTyp()));
                } else {
                    zellBinding.vTagPunkt.setVisibility(View.INVISIBLE);
                }

                reihe.addView(zellBinding.getRoot());
                zellDatum.add(Calendar.DAY_OF_MONTH, 1);
            }

            binding.calendarGridContainer.addView(reihe);
        }
    }

    private Abfuhrregel findeRegelFuerTag(Calendar tag, Set<String> ausgewaehlteTonnen) {
        for (Abfuhrregel regel : aktuelleRegeln) {
            if (ausgewaehlteTonnen.contains(regel.getTonnenTyp()) && regel.trifftAufTag(tag)) {
                return regel;
            }
        }
        return null;
    }

    private boolean istGleicherTag(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public boolean istAusgewaehlt(AbholTermin termin) {
        return prefs.getBoolean(KEY_PREFIX_AUSGEWAEHLT + termin.getTonnenTyp(), true);
    }

    @Override
    public void onAuswahlGeaendert(AbholTermin termin, boolean ausgewaehlt) {
        prefs.edit().putBoolean(KEY_PREFIX_AUSGEWAEHLT + termin.getTonnenTyp(), ausgewaehlt).apply();
        renderKalender();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
