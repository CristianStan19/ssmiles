package com.example.dcm_stellarsmiles.Fragments;

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
import androidx.fragment.app.DialogFragment;

import com.example.dcm_stellarsmiles.Adapter.CustomSpinnerAdapter;
import com.example.dcm_stellarsmiles.Classes.Schedule.Schedule;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DoctorRescheduleAppointmentsDialogFragment extends DialogFragment {

    private Spinner spinnerDate, spinnerTime;
    private Button btnConfirm;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String doctorName;
    private String patientName;

    private OnRescheduleConfirmedListener listener;

    public interface OnRescheduleConfirmedListener {
        void onRescheduleConfirmed(String newDate, String newTime);
    }

    public DoctorRescheduleAppointmentsDialogFragment(String patientName,String doctorName, OnRescheduleConfirmedListener listener) {
        this.listener = listener;
        this.doctorName = doctorName;
        this.patientName = patientName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_reschedule_appointments_dialog, container, false);

        spinnerDate = view.findViewById(R.id.spinnerDate);
        spinnerTime = view.findViewById(R.id.spinnerTime);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        db = FirebaseFirestore.getInstance();

        fetchSchedulesForDoctor();

        btnConfirm.setOnClickListener(v -> {
            String selectedDate = spinnerDate.getSelectedItem().toString();
            String selectedTime = spinnerTime.getSelectedItem().toString();
            listener.onRescheduleConfirmed(selectedDate, selectedTime);
            dismiss();
        });

        return view;
    }


    private void fetchSchedulesForDoctor() {
        CollectionReference schedulesRef = db.collection("schedules");

        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = calendar.get(Calendar.YEAR);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // Construct the document ID
        String documentID = user.getUid() + "_" + String.format("%02d", month) + "-" + year;

        schedulesRef.document(documentID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Schedule schedule = document.toObject(Schedule.class);
                            List<Schedule> schedules = new ArrayList<>();
                            schedules.add(schedule);
                            setAvailableDatesAndTimes(schedules);
                        } else {
                            Log.w("RescheduleDialog", "No schedule available for document ID: " + documentID);
                        }
                    } else {
                        Log.w("RescheduleDialog", "Error getting schedules: ", task.getException());
                    }
                });
    }

    private void setAvailableDatesAndTimes(List<Schedule> schedules) {
        List<String> availableDays = new ArrayList<>();

        for (Schedule schedule : schedules) {
            availableDays.addAll(schedule.getDays().keySet());
        }

        // Sort dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Collections.sort(availableDays, new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {
                try {
                    return dateFormat.parse(date1).compareTo(dateFormat.parse(date2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        CustomSpinnerAdapter dateAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, availableDays);
        dateAdapter.setDropDownViewResource(R.layout.spinner_list_color);
        spinnerDate.setAdapter(dateAdapter);
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = spinnerDate.getSelectedItem().toString();
                updateAvailableTimeSlots(doctorName, selectedDate, patientName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no date is selected
            }
        });
    }

    private void updateAvailableTimeSlots(String doctorID, String date, String patientName) {
        CollectionReference appointmentsRef = db.collection("appointments");
        CollectionReference schedulesRef = db.collection("schedules");

        // Fetch appointments for the selected date and doctor
        appointmentsRef
                .whereEqualTo("appointmentDate", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> bookedDoctorTimeSlots = new ArrayList<>();
                        List<String> bookedPatientTimeSlots = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appointmentTime = document.getString("time");
                            String appointmentStatus = document.getString("appointmentStatus");
                            String appointmentDoctor = document.getString("doctor");
                            String appointmentPatient = document.getString("patientName");

                            if (!appointmentStatus.equals(Constants.APP_CANCELED)) {
                                if (appointmentDoctor.equals(doctorID)) {
                                    bookedDoctorTimeSlots.add(appointmentTime);
                                }
                                if (appointmentPatient.equals(patientName)) {
                                    bookedPatientTimeSlots.add(appointmentTime);
                                }
                            }
                        }

                        // Fetch schedule for the doctor
                        schedulesRef.whereEqualTo("doctorName", doctorID)
                                .get()
                                .addOnCompleteListener(scheduleTask -> {
                                    if (scheduleTask.isSuccessful() && !scheduleTask.getResult().isEmpty()) {
                                        List<String> availableTimeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot scheduleDoc : scheduleTask.getResult()) {
                                            Schedule schedule = scheduleDoc.toObject(Schedule.class);
                                            if (schedule.getDays().containsKey(date)) {
                                                List<String> intervals = schedule.getDays().get(date);
                                                availableTimeSlots = generateTimeSlotsFromIntervals(intervals);
                                                availableTimeSlots.removeAll(bookedDoctorTimeSlots);
                                                availableTimeSlots.removeAll(bookedPatientTimeSlots);
                                            }
                                        }

                                        if (availableTimeSlots.isEmpty()) {
                                            Toast.makeText(getContext(), "No available time slots for the selected date.", Toast.LENGTH_SHORT).show();
                                        }

                                        CustomSpinnerAdapter timeAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, availableTimeSlots);
                                        timeAdapter.setDropDownViewResource(R.layout.spinner_list_color);
                                        spinnerTime.setAdapter(timeAdapter);
                                    } else {
                                        Log.w("RescheduleDialog", "No schedule found for doctor: " + doctorID);
                                    }
                                });
                    } else {
                        Log.w("RescheduleDialog", "Error getting appointments: ", task.getException());
                    }
                });
    }

    private List<String> generateTimeSlotsFromIntervals(List<String> intervals) {
        List<String> timeSlots = new ArrayList<>();
        for (String interval : intervals) {
            String[] times = interval.split(" - ");
            String startTime = times[0];
            String endTime = times[1];

            int startHour = Integer.parseInt(startTime.split(":")[0]);
            int startMinute = Integer.parseInt(startTime.split(":")[1]);
            int endHour = Integer.parseInt(endTime.split(":")[0]);
            int endMinute = Integer.parseInt(endTime.split(":")[1]);

            while (startHour < endHour || (startHour == endHour && startMinute < endMinute)) {
                timeSlots.add(String.format("%02d:%02d", startHour, startMinute));
                startMinute += 30;
                if (startMinute >= 60) {
                    startMinute -= 60;
                    startHour++;
                }
            }
        }
        return timeSlots;
    }
}
