package com.example.recyclingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.databinding.ItemAdresseBinding;
import com.example.recyclingapp.models.Adresse;
import java.util.ArrayList;
import java.util.List;

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

            binding.btnEditAdresse.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(adresse);
            });

            binding.btnDeleteAdresse.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(adresse);
            });
        }
    }
}
