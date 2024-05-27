package com.example.dcm_stellarsmiles.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dcm_stellarsmiles.Adapter.ReceptionistAppointmentsAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Intefaces.OnAppointmentActionListener;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReceptionistAppointmentsFragment extends Fragment implements OnAppointmentActionListener {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewAppointments;
    private ReceptionistAppointmentsAdapter adapter;
    private List<Appointment> appointmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receptionist_appointments, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerViewAppointments = view.findViewById(R.id.recycler_view_appointments);
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        recyclerViewAppointments.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        appointmentList = new ArrayList<>();
        adapter = new ReceptionistAppointmentsAdapter(appointmentList, this);
        recyclerViewAppointments.setAdapter(adapter);

        fetchAppointments();

        return view;
    }

    private void fetchAppointments() {
        db.collection("appointments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointmentList.add(appointment);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("ReceptionistFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    public void onCancelAppointment(Appointment appointment) {
        updateAppointmentStatus(appointment, "canceled");
    }

    @Override
    public void onRescheduleAppointment(Appointment appointment) {
        showDatePickerDialog(appointment);
    }

    @Override
    public void onCompleteAppointment(Appointment appointment) {
        updateAppointmentStatus(appointment, "completed", true);
    }

    private void updateAppointmentStatus(Appointment appointment, String status) {
        updateAppointmentStatus(appointment, status, false);
    }

    private void updateAppointmentStatus(Appointment appointment, String status, boolean notify) {
        appointment.setAppointmentStatus(status);
        db.collection("appointments").document(appointment.getAppointmentId())
                .update("appointmentStatus", status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Appointment " + status, Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        if (notify) {
                            sendNotification(appointment);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to update appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePickerDialog(Appointment appointment) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String newDate = makeDateString(dayOfMonth, month + 1, year);
                    appointment.setAppointmentDate(newDate);
                    updateAppointmentDate(appointment, newDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateAppointmentDate(Appointment appointment, String newDate) {
        db.collection("appointments").document(appointment.getAppointmentId())
                .update("appointmentDate", newDate, "appointmentStatus", "rescheduled")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Appointment rescheduled", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to reschedule appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNotification(Appointment appointment) {
        db.collection("customers")
                .whereEqualTo("fullName", appointment.getPatientName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String email = document.getString("email");
                        String phoneNumber = document.getString("phoneNumber");

                        if (email != null) {
                            // Send email notification
                            sendEmailNotification(email, appointment);
                        }

                        if (phoneNumber != null) {
                            // Send SMS notification
                            sendSMSNotification(phoneNumber, appointment);
                        }
                    } else {
                        Log.w("ReceptionistFragment", "Customer not found for appointment: " + appointment.getPatientName());
                    }
                });
    }

    private void sendEmailNotification(String email, Appointment appointment) {
        // Implement email sending logic here
        Log.d("Notification", "Sending email to: " + email + " for appointment: " + appointment.getAppointmentId());
    }

    private void sendSMSNotification(String phoneNumber, Appointment appointment) {
        // Implement SMS sending logic here
        Log.d("Notification", "Sending SMS to: " + phoneNumber + " for appointment: " + appointment.getAppointmentId());
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
}
