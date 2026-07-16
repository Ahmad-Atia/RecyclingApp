package com.example.recyclingapp.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.R;
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
            int iconColor;
            int pillDrawable;
            int textColor;

            if (kat.contains("gelb")) {
                iconColor = Color.parseColor("#FFD700"); // Gelber Sack
                pillDrawable = R.drawable.bg_pill_yellow;
                textColor = Color.parseColor("#856404");
            } else if (kat.contains("papier")) {
                iconColor = Color.parseColor("#007BFF"); // Papiertonne
                pillDrawable = R.drawable.bg_pill_blue;
                textColor = Color.parseColor("#004085");
            } else if (kat.contains("bio")) {
                iconColor = Color.parseColor("#28A745"); // Biotonne
                pillDrawable = R.drawable.bg_pill_green;
                textColor = Color.parseColor("#155724");
            } else if (kat.contains("restmüll") || kat.contains("rest")) {
                iconColor = Color.parseColor("#6C757D"); // Restmüll
                pillDrawable = R.drawable.bg_pill_red;
                textColor = Color.parseColor("#721c24");
            } else {
                iconColor = ContextCompat.getColor(itemView.getContext(), R.color.neutral);
                pillDrawable = R.drawable.bg_pill_blue;
                textColor = Color.DKGRAY;
            }

            binding.ivItemIcon.setImageResource(R.drawable.ic_trash);
            binding.ivItemIcon.setColorFilter(iconColor);
            binding.tvItemKategorie.setBackgroundResource(pillDrawable);
            binding.tvItemKategorie.setTextColor(textColor);
            
            // Fixed light gray background for icon container
            binding.iconContainer.setCardBackgroundColor(Color.parseColor("#F1F3F5"));
        }
    }
}
