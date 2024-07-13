package com.example.dcm_stellarsmiles.Schedule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Adapter.CustomSpinnerAdapter;
import com.example.dcm_stellarsmiles.Adapter.DoctorAppointmentsAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.Fragments.DoctorRescheduleAppointmentsDialogFragment;
import com.example.dcm_stellarsmiles.Fragments.RescheduleAppointmentDialogFragment;
import com.example.dcm_stellarsmiles.Intefaces.OnCancelAppointmentClickListener;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DoctorAppointmentsFragment extends Fragment implements OnCancelAppointmentClickListener {

    private RecyclerView recyclerView;
    private DoctorAppointmentsAdapter adapter;
    private List<Appointment> appointmentList;
    private List<Appointment> filteredAppointmentList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Spinner spinnerCustomers, spinnerStatuses, spinnerAppointmentTypes, spinnerDates;
    private Button btnPickDate;
    private String selectedCustomer = "All Customers";
    private String selectedStatus = "All Statuses";
    private String selectedType = "All Types";
    private String selectedDate = "All Dates";

    public DoctorAppointmentsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        appointmentList = new ArrayList<>();
        filteredAppointmentList = new ArrayList<>();
        adapter = new DoctorAppointmentsAdapter(filteredAppointmentList, this, this);
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        spinnerCustomers = view.findViewById(R.id.spinnerCustomers);
        spinnerStatuses = view.findViewById(R.id.spinnerStatuses);
        spinnerAppointmentTypes = view.findViewById(R.id.spinnerAppointmentTypes);
        spinnerDates = view.findViewById(R.id.spinnerDates);

        setupSpinners();
        fetchCustomerNames();
        fetchDoctorAppointments();
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
                            CustomSpinnerAdapter customersAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, customerNames);
                            customersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCustomers.setAdapter(customersAdapter);
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch customer names", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchDoctorAppointments() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUID = user.getUid();
        db.collection("doctors").document(userUID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String doctorName = document.getString("name");
                    if (doctorName != null) {
                        fetchAppointmentsForDoctor(doctorName);
                    } else {
                        Toast.makeText(getContext(), "Doctor's name not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Doctor document does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to fetch doctor details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAppointmentsForDoctor(String doctorName) {
        CollectionReference appointmentsRef = db.collection("appointments");
        appointmentsRef.whereEqualTo("doctor", doctorName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            appointmentList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointmentList.add(appointment);
                            }
                            fetchDates();
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch appointments", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchDates() {
        List<String> dates = new ArrayList<>();
        dates.add("All Dates");
        for (Appointment appointment : appointmentList) {
            String date = appointment.getAppointmentDate();
            if (date != null && !dates.contains(date)) {
                dates.add(date);
            }
        }
        CustomSpinnerAdapter datesAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, dates);
        datesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDates.setAdapter(datesAdapter);
        spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = parent.getItemAtPosition(position).toString();
                filterAppointments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDate = "All Dates";
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
        CustomSpinnerAdapter statusesAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, statuses);
        statusesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatuses.setAdapter(statusesAdapter);

        // Populate spinner with appointment types
        List<String> appointmentTypes = new ArrayList<>(Constants.APPOINTMENT_COSTS.keySet());
        appointmentTypes.add(0, "All Types");
        CustomSpinnerAdapter appointmentTypesAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, appointmentTypes);
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

    private void filterAppointments() {
        filteredAppointmentList.clear();
        for (Appointment appointment : appointmentList) {
            boolean matches = true;
            if (selectedCustomer != null && !selectedCustomer.equals("All Customers") && !appointment.getPatientName().equals(selectedCustomer)) {
                matches = false;
            }
            if (selectedStatus != null && !selectedStatus.equals("All Statuses") && !appointment.getAppointmentStatus().equals(selectedStatus)) {
                matches = false;
            }
            if (selectedType != null && !selectedType.equals("All Types") && !appointment.getType().equals(selectedType)) {
                matches = false;
            }
            if (selectedDate != null && !selectedDate.equals("All Dates")) {
                if (!appointment.getAppointmentDate().equals(selectedDate)) {
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
        appointment.setAppointmentStatus("canceled");
        updateAppointmentInFirestore(appointment); // Call a new method to update Firestore
    }

    public void onRescheduleAppointment(Appointment appointment) {
        DoctorRescheduleAppointmentsDialogFragment dialogFragment = new DoctorRescheduleAppointmentsDialogFragment(
                appointment.getDoctor(),
                (newDate, newTime) -> {
                    appointment.setAppointmentDate(newDate);
                    appointment.setTime(newTime);
                    updateAppointmentDate(appointment, newDate, newTime);
                }
        );
        dialogFragment.show(getChildFragmentManager(), "DoctorRescheduleAppointmentsDialogFragment");
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

    private void updateAppointmentInFirestore(Appointment appointment) {
        db.collection("appointments")
                .document(appointment.getAppointmentId())
                .update("appointmentStatus", appointment.getAppointmentStatus(), "appointmentDate", appointment.getAppointmentDate())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DoctorAppointmentsFragment", "Appointment updated in Firestore.");
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("DoctorAppointmentsFragment", "Error updating appointment in Firestore.", task.getException());
                        }
                    }
                });
    }
}
