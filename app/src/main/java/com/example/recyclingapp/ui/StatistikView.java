package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recyclingapp.controllers.StatistikController;
import com.example.recyclingapp.databinding.FragmentStatistikBinding;
import com.example.recyclingapp.databinding.ItemMeilensteinBinding;
import com.example.recyclingapp.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

public class StatistikView extends Fragment {

    private static final double CO2_ZIEL_PRO_LEVEL = 15.0;

    private FragmentStatistikBinding binding;
    private StatistikController statistikController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatistikBinding.inflate(inflater, container, false);
        statistikController = new StatistikController();

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid != null) {
            statistikController.loadUserStats(uid, this::render, e ->
                    android.util.Log.e("StatistikView", "Fehler beim Laden der Statistiken: " + e.getMessage()));
        }

        return binding.getRoot();
    }

    private void render(User user) {
        if (binding == null) return;

        int level = user.getUmweltheldLevel();
        binding.tvEcoPunkte.setText(String.format(Locale.GERMANY, "%d Eco-Punkte", user.getEcoScore()));
        binding.tvLevelBadge.setText(String.format(Locale.GERMANY, "🌿 Umweltheld Level %d", level));

        double co2 = user.getCo2Eingespart();
        double naechstesZiel = CO2_ZIEL_PRO_LEVEL * level;
        int fortschritt = (int) Math.min(100, (co2 / naechstesZiel) * 100);

        binding.tvCo2Wert.setText(String.format(Locale.GERMANY, "%.1fkg", co2));
        binding.progressCo2.setProgress(fortschritt);
        binding.tvNaechstesLevel.setText(String.format(Locale.GERMANY, "Nächstes Level bei %.0fkg", naechstesZiel));

        binding.tvGescannt.setText(String.valueOf(user.getGescannteGegenstaende()));
        binding.tvStreak.setText(String.format(Locale.GERMANY, "%d Tage", user.getTagesStreak()));

        renderMeilensteine(statistikController.berechneMeilensteine(user));
    }

    private void renderMeilensteine(List<StatistikController.Meilenstein> meilensteine) {
        binding.containerMeilensteine.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (StatistikController.Meilenstein meilenstein : meilensteine) {
            ItemMeilensteinBinding itemBinding = ItemMeilensteinBinding.inflate(inflater, binding.containerMeilensteine, false);

            itemBinding.tvMeilensteinTitel.setText(meilenstein.titel);
            itemBinding.tvMeilensteinBeschreibung.setText(meilenstein.beschreibung);
            itemBinding.tvMeilensteinStatus.setText(meilenstein.erreicht ? "✅" : "⬜");
            itemBinding.tvMeilensteinIcon.setAlpha(meilenstein.erreicht ? 1.0f : 0.4f);

            binding.containerMeilensteine.addView(itemBinding.getRoot());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
