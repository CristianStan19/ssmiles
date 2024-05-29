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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Adapter.AppointmentsAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.Intefaces.OnCancelAppointmentClickListener;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentsFragment extends Fragment implements OnCancelAppointmentClickListener {
    private FirebaseFirestore db;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentsAdapter appointmentsAdapter;
    private List<Appointment> appointmentList;
    private List<Appointment> filteredAppointmentList;
    private Spinner spinnerDoctors, spinnerStatuses, spinnerAppointmentTypes;
    private Button btnPickDate;
    private String selectedDoctor = "All Doctors";
    private String selectedStatus = "All Statuses";
    private String selectedType = "All Types";
    private String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and List
        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        spinnerDoctors = view.findViewById(R.id.spinnerDoctors);
        spinnerStatuses = view.findViewById(R.id.spinnerStatuses);
        spinnerAppointmentTypes = view.findViewById(R.id.spinnerAppointmentTypes);
        btnPickDate = view.findViewById(R.id.btnPickDate);

        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        appointmentsRecyclerView.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        appointmentList = new ArrayList<>();
        filteredAppointmentList = new ArrayList<>();
        appointmentsAdapter = new AppointmentsAdapter(filteredAppointmentList, this);
        appointmentsRecyclerView.setAdapter(appointmentsAdapter);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchDoctors();
        setupSpinners();
        setupDatePicker();

        fetchAppointments(userUid);

        return view;
    }

    private void fetchAppointments(String userUid) {
        db.collection("appointments")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), userUid + "0")
                .whereLessThanOrEqualTo(FieldPath.documentId(), userUid + "999")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            appointmentList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointmentList.add(appointment);
                            }
                            filterAppointments();
                        } else {
                            Log.w("AppointmentsFragment", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void fetchDoctors() {
        db.collection("doctors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> doctorNames = new ArrayList<>();
                            doctorNames.add("All Doctors");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String fullName = document.getString("name");
                                if (fullName != null) {
                                    doctorNames.add(fullName);
                                }
                            }
                            ArrayAdapter<String> doctorsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, doctorNames);
                            doctorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDoctors.setAdapter(doctorsAdapter);
                        } else {
                            Log.w("AppointmentsFragment", "Error getting doctors.", task.getException());
                        }
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

        spinnerDoctors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDoctor = parent.getItemAtPosition(position).toString();
                filterAppointments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDoctor = "All Doctors";
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
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
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
            if (selectedDoctor != null && !selectedDoctor.equals("All Doctors") && !appointment.getDoctor().equals(selectedDoctor)) {
                matches = false;
            }
            if (selectedStatus != null && !selectedStatus.equals("All Statuses") && !appointment.getAppointmentStatus().equals(selectedStatus)) {
                matches = false;
            }
            if (selectedType != null && !selectedType.equals("All Types") && !appointment.getType().equals(selectedType)) {
                matches = false;
            }
            if (selectedDate != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                    Date date = sdf.parse(selectedDate);
                    Date appointmentDate = sdf.parse(appointment.getAppointmentDate());
                    if (!appointmentDate.equals(date)) {
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
        appointmentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelAppointment(Appointment appointment) {
        appointment.setAppointmentStatus("canceled");
        updateAppointmentInFirestore(appointment); // Call a new method to update Firestore
    }

    @Override
    public void onRescheduleAppointment(Appointment appointment) {
        // Implement rescheduling functionality if needed
    }

    private void updateAppointmentInFirestore(Appointment appointment) {
        db.collection("appointments")
                .document(appointment.getAppointmentId()) // Assuming Appointment has an id field
                .update("appointmentStatus", "canceled")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("AppointmentsFragment", "Appointment status updated in Firestore.");
                        } else {
                            Log.w("AppointmentsFragment", "Error updating appointment in Firestore.", task.getException());
                        }
                    }
                });
    }
}
