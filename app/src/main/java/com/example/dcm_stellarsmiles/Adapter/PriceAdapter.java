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

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {
    private List<PriceItem> priceItemList;

    public PriceAdapter(List<PriceItem> priceItemList) {
        this.priceItemList = priceItemList;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        PriceItem priceItem = priceItemList.get(position);
        Context context = holder.itemView.getContext();

        holder.appointmentTypeTextView.setText(priceItem.getAppointmentType());
        holder.appointmentCostTextView.setText(String.format("%s %s", context.getString(R.string.appointment_cost), priceItem.getCost()));
    }


    @Override
    public int getItemCount() {
        return priceItemList.size();
    }

    public static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentTypeTextView;
        TextView appointmentCostTextView;

        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentTypeTextView = itemView.findViewById(R.id.appointmentTypeTextView);
            appointmentCostTextView = itemView.findViewById(R.id.appointmentCostTextView);
        }
    }
}
