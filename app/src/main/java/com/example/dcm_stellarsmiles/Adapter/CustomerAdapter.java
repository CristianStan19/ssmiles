package com.example.dcm_stellarsmiles.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<DocumentSnapshot> customerList;

    public CustomerAdapter(List<DocumentSnapshot> customerList) {
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        DocumentSnapshot document = customerList.get(position);
        holder.bind(document);
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFullName, tvBirthDate, tvSmoker, tvDrinker;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvBirthDate = itemView.findViewById(R.id.tvBirthDate);
            tvSmoker = itemView.findViewById(R.id.tvSmoker);
            tvDrinker = itemView.findViewById(R.id.tvDrinker);
        }

        public void bind(DocumentSnapshot document) {
            String fullName = document.getString("fullName");
            String birthDate = document.getString("birthDate");
            Boolean smoker = document.getBoolean("smoker");
            Boolean drinker = document.getBoolean("drinker");

            tvFullName.setText(fullName);
            tvBirthDate.setText(birthDate);
            tvSmoker.setText(smoker != null && smoker ? "Yes" : "No");
            tvDrinker.setText(drinker != null && drinker ? "Yes" : "No");
        }
    }
}
