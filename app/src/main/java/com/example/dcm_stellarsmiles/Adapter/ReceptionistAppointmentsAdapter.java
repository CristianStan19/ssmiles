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
import com.example.dcm_stellarsmiles.Intefaces.OnAppointmentActionListener;
import com.example.dcm_stellarsmiles.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReceptionistAppointmentsAdapter extends RecyclerView.Adapter<ReceptionistAppointmentsAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private OnAppointmentActionListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

    public ReceptionistAppointmentsAdapter(List<Appointment> appointmentList, OnAppointmentActionListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_receptionist, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvAppDate.setText(appointment.getAppointmentDate());
        holder.tvAppType.setText(appointment.getType());
        holder.tvAppCost.setText(String.valueOf(appointment.getCost()));
        holder.tvAppPatient.setText(appointment.getPatientName());
        holder.tvAppStatus.setText("Status: " + appointment.getAppointmentStatus());

        holder.btnCancelAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                listener.onCancelAppointment(appointment);
            } else {
                Toast.makeText(v.getContext(), "Cannot cancel appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRescheduleAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                listener.onRescheduleAppointment(appointment);
            } else {
                Toast.makeText(v.getContext(), "Cannot reschedule appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCompleteAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                listener.onCompleteAppointment(appointment);
            } else {
                Toast.makeText(v.getContext(), "Cannot complete appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppPatient, tvAppCost, tvAppType, tvAppDate, tvAppStatus;
        Button btnCancelAppointment, btnRescheduleAppointment, btnCompleteAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppCost = itemView.findViewById(R.id.rec_text_view_cost);
            tvAppDate = itemView.findViewById(R.id.rec_text_view_appointment_date);
            tvAppType = itemView.findViewById(R.id.rec_text_view_type);
            tvAppPatient = itemView.findViewById(R.id.rec_text_view_patient);
            tvAppStatus = itemView.findViewById(R.id.rec_text_view_status);
            btnCancelAppointment = itemView.findViewById(R.id.rec_button_cancel);
            btnRescheduleAppointment = itemView.findViewById(R.id.rec_button_reschedule);
            btnCompleteAppointment = itemView.findViewById(R.id.rec_button_complete);
        }
    }
}
