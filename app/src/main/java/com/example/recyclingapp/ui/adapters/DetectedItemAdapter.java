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
        
        if (category.contains("sonder")) {
            holder.binding.categoryDot.setBackgroundResource(R.drawable.dot_red);
            holder.binding.itemCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            holder.binding.itemIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else if (category.contains("pfand") || category.contains("wertstoff")) {
            holder.binding.categoryDot.setBackgroundResource(R.drawable.dot_green);
            holder.binding.itemCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            holder.binding.itemIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.binding.categoryDot.setBackgroundResource(R.drawable.dot_green);
            holder.binding.itemCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            holder.binding.itemIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        }

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
