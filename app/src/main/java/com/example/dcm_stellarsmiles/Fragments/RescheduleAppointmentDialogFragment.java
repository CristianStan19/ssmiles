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
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Classes.Schedule.Schedule;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.R;
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

public class RescheduleAppointmentDialogFragment extends DialogFragment {

    private Spinner spinnerDate, spinnerTime;
    private Button btnConfirm;
    private FirebaseFirestore db;
    private String doctorName;
    private OnRescheduleConfirmedListener listener;
    private String patientName;
    private int duration;

    public interface OnRescheduleConfirmedListener {
        void onRescheduleConfirmed(String newDate, String newTime);
    }

    public RescheduleAppointmentDialogFragment(int duration, String patientName, String doctorName, OnRescheduleConfirmedListener listener) {
        this.doctorName = doctorName;
        this.listener = listener;
        this.patientName = patientName;
        this.duration = duration;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reschedule_appointment_dialog, container, false);

        spinnerDate = view.findViewById(R.id.spinnerDate);
        spinnerTime = view.findViewById(R.id.spinnerTime);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        db = FirebaseFirestore.getInstance();

        fetchDoctorID(doctorName);

        btnConfirm.setOnClickListener(v -> {
            String selectedDate = spinnerDate.getSelectedItem().toString();
            String selectedTime = spinnerTime.getSelectedItem().toString();
            listener.onRescheduleConfirmed(selectedDate, selectedTime);
            dismiss();
        });

        return view;
    }

    private void fetchDoctorID(String doctorName) {
        db.collection("doctors").whereEqualTo("name", doctorName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doctorDoc = task.getResult().getDocuments().get(0);
                        String doctorID = doctorDoc.getString("employeeID");
                        if (doctorID != null) {
                            fetchSchedulesForDoctor(doctorID);
                        }
                    } else {
                        Log.w("RescheduleDialog", "No doctor found or error getting doctor: ", task.getException());
                    }
                });
    }

    private void fetchSchedulesForDoctor(String doctorID) {
        CollectionReference schedulesRef = db.collection("schedules");

        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = calendar.get(Calendar.YEAR);

        // Construct the document ID
        String documentID = doctorID + "_" + String.format("%02d", month) + "-" + year;

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

        appointmentsRef.whereEqualTo("appointmentDate", date).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Appointment> bookedAppointments = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Appointment appointment = document.toObject(Appointment.class);
                    if (!appointment.getAppointmentStatus().equals(Constants.APP_CANCELED)) {
                        bookedAppointments.add(appointment);
                    }
                }

                schedulesRef.whereEqualTo("doctorName", doctorID).get().addOnCompleteListener(scheduleTask -> {
                    if (scheduleTask.isSuccessful() && !scheduleTask.getResult().isEmpty()) {
                        List<String> availableTimeSlots = new ArrayList<>();
                        for (QueryDocumentSnapshot scheduleDoc : scheduleTask.getResult()) {
                            Schedule schedule = scheduleDoc.toObject(Schedule.class);
                            if (schedule.getDays().containsKey(date)) {
                                List<String> intervals = schedule.getDays().get(date);
                                availableTimeSlots = generateTimeSlotsFromIntervals(intervals);

                                // Remove slots that overlap with existing appointments
                                availableTimeSlots = filterOverlappingSlots(availableTimeSlots, bookedAppointments);

                                // Remove slots that exceed 18:00
                                availableTimeSlots = filterExceedingSlots(availableTimeSlots, duration, 18, 0);
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

    private List<String> filterExceedingSlots(List<String> availableTimeSlots, int appointmentDuration, int endHour, int endMinute) {
        List<String> filteredSlots = new ArrayList<>();
        int endInMinutes = endHour * 60 + endMinute;
        for (String slot : availableTimeSlots) {
            if (!exceedsEndTime(slot, appointmentDuration, endInMinutes)) {
                filteredSlots.add(slot);
            }
        }
        return filteredSlots;
    }

    private boolean exceedsEndTime(String slot, int appointmentDuration, int endInMinutes) {
        int slotHour = Integer.parseInt(slot.split(":")[0]);
        int slotMinute = Integer.parseInt(slot.split(":")[1]);

        int slotStartInMinutes = slotHour * 60 + slotMinute;
        int slotEndInMinutes = slotStartInMinutes + appointmentDuration;

        return slotEndInMinutes > endInMinutes;
    }

    private List<String> filterOverlappingSlots(List<String> availableTimeSlots, List<Appointment> bookedAppointments) {
        List<String> filteredSlots = new ArrayList<>(availableTimeSlots);

        for (Appointment appointment : bookedAppointments) {
            String appointmentStartTime = appointment.getTime();
            int appointmentDuration = appointment.getDuration();
            String appointmentEndTime = calculateEndTime(appointmentStartTime, appointmentDuration);

            for (String slot : new ArrayList<>(filteredSlots)) {
                String slotEndTime = calculateEndTime(slot, 30); // Assuming each slot is 30 minutes

                if (timesOverlap(slot, slotEndTime, appointmentStartTime, appointmentEndTime)) {
                    filteredSlots.remove(slot);
                }
            }
        }

        return filteredSlots;
    }

    private boolean timesOverlap(String slotStart, String slotEnd, String appointmentStart, String appointmentEnd) {
        return (slotStart.compareTo(appointmentEnd) < 0 && slotEnd.compareTo(appointmentStart) > 0);
    }

    private String calculateEndTime(String startTime, int duration) {
        String[] timeParts = startTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        minute += duration;
        while (minute >= 60) {
            minute -= 60;
            hour += 1;
        }

        return String.format("%02d:%02d", hour, minute);
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
