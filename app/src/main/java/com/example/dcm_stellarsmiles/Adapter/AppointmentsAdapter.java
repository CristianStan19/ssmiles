package com.example.dcm_stellarsmiles.Adapter;

import android.content.Context;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {
    private List<Appointment> appointmentList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnCancelAppointmentClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

    public AppointmentsAdapter(List<Appointment> appointmentList, OnCancelAppointmentClickListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
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
        holder.tvAppDuration.setText(String.format("%s %s", context.getString(R.string.duration), appointment.getDuration()));
        holder.tvAppHour.setText(String.format("%s %s", context.getString(R.string.hour), appointment.getTime()));

        // Show rating bar only for completed appointments
        if (appointment.getAppointmentStatus().equals("completed")) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(appointment.getRating());
        } else {
            holder.ratingBar.setVisibility(View.GONE);
        }

        holder.btnCancelAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                listener.onCancelAppointment(appointment);
            } else {
                Toast.makeText(v.getContext(), "Cannot cancel appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                appointment.setRating(rating);
                updateAppointmentRatingInFirestore(appointment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppStatus, tvAppCost, tvAppDoctor, tvAppDate, tvAppDuration, tvAppHour;
        Button btnCancelAppointment;
        RatingBar ratingBar; // New rating bar

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppCost = itemView.findViewById(R.id.tvAppCost);
            tvAppDate = itemView.findViewById(R.id.tvAppDate);
            tvAppDoctor = itemView.findViewById(R.id.tvAppDoctor);
            tvAppStatus = itemView.findViewById(R.id.tvAppStatus);
            tvAppDuration = itemView.findViewById(R.id.tvAppDuration);
            tvAppHour = itemView.findViewById(R.id.tvAppHour);
            btnCancelAppointment = itemView.findViewById(R.id.btnCancelAppointment);
            ratingBar = itemView.findViewById(R.id.ratingBar); // Initialize rating bar
        }
    }

    private void updateAppointmentRatingInFirestore(Appointment appointment) {
        db.collection("appointments")
                .document(appointment.getAppointmentId())
                .update("rating", appointment.getRating())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("AppointmentsAdapter", "Appointment rating updated in Firestore.");
                        } else {
                            Log.w("AppointmentsAdapter", "Error updating appointment rating in Firestore.", task.getException());
                        }
                    }
                });
    }
}
