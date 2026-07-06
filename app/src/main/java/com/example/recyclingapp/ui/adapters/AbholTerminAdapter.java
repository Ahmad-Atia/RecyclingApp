package com.example.recyclingapp.ui.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    private final List<AbholTermin> list = new ArrayList<>();
    private final Listener listener;

    public AbholTerminAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<AbholTermin> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    public static int punktFarbeFuer(String tonnenTyp) {
        if (tonnenTyp == null) return Color.GRAY;
        switch (tonnenTyp.toLowerCase()) {
            case "biotonne": return Color.parseColor("#4CAF50");
            case "gelber sack":
            case "gelbe tonne": return Color.parseColor("#FFEB3B");
            case "papiertonne": return Color.parseColor("#2196F3");
            case "restmüll": return Color.parseColor("#757575");
            default: return Color.parseColor("#9E9E9E");
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
        holder.bind(list.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAbholterminBinding binding;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d. MMM", Locale.GERMANY);

        public ViewHolder(ItemAbholterminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AbholTermin termin, Listener listener) {
            binding.tvTerminTyp.setText(termin.getTonnenTyp());
            binding.tvTerminDatum.setText(dateFormat.format(termin.getDatum().getTime()));

            int farbe = punktFarbeFuer(termin.getTonnenTyp());
            GradientDrawable background = (GradientDrawable) binding.tvTerminIcon.getBackground();
            background.setColor(farbe);

            if (termin.getTonnenTyp().toLowerCase().contains("gelb")) {
                binding.tvTerminIcon.setTextColor(Color.BLACK);
            } else {
                binding.tvTerminIcon.setTextColor(Color.WHITE);
            }

            binding.switchAusgewaehlt.setOnCheckedChangeListener(null);
            binding.switchAusgewaehlt.setChecked(listener.istAusgewaehlt(termin));
            binding.switchAusgewaehlt.setOnCheckedChangeListener((v, isChecked) -> {
                if (listener != null) {
                    listener.onAuswahlGeaendert(termin, isChecked);
                }
            });
        }
    }
}
