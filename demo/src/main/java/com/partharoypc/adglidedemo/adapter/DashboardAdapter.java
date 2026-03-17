package com.partharoypc.adglidedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.model.DashboardItem;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    // Per-position icon tint colors to visually differentiate each ad format card
    private static final int[] ICON_COLORS = {
        R.color.badgeBanner,         // Banner
        R.color.badgeInterstitial,   // Interstitial
        R.color.badgeRewarded,       // Rewarded
        R.color.badgeNative,         // Native
        R.color.badgeRewardedInt,    // Rewarded Interstitial
        R.color.badgeDebug,          // Debug HUD
    };

    private final Context context;
    private final List<DashboardItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DashboardItem item);
    }

    public DashboardAdapter(Context context, List<DashboardItem> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        // Apply a unique tint color to each card's icon container
        if (holder.iconContainer != null) {
            int colorResId = ICON_COLORS[Math.min(position, ICON_COLORS.length - 1)];
            int color = ContextCompat.getColor(context, colorResId);
            // Set alpha-20 tint on the container background
            holder.iconContainer.getBackground().mutate().setAlpha(30);
            holder.icon.setColorFilter(color);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            } else if (item.getActivityClass() != null) {
                context.startActivity(new Intent(context, item.getActivityClass()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView icon;
        FrameLayout iconContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            description = itemView.findViewById(R.id.item_description);
            icon = itemView.findViewById(R.id.item_icon);
            iconContainer = itemView.findViewById(R.id.icon_container);
        }
    }
}
