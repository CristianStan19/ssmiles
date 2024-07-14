package com.example.dcm_stellarsmiles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Intefaces.OnCancelAppointmentClickListener;
import com.example.dcm_stellarsmiles.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DoctorAppointmentsAdapter extends RecyclerView.Adapter<DoctorAppointmentsAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private OnCancelAppointmentClickListener cancelListener;
    private OnCancelAppointmentClickListener rescheduleListener;
    private FirebaseFirestore db;

    public DoctorAppointmentsAdapter(List<Appointment> appointmentList, OnCancelAppointmentClickListener cancelListener, OnCancelAppointmentClickListener rescheduleListener) {
        this.appointmentList = appointmentList;
        this.cancelListener = cancelListener;
        this.rescheduleListener = rescheduleListener;
        this.db = FirebaseFirestore.getInstance();
        sortAppointments();
    }

    private void sortAppointments() {
        Collections.sort(appointmentList, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getAppointmentStatus().compareTo(o2.getAppointmentStatus());
            }
        });
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
        Context context = holder.itemView.getContext();

        holder.tvAppDate.setText("Date: "+appointment.getAppointmentDate());
        holder.tvAppType.setText("Type: " + appointment.getType());
        holder.tvAppCost.setText("Cost: " + String.valueOf(appointment.getCost()));
        holder.tvAppPatient.setText("Patient: " + appointment.getPatientName());
        holder.tvAppStatus.setText("Status: " + appointment.getAppointmentStatus());
        holder.tvAppDuration.setText("Duration: " + appointment.getDuration());
        holder.tvAppHour.setText("Hour: " + appointment.getTime());
        if (appointment.getAppointmentStatus().equals("completed")) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(appointment.getRating());
        } else {
            holder.ratingBar.setVisibility(View.GONE);
        }

        holder.btnCancelAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                cancelListener.onCancelAppointment(appointment);
                updateAppointmentStatus(appointment, "canceled");
                Toast.makeText(v.getContext(), "Appointment has been canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Cannot cancel appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRescheduleAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                rescheduleListener.onRescheduleAppointment(appointment);
            } else {
                Toast.makeText(v.getContext(), "Cannot reschedule appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private void updateAppointmentStatus(Appointment appointment, String status) {
        appointment.setAppointmentStatus(status);
        db.collection("appointments").document(appointment.getAppointmentId())
                .update("appointmentStatus", status, "appointmentDate", appointment.getAppointmentDate())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(db.getApp().getApplicationContext(), "Appointment updated", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(db.getApp().getApplicationContext(), "Failed to update appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppPatient, tvAppCost, tvAppType, tvAppDate, tvAppStatus, tvAppDuration, tvAppHour;
        Button btnCancelAppointment, btnRescheduleAppointment;
        RatingBar ratingBar; // Add RatingBar

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppCost = itemView.findViewById(R.id.text_view_cost);
            tvAppDate = itemView.findViewById(R.id.text_view_appointment_date);
            tvAppType = itemView.findViewById(R.id.text_view_type);
            tvAppPatient = itemView.findViewById(R.id.text_view_patient);
            tvAppStatus = itemView.findViewById(R.id.text_view_status);
            tvAppDuration = itemView.findViewById(R.id.text_view_duration); // Initialize TextView for duration
            tvAppHour = itemView.findViewById(R.id.text_view_hour); // Initialize TextView for hour
            btnCancelAppointment = itemView.findViewById(R.id.button_cancel);
            btnRescheduleAppointment = itemView.findViewById(R.id.button_reschedule);
            ratingBar = itemView.findViewById(R.id.ratingBar); // Initialize RatingBar
        }
    }
}
