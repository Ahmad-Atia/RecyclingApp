package com.example.recyclingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.databinding.ItemDisposalPointBinding;
import com.example.recyclingapp.models.DisposalPoint;
import java.util.List;

public class DisposalPointAdapter extends RecyclerView.Adapter<DisposalPointAdapter.ViewHolder> {
    private final List<DisposalPoint> points;

    public DisposalPointAdapter(List<DisposalPoint> points) {
        this.points = points;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDisposalPointBinding binding = ItemDisposalPointBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisposalPoint point = points.get(position);
        holder.binding.pointName.setText(point.getName());
        holder.binding.pointAddress.setText(point.getAddress());
        holder.binding.distanceBadge.setText(String.format("%.0fm", point.getDistance()));
        // In a real app, you'd calculate opening hours logic
        holder.binding.pointHours.setText("🕒 Heute bis 18:00 geöffnet");
    }

    @Override
    public int getItemCount() {
        return points != null ? points.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemDisposalPointBinding binding;
        ViewHolder(ItemDisposalPointBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
