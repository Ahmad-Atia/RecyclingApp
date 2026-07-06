package com.example.recyclingapp.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.databinding.ItemAbholortAuswahlBinding;
import com.example.recyclingapp.models.Adresse;
import java.util.ArrayList;
import java.util.List;

public class AbholortAuswahlAdapter extends RecyclerView.Adapter<AbholortAuswahlAdapter.ViewHolder> {

    public interface Listener {
        void onAdresseAusgewaehlt(Adresse adresse);
    }

    private final List<Adresse> list = new ArrayList<>();
    private String ausgewaehlteId;
    private final Listener listener;

    public AbholortAuswahlAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Adresse> newList, String ausgewaehlteId) {
        this.list.clear();
        this.list.addAll(newList);
        this.ausgewaehlteId = ausgewaehlteId;
        notifyDataSetChanged();
    }

    public void setAusgewaehlteId(String ausgewaehlteId) {
        this.ausgewaehlteId = ausgewaehlteId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAbholortAuswahlBinding binding = ItemAbholortAuswahlBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Adresse adresse = list.get(position);
        holder.bind(adresse, ausgewaehlteId, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAbholortAuswahlBinding binding;

        public ViewHolder(ItemAbholortAuswahlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Adresse adresse, String ausgewaehlteId, Listener listener) {
            binding.tvAuswahlLabel.setText(adresse.getLabel());
            binding.tvAuswahlAdresse.setText(adresse.getAdresse());

            boolean isSelected = adresse.getId() != null && adresse.getId().equals(ausgewaehlteId);
            
            if (isSelected) {
                binding.getRoot().setStrokeColor(Color.parseColor("#4CAF50")); // Green
                binding.tvAuswahlIndikator.setText("✓");
                binding.tvAuswahlIndikator.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                binding.getRoot().setStrokeColor(Color.TRANSPARENT);
                binding.tvAuswahlIndikator.setText("›");
                binding.tvAuswahlIndikator.setTextColor(Color.parseColor("#999999"));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAdresseAusgewaehlt(adresse);
                }
            });
        }
    }
}
