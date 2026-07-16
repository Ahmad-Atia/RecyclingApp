package com.example.recyclingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.ItemDetectedBinding;
import com.example.recyclingapp.models.Item;
import java.util.List;

public class DetectedItemAdapter extends RecyclerView.Adapter<DetectedItemAdapter.ViewHolder> {
    private final List<Item> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public DetectedItemAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetectedBinding binding = ItemDetectedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.binding.itemName.setText(item.getName());
        holder.binding.itemCategory.setText(item.getCategory());
        
        String category = item.getCategory().toLowerCase();
        int color;
        int dotRes = R.drawable.dot_green;

        if (category.contains("sonder") || category.contains("batterie") || category.contains("akku")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.cat_sondermuell);
            dotRes = R.drawable.dot_red;
        } else if (category.contains("gelb") || category.contains("plastik") || category.contains("wertstoff")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.cat_gelbe_tonne);
        } else if (category.contains("papier")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.cat_papier);
        } else if (category.contains("bio")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.cat_bio);
        } else if (category.contains("glas")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.cat_glas);
        } else if (category.contains("pfand")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.cat_pfand);
        } else if (category.contains("rest")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.cat_restmuell);
            dotRes = R.drawable.dot_red;
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary);
        }

        holder.binding.categoryDot.setBackgroundResource(dotRes);
        holder.binding.itemCategory.setTextColor(color);
        holder.binding.itemIcon.setColorFilter(color);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemDetectedBinding binding;
        ViewHolder(ItemDetectedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
