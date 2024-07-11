package com.example.dcm_stellarsmiles.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Adapter.ReceptionistAppointmentsAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.Intefaces.OnAppointmentActionListener;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReceptionistAppointmentsFragment extends Fragment implements OnAppointmentActionListener {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewAppointments;
    private ReceptionistAppointmentsAdapter adapter;
    private List<Appointment> appointmentList;
    private List<Appointment> filteredAppointmentList;
    private Spinner spinnerCustomers, spinnerStatuses, spinnerAppointmentTypes;
    private Button btnPickDate;
    private String selectedCustomer = "All Customers";
    private String selectedStatus = "All Statuses";
    private String selectedType = "All Types";
    private String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receptionist_appointments, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerViewAppointments = view.findViewById(R.id.recycler_view_appointments);
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        recyclerViewAppointments.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        appointmentList = new ArrayList<>();
        filteredAppointmentList = new ArrayList<>();
        adapter = new ReceptionistAppointmentsAdapter(filteredAppointmentList, this);
        recyclerViewAppointments.setAdapter(adapter);

        spinnerCustomers = view.findViewById(R.id.spinnerCustomers);
        spinnerStatuses = view.findViewById(R.id.spinnerStatuses);
        spinnerAppointmentTypes = view.findViewById(R.id.spinnerAppointmentTypes);
        btnPickDate = view.findViewById(R.id.btnPickDate);

        setupSpinners();
        setupDatePicker();
        fetchCustomerNames();
        fetchAppointments();

        return view;
    }

    private void fetchCustomerNames() {
        db.collection("customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<String> customerNames = new ArrayList<>();
                            customerNames.add("All Customers");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String customerFullName = document.getString("fullName");
                                if (customerFullName != null) {
                                    customerNames.add(customerFullName);
                                }
                            }
                            ArrayAdapter<String> customersAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, customerNames);
                            customersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCustomers.setAdapter(customersAdapter);
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch customer names", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchAppointments() {
        db.collection("appointments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointmentList.add(appointment);
                        }
                        filterAppointments();
                    } else {
                        Log.w("ReceptionistFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    private void setupSpinners() {
        // Populate spinner with appointment statuses
        List<String> statuses = new ArrayList<>();
        statuses.add("All Statuses");
        statuses.add(Constants.APP_COMPLETED);
        statuses.add(Constants.APP_ON_GOING);
        statuses.add(Constants.APP_CANCELED);
        statuses.add("rescheduled");
        ArrayAdapter<String> statusesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, statuses);
        statusesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatuses.setAdapter(statusesAdapter);

        // Populate spinner with appointment types
        List<String> appointmentTypes = new ArrayList<>(Constants.APPOINTMENT_COSTS.keySet());
        appointmentTypes.add(0, "All Types");
        ArrayAdapter<String> appointmentTypesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, appointmentTypes);
        appointmentTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAppointmentTypes.setAdapter(appointmentTypesAdapter);

        spinnerCustomers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCustomer = parent.getItemAtPosition(position).toString();
                filterAppointments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCustomer = "All Customers";
            }
        });

        spinnerStatuses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = parent.getItemAtPosition(position).toString();
                filterAppointments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStatus = "All Statuses";
            }
        });

        spinnerAppointmentTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = parent.getItemAtPosition(position).toString();
                filterAppointments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedType = "All Types";
            }
        });
    }

    private void setupDatePicker() {
        btnPickDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDate = String.format("%02d/%s/%d", dayOfMonth, getMonthFormat(month + 1), year);
                        filterAppointments();
                        Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
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

    private void filterAppointments() {
        filteredAppointmentList.clear();
        for (Appointment appointment : appointmentList) {
            boolean matches = true;

            if (selectedCustomer != null && !selectedCustomer.equals("All Customers")) {
                String patientName = appointment.getPatientName();
                if (patientName == null || !patientName.equals(selectedCustomer)) {
                    matches = false;
                }
            }

            if (selectedStatus != null && !selectedStatus.equals("All Statuses")) {
                String appointmentStatus = appointment.getAppointmentStatus();
                if (appointmentStatus == null || !appointmentStatus.equals(selectedStatus)) {
                    matches = false;
                }
            }

            if (selectedType != null && !selectedType.equals("All Types")) {
                String type = appointment.getType();
                if (type == null || !type.equals(selectedType)) {
                    matches = false;
                }
            }

            if (selectedDate != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                    Date date = sdf.parse(selectedDate);
                    String appointmentDateStr = appointment.getAppointmentDate();
                    Date appointmentDate = appointmentDateStr != null ? sdf.parse(appointmentDateStr) : null;
                    if (appointmentDate == null || !appointmentDate.equals(date)) {
                        matches = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    matches = false;
                }
            }

            if (matches) {
                filteredAppointmentList.add(appointment);
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCancelAppointment(Appointment appointment) {
        updateAppointmentStatus(appointment, "canceled");
    }

    @Override
    public void onRescheduleAppointment(Appointment appointment) {
        RescheduleAppointmentDialogFragment dialogFragment = new RescheduleAppointmentDialogFragment(
                appointment.getDoctor(),
                (newDate, newTime) -> {
                    appointment.setAppointmentDate(newDate);
                    appointment.setTime(newTime);
                    updateAppointmentDate(appointment, newDate, newTime);
                }
        );
        dialogFragment.show(getChildFragmentManager(), "RescheduleAppointmentDialogFragment");
    }

    private void updateAppointmentDate(Appointment appointment, String newDate, String newTime) {
        db.collection("appointments").document(appointment.getAppointmentId())
                .update("appointmentDate", newDate, "time", newTime, "appointmentStatus", "rescheduled")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Appointment rescheduled", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to reschedule appointment", Toast.LENGTH_SHORT).show();
                    }
                });
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
                        if ("completed".equals(status)) {
                            incrementCustomerVisits(appointment.getPatientName());
                        }
                        adapter.notifyDataSetChanged();
                        if (notify) {
                            sendNotification(appointment);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to update appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void incrementCustomerVisits(String patientName) {
        db.collection("customers")
                .whereEqualTo("fullName", patientName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String customerId = document.getId();
                        long currentVisits = document.getLong("visits") != null ? document.getLong("visits") : 0;
                        db.collection("customers").document(customerId)
                                .update("visits", currentVisits + 1)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(getContext(), "Customer visits incremented", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to increment customer visits", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.w("ReceptionistFragment", "Customer not found for appointment: " + patientName);
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
                            sendEmailNotification(email, appointment);
                        }

                        if (phoneNumber != null) {
                            sendSMSNotification(phoneNumber, appointment);
                        }
                    } else {
                        Log.w("ReceptionistFragment", "Customer not found for appointment: " + appointment.getPatientName());
                    }
                });
    }

    private void sendEmailNotification(String email, Appointment appointment) {
        Log.d("Notification", "Sending email to: " + email + " for appointment: " + appointment.getAppointmentId());
    }

    private void sendSMSNotification(String phoneNumber, Appointment appointment) {
        Log.d("Notification", "Sending SMS to: " + phoneNumber + " for appointment: " + appointment.getAppointmentId());
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + "/" + getMonthFormat(month) + "/" + year;
    }
}

