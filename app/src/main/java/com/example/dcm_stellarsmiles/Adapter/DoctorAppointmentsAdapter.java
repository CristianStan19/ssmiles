package com.example.dcm_stellarsmiles.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.R;
import java.util.List;

public class DoctorAppointmentsAdapter extends RecyclerView.Adapter<DoctorAppointmentsAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;

    public DoctorAppointmentsAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.textViewAppointmentDate.setText(appointment.getAppointmentDate());
        holder.textViewType.setText(appointment.getType());
        holder.textViewCost.setText(String.valueOf(appointment.getCost()));
        // Set other fields as necessary
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAppointmentDate, textViewType, textViewCost;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAppointmentDate = itemView.findViewById(R.id.text_view_appointment_date);
            textViewType = itemView.findViewById(R.id.text_view_type);
            textViewCost = itemView.findViewById(R.id.text_view_cost);
            // Initialize other fields as necessary
        }
    }
}
