package com.example.recyclingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.databinding.ItemAdresseBinding;
import com.example.recyclingapp.models.Adresse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdressenAdapter extends RecyclerView.Adapter<AdressenAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(Adresse adresse);
        void onDelete(Adresse adresse);
    }

    private final List<Adresse> list = new ArrayList<>();
    private final Listener listener;

    public AdressenAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Adresse> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdresseBinding binding = ItemAdresseBinding.inflate(
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
        private final ItemAdresseBinding binding;
        private final SimpleDateFormat datumsFormat = new SimpleDateFormat("EEE, d. MMM", Locale.GERMANY);

        public ViewHolder(ItemAdresseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Adresse adresse, Listener listener) {
            binding.tvAdresseLabel.setText(adresse.getLabel());
            binding.tvAdresseText.setText(adresse.getAdresse());

            if (adresse.getLabel() != null && adresse.getLabel().toLowerCase().contains("arbeit")) {
                binding.tvAdresseIcon.setText("💼");
            } else {
                binding.tvAdresseIcon.setText("🏠");
            }

            binding.rowStandardInfo.setVisibility(adresse.isIstStandard() ? View.VISIBLE : View.GONE);
            binding.tvNaechsteAbholung.setText(naechsteAbholungText(adresse));

            binding.btnEditAdresse.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(adresse);
            });

            binding.btnDeleteAdresse.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(adresse);
            });
        }

        private String naechsteAbholungText(Adresse adresse) {
            Calendar heute = Calendar.getInstance();
            Calendar termin = adresse.naechsteAbholungAb(heute);
            if (termin == null) {
                return "Keine Abholtermine hinterlegt";
            }

            long tageDiff = Math.round((mitternacht(termin).getTimeInMillis() - mitternacht(heute).getTimeInMillis()) / 86400000.0);
            if (tageDiff == 0) return "Nächste Abholung: Heute";
            if (tageDiff == 1) return "Nächste Abholung: Morgen";
            return "Nächste Abholung: " + datumsFormat.format(termin.getTime());
        }

        private static Calendar mitternacht(Calendar quelle) {
            Calendar kopie = (Calendar) quelle.clone();
            kopie.set(Calendar.HOUR_OF_DAY, 0);
            kopie.set(Calendar.MINUTE, 0);
            kopie.set(Calendar.SECOND, 0);
            kopie.set(Calendar.MILLISECOND, 0);
            return kopie;
        }
    }
}
