package com.example.recyclingapp.ui.adapters;

import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.ItemNotificationBinding;
import com.example.recyclingapp.models.AppNotification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<AppNotification> notifications;

    public NotificationAdapter(List<AppNotification> notifications) {
        this.notifications = notifications;
    }

    public void updateList(List<AppNotification> newList) {
        this.notifications = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotification n = notifications.get(position);
        holder.binding.tvTitle.setText(n.getTitle());
        holder.binding.tvMessage.setText(n.getMessage());
        
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(n.getTimestamp().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        holder.binding.tvTime.setText(timeAgo);

        holder.binding.unreadDot.setVisibility(n.isRead() ? View.GONE : View.VISIBLE);

        // Styling based on type
        switch (n.getType()) {
            case PICKUP:
                holder.binding.tvIcon.setText("🚛");
                holder.binding.iconContainer.setBackgroundResource(R.drawable.bg_circle_light_green);
                holder.binding.notificationRoot.setBackgroundColor(Color.TRANSPARENT);
                holder.binding.tvTitle.setTextColor(Color.parseColor("#333333"));
                holder.binding.tvMessage.setTextColor(Color.parseColor("#666666"));
                break;
            case IMPACT:
                holder.binding.tvIcon.setText("⭐");
                holder.binding.iconContainer.setBackgroundResource(R.drawable.bg_circle_pink);
                holder.binding.notificationRoot.setBackgroundColor(Color.TRANSPARENT);
                holder.binding.tvTitle.setTextColor(Color.parseColor("#333333"));
                holder.binding.tvMessage.setTextColor(Color.parseColor("#666666"));
                break;
            case POINTS:
                holder.binding.notificationRoot.setBackgroundResource(R.drawable.bg_notification_points);
                holder.binding.tvTitle.setTextColor(Color.WHITE);
                holder.binding.tvMessage.setTextColor(Color.parseColor("#E0E0E0"));
                holder.binding.tvTime.setTextColor(Color.parseColor("#E0E0E0"));
                holder.binding.tvIcon.setText("🏆");
                holder.binding.iconContainer.setBackgroundResource(R.drawable.bg_circle_light_green); // or similar
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemNotificationBinding binding;
        ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
