package com.example.dcm_stellarsmiles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Intefaces.OnCancelAppointmentClickListener;
import com.example.dcm_stellarsmiles.R;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {
    private List<Appointment> appointmentList;
    private OnCancelAppointmentClickListener listener;

    public AppointmentsAdapter(List<Appointment> appointmentList, OnCancelAppointmentClickListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_appointments_row, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvAppStatus.setText(String.format("%s %s", context.getString(R.string.appointment_status), appointment.getAppointmentStatus()));
        holder.tvAppDate.setText(String.format("%s %s", context.getString(R.string.appointment_date), appointment.getAppointmentDate()));
        holder.tvAppDoctor.setText(String.format("%s %s", context.getString(R.string.doctor), appointment.getDoctor()));
        holder.tvAppCost.setText(String.format("%s %s", context.getString(R.string.appointment_cost), appointment.getCost()));

        holder.btnCancelAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appointment.getAppointmentStatus().equals("ongoing")) {
                    listener.onCancelAppointment(appointment);
                } else {
                    // Appointment is not ongoing, show a message or perform other action
                    Toast.makeText(v.getContext(), "Cannot cancel appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppStatus, tvAppCost, tvAppDoctor, tvAppDate;
        Button btnCancelAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppCost = itemView.findViewById(R.id.tvAppCost);
            tvAppDate = itemView.findViewById(R.id.tvAppDate);
            tvAppDoctor = itemView.findViewById(R.id.tvAppDoctor);
            tvAppStatus = itemView.findViewById(R.id.tvAppStatus);
            btnCancelAppointment = itemView.findViewById(R.id.btnCancelAppointment);
        }
    }
}

