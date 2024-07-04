package com.example.dcm_stellarsmiles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Classes.Price.PriceItem;
import com.example.dcm_stellarsmiles.R;

import java.util.List;
import java.util.Map;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {
    private Map<String, List<PriceItem>> priceItemMap;
    private List<String> specializationList;

    public PriceAdapter(Map<String, List<PriceItem>> priceItemMap, List<String> specializationList) {
        this.priceItemMap = priceItemMap;
        this.specializationList = specializationList;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        String specialization = specializationList.get(position);
        List<PriceItem> priceItems = priceItemMap.get(specialization);
        Context context = holder.itemView.getContext();

        holder.specializationTextView.setText(specialization);

        StringBuilder priceDetails = new StringBuilder();
        for (PriceItem item : priceItems) {
            priceDetails.append(String.format("%s: %s%s\n", item.getAppointmentType(), item.getCost(), "$"));
        }

        holder.appointmentDetailsTextView.setText(priceDetails.toString().trim());
    }

    @Override
    public int getItemCount() {
        return specializationList.size();
    }

    public static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView specializationTextView;
        TextView appointmentDetailsTextView;

        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            specializationTextView = itemView.findViewById(R.id.specializationTextView);
            appointmentDetailsTextView = itemView.findViewById(R.id.appointmentDetailsTextView);
        }
    }
}
