package com.partharoypc.adglidedemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.partharoypc.adglidedemo.R;

import java.util.List;

public class AdapterNetwork extends RecyclerView.Adapter<AdapterNetwork.ViewHolder> {

    private Context context;
    private List<NetworkItem> items;
    private String selectedNetwork;
    private OnItemClickListener onItemClickListener;

    public static class NetworkItem {
        public String name;
        public String adNetworkId;

        public NetworkItem(String name, String adNetworkId) {
            this.name = name;
            this.adNetworkId = adNetworkId;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NetworkItem item);
    }

    public AdapterNetwork(Context context, List<NetworkItem> items, String selectedNetwork,
            OnItemClickListener onItemClickListener) {
        this.context = context;
        this.items = items;
        this.selectedNetwork = selectedNetwork;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_network, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NetworkItem item = items.get(position);
        holder.name.setText(item.name);
        holder.radioSelected.setChecked(item.adNetworkId.equals(selectedNetwork));

        holder.itemView.setOnClickListener(v -> {
            selectedNetwork = item.adNetworkId;
            notifyDataSetChanged();
            onItemClickListener.onItemClick(item);
        });

        holder.radioSelected.setOnClickListener(v -> {
            selectedNetwork = item.adNetworkId;
            notifyDataSetChanged();
            onItemClickListener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedNetwork(String selectedNetwork) {
        this.selectedNetwork = selectedNetwork;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public RadioButton radioSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            radioSelected = itemView.findViewById(R.id.radio_selected);
        }
    }
}
