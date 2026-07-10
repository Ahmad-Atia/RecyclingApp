package com.example.recyclingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.ItemAbholterminBinding;
import com.example.recyclingapp.models.AbholTermin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbholTerminAdapter extends RecyclerView.Adapter<AbholTerminAdapter.ViewHolder> {

    public interface Listener {
        boolean istAusgewaehlt(AbholTermin termin);
        void onAuswahlGeaendert(AbholTermin termin, boolean ausgewaehlt);
    }

    private final List<AbholTermin> termine = new ArrayList<>();
    private final Listener listener;
    private final SimpleDateFormat datumsFormat = new SimpleDateFormat("EEEE, d. MMM", Locale.GERMANY);

    public AbholTerminAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<AbholTermin> neueTermine) {
        termine.clear();
        termine.addAll(neueTermine);
        notifyDataSetChanged();
    }

    public static String emojiFuer(String tonnenTyp) {
        if (tonnenTyp == null) return "♻️";
        switch (tonnenTyp) {
            case "Gelber Sack": return "🥤";
            case "Papiertonne": return "📄";
            case "Restmüll": return "🗑️";
            default: return "♻️";
        }
    }

    public static int kreisDrawableFuer(String tonnenTyp) {
        if (tonnenTyp == null) return R.drawable.bg_circle_light_green;
        switch (tonnenTyp) {
            case "Gelber Sack": return R.drawable.bg_circle_yellow;
            case "Papiertonne": return R.drawable.bg_circle_blue;
            case "Restmüll": return R.drawable.bg_circle_grey;
            default: return R.drawable.bg_circle_light_green;
        }
    }

    public static int punktFarbeFuer(String tonnenTyp) {
        if (tonnenTyp == null) return 0xFF1B5E20;
        switch (tonnenTyp) {
            case "Gelber Sack": return 0xFFE8C547;
            case "Papiertonne": return 0xFF5B8DBE;
            case "Restmüll": return 0xFF9E9E9E;
            default: return 0xFF1B5E20;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAbholterminBinding binding = ItemAbholterminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(termine.get(position), listener, datumsFormat);
    }

    @Override
    public int getItemCount() {
        return termine.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAbholterminBinding binding;

        ViewHolder(ItemAbholterminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AbholTermin termin, Listener listener, SimpleDateFormat datumsFormat) {
            binding.tvTerminTyp.setText(termin.getTonnenTyp());
            binding.tvTerminDatum.setText(datumsFormat.format(termin.getDatum().getTime()));
            binding.tvTerminIcon.setText(emojiFuer(termin.getTonnenTyp()));
            binding.tvTerminIcon.setBackgroundResource(kreisDrawableFuer(termin.getTonnenTyp()));

            boolean ausgewaehlt = listener.istAusgewaehlt(termin);
            binding.getRoot().setAlpha(ausgewaehlt ? 1.0f : 0.5f);

            binding.switchAusgewaehlt.setOnCheckedChangeListener(null);
            binding.switchAusgewaehlt.setChecked(ausgewaehlt);
            binding.switchAusgewaehlt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                binding.getRoot().setAlpha(isChecked ? 1.0f : 0.5f);
                listener.onAuswahlGeaendert(termin, isChecked);
            });
        }
    }
}
