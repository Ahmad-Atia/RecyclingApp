package com.example.recyclingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.databinding.ItemScanVerlaufBinding;
import com.example.recyclingapp.models.ScanVerlaufEintrag;
import java.util.ArrayList;
import java.util.List;

public class ScanVerlaufAdapter extends RecyclerView.Adapter<ScanVerlaufAdapter.ViewHolder> {

    private final List<ScanVerlaufEintrag> list = new ArrayList<>();

    public void submitList(List<ScanVerlaufEintrag> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScanVerlaufBinding binding = ItemScanVerlaufBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemScanVerlaufBinding binding;

        public ViewHolder(ItemScanVerlaufBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ScanVerlaufEintrag eintrag) {
            binding.tvItemTitel.setText(eintrag.getTitel());
            binding.tvItemZeit.setText(eintrag.getZeitangabe());
            binding.tvItemKategorie.setText(eintrag.getKategorie().toUpperCase());
            
            String kat = eintrag.getKategorie().toLowerCase();
            if (kat.contains("gelb")) {
                binding.tvItemIcon.setText("🟡");
            } else if (kat.contains("papier")) {
                binding.tvItemIcon.setText("🔵");
            } else if (kat.contains("bio")) {
                binding.tvItemIcon.setText("🟢");
            } else if (kat.contains("restmüll")) {
                binding.tvItemIcon.setText("⚫");
            } else {
                binding.tvItemIcon.setText("📦");
            }
        }
    }
}
