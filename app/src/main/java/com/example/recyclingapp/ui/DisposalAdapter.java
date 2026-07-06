package com.example.recyclingapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.R;
import com.example.recyclingapp.models.DisposalPoint;
import java.util.List;

public class DisposalAdapter extends RecyclerView.Adapter<DisposalAdapter.ViewHolder> {
    private List<DisposalPoint> points;

    public DisposalAdapter(List<DisposalPoint> points) {
        this.points = points;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disposal_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisposalPoint point = points.get(position);
        holder.name.setText(point.getName());
        holder.address.setText(point.getAddress() != null ? point.getAddress() : "OSM Point ID: " + point.getPointId());
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pointName);
            address = itemView.findViewById(R.id.pointAddress);
        }
    }
}
