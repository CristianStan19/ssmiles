package com.example.dcm_stellarsmiles.Adapter;

import android.app.DatePickerDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
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

        holder.tvAppDate.setText(appointment.getAppointmentDate());
        holder.tvAppType.setText(appointment.getType());
        holder.tvAppCost.setText(String.valueOf(appointment.getCost()));
        holder.tvAppPatient.setText(appointment.getPatientName());

        holder.btnCancelAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                cancelListener.onCancelAppointment(appointment);
                updateAppointmentStatus(appointment, "canceled");
            } else {
                Toast.makeText(v.getContext(), "Cannot cancel appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRescheduleAppointment.setOnClickListener(v -> {
            if (appointment.getAppointmentStatus().equals("ongoing") || appointment.getAppointmentStatus().equals("rescheduled")) {
                showDatePickerDialog(context, appointment);
            } else {
                Toast.makeText(v.getContext(), "Cannot reschedule appointment. Status is not ongoing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private void showDatePickerDialog(Context context, Appointment appointment) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    String newDate = makeDateString(dayOfMonth, month + 1, year);
                    appointment.setAppointmentDate(newDate);
                    updateAppointmentStatus(appointment, "rescheduled");
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + "/" + getMonthFormat(month) + "/" + year;
    }

    private String getMonthFormat(int month) {
        String monthAbbreviation;
        switch (month) {
            case 1:
                monthAbbreviation = "JAN";
                break;
            case 2:
                monthAbbreviation = "FEB";
                break;
            case 3:
                monthAbbreviation = "MAR";
                break;
            case 4:
                monthAbbreviation = "APR";
                break;
            case 5:
                monthAbbreviation = "MAY";
                break;
            case 6:
                monthAbbreviation = "JUN";
                break;
            case 7:
                monthAbbreviation = "JUL";
                break;
            case 8:
                monthAbbreviation = "AUG";
                break;
            case 9:
                monthAbbreviation = "SEP";
                break;
            case 10:
                monthAbbreviation = "OCT";
                break;
            case 11:
                monthAbbreviation = "NOV";
                break;
            case 12:
                monthAbbreviation = "DEC";
                break;
            default:
                monthAbbreviation = "JAN";
                break;
        }
        return monthAbbreviation;
    }

    private void updateAppointmentStatus(Appointment appointment, String status) {
        appointment.setAppointmentStatus(status);
        db.collection("appointments").document(appointment.getAppointmentId())
                .update("appointmentDate", appointment.getAppointmentDate(),
                        "appointmentStatus", appointment.getAppointmentStatus())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(db.getApp().getApplicationContext(), "Appointment updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(db.getApp().getApplicationContext(), "Failed to update appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppPatient, tvAppCost, tvAppType, tvAppDate;
        Button btnCancelAppointment, btnRescheduleAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppCost = itemView.findViewById(R.id.text_view_cost);
            tvAppDate = itemView.findViewById(R.id.text_view_appointment_date);
            tvAppType = itemView.findViewById(R.id.text_view_type);
            tvAppPatient = itemView.findViewById(R.id.text_view_patient);
            btnCancelAppointment = itemView.findViewById(R.id.button_cancel);
            btnRescheduleAppointment = itemView.findViewById(R.id.button_reschedule);
        }
    }
}
