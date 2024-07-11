package com.example.dcm_stellarsmiles.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.dcm_stellarsmiles.Auth.LogIn;
import com.example.dcm_stellarsmiles.Classes.Schedule.Schedule;
import com.example.dcm_stellarsmiles.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleFragment extends Fragment {
    private LinearLayout layoutDays;
    private Button btnSaveSchedule, btnDate;
    private TextView textViewScheduleStatus;
    private String doctorID;
    private String doctorName;
    private FirebaseFirestore db;
    private Map<String, List<String>> availableDaysAndIntervals = new HashMap<>();
    private Map<String, Boolean> unavailableDays = new HashMap<>();
    private Button btnEditSchedule;
    private Calendar selectedDate;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EdgeToEdge.enable(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        LinearLayout layout_schedule = view.findViewById(R.id.layout_schedule);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            doctorID = user.getUid();

            // Initialize Firestore
            db = FirebaseFirestore.getInstance();

            // Reference the doctor document using the doctorID
            DocumentReference docRef = db.collection("doctors").document(doctorID);

            // Fetch the document
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the doctor's name from the document
                        doctorName = document.getString("name");
                    } else {
                        Toast.makeText(getContext(), "Doctor's document does not exist.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to get doctor's document: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where user is not logged in
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
        }

        btnSaveSchedule = view.findViewById(R.id.btnSaveSchedule);
        btnEditSchedule = view.findViewById(R.id.btnEditSchedule);
        btnDate = view.findViewById(R.id.btnDate);
        layoutDays = view.findViewById(R.id.layoutDays);
        textViewScheduleStatus = view.findViewById(R.id.textViewScheduleStatus);

        btnDate.setOnClickListener(v -> showMonthPickerDialog());

        btnSaveSchedule.setOnClickListener(v -> saveSchedule());

        // Initialize current date
        selectedDate = Calendar.getInstance();
        updateDateButton();

        checkIfScheduleExists();

        ViewCompat.setOnApplyWindowInsetsListener(layout_schedule, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutOption) {
            Toast.makeText(getContext(), R.string.logout, Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LogIn.class);
            startActivity(intent);
            getActivity().finish();
        }
        return true;
    }

    private void saveSchedule() {
        if (selectedDate == null) {
            Toast.makeText(getContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedDateStr = getFormattedMonth(selectedDate);

        Schedule schedule = new Schedule(doctorName, selectedDateStr, availableDaysAndIntervals, unavailableDays);

        db.collection("schedules").document(doctorID + "_" + selectedDateStr).set(schedule)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Schedule saved successfully", Toast.LENGTH_SHORT).show();
                        lockSchedule();
                    } else {
                        Toast.makeText(getContext(), "Failed to save schedule", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfScheduleExists() {
        if (selectedDate == null) {
            return;
        }

        String selectedDateStr = getFormattedMonth(selectedDate);

        db.collection("schedules").document(doctorID + "_" + selectedDateStr).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Schedule schedule = document.toObject(Schedule.class);
                            if (schedule != null) {
                                availableDaysAndIntervals = schedule.getDays();
                                unavailableDays = schedule.getUnavailableDays() != null ? schedule.getUnavailableDays() : new HashMap<>();
                                lockSchedule();
                            }
                        } else {
                            generateUnavailableDaysLayout();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to check schedule", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void lockSchedule() {
        layoutDays.removeAllViews(); // Clear any existing views

        // Show the days the doctor marked as unavailable
        for (String day : unavailableDays.keySet()) {
            if (unavailableDays.get(day)) {
                LinearLayout dayLayout = new LinearLayout(getContext());
                dayLayout.setOrientation(LinearLayout.HORIZONTAL);
                dayLayout.setPadding(16, 8, 16, 8);

                TextView dayText = new TextView(getContext());
                dayText.setText(day);
                dayText.setTextColor(getResources().getColor(R.color.darkPurple));
                dayText.setTextSize(16);

                dayLayout.addView(dayText);
                layoutDays.addView(dayLayout);
            }
        }

        // Hide the save button and show the schedule status
        btnSaveSchedule.setVisibility(View.GONE);
        textViewScheduleStatus.setVisibility(View.VISIBLE);
        textViewScheduleStatus.setText("You have already done your schedule for the selected month.");

        // Show the edit button
        btnEditSchedule.setVisibility(View.VISIBLE);
        btnEditSchedule.setOnClickListener(v -> editSchedule());
    }


    private void editSchedule() {
        layoutDays.removeAllViews(); // Clear any existing views
        textViewScheduleStatus.setVisibility(View.GONE);

        generateDaysLayoutForSelectedMonth();

        // Show save button and hide edit button
        btnSaveSchedule.setVisibility(View.VISIBLE);
        btnEditSchedule.setVisibility(View.GONE);
    }

    private void showMonthPickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateButton();
                    checkIfScheduleExists();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateButton() {
        btnDate.setText(getFormattedMonth(selectedDate));
    }

    private void generateUnavailableDaysLayout() {
        layoutDays.removeAllViews();
        availableDaysAndIntervals.clear();

        generateDaysLayoutForSelectedMonth();
    }



    private void generateDaysLayoutForSelectedMonth() {
        layoutDays.removeAllViews();

        // Generate layout for all days of the current month and the next month
        Calendar currentMonth = (Calendar) selectedDate.clone();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar nextMonth = (Calendar) currentMonth.clone();
        nextMonth.add(Calendar.MONTH, 1);

        addDaysToLayout(currentMonth);
        addDaysToLayout(nextMonth);
    }

    private void addDaysToLayout(Calendar month) {
        int daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();

        for (int day = 1; day <= daysInMonth; day++) {
            month.set(Calendar.DAY_OF_MONTH, day);

            if (month.before(today)) {
                continue; // Skip past days
            }

            String dateStr = getFormattedDate(month);

            LinearLayout dayLayout = new LinearLayout(getContext());
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            dayLayout.setPadding(40, 8, 40, 8);
            dayLayout.setBackgroundColor(getResources().getColor(R.color.lightPurple));

            TextView dayText = new TextView(getContext());
            dayText.setText(dateStr);
            dayText.setTextColor(getResources().getColor(R.color.darkPurple));
            dayText.setTextSize(16);
            dayLayout.addView(dayText);

            // Checkbox for "I cannot come this day"
            CheckBox cannotComeCheckbox = new CheckBox(getContext());
            cannotComeCheckbox.setText("I cannot come this day");
            cannotComeCheckbox.setTextColor(getResources().getColor(R.color.darkPurple));
            cannotComeCheckbox.setTextSize(16);

            // Set the checkbox state based on the unavailableDays map
            cannotComeCheckbox.setChecked(unavailableDays.containsKey(dateStr) && unavailableDays.get(dateStr));

            cannotComeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    unavailableDays.put(dateStr, true);
                    availableDaysAndIntervals.remove(dateStr);
                } else {
                    unavailableDays.remove(dateStr);
                }
            });

            // Spinner for selecting start hour
            Spinner startHourSpinner = new Spinner(getContext());
            List<String> startHours = new ArrayList<>();
            for (int hour = 9; hour < 18; hour++) {
                startHours.add(hour + ":00");
            }
            ArrayAdapter<String> startHourAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, startHours);
            startHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startHourSpinner.setAdapter(startHourAdapter);

            // Spinner for selecting end hour
            Spinner endHourSpinner = new Spinner(getContext());
            List<String> endHours = new ArrayList<>();
            for (int hour = 10; hour <= 18; hour++) {
                endHours.add(hour + ":00");
            }
            ArrayAdapter<String> endHourAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, endHours);
            endHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            endHourSpinner.setAdapter(endHourAdapter);
            endHourSpinner.setSelection(endHours.size() - 1); // Set default selection to 18:00

            LinearLayout timeSelectionLayout = new LinearLayout(getContext());
            timeSelectionLayout.setOrientation(LinearLayout.HORIZONTAL);
            timeSelectionLayout.addView(startHourSpinner);
            timeSelectionLayout.addView(endHourSpinner);

            startHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateAvailableIntervals(dateStr, startHourSpinner, endHourSpinner);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            endHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateAvailableIntervals(dateStr, startHourSpinner, endHourSpinner);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            dayLayout.addView(cannotComeCheckbox);
            dayLayout.addView(timeSelectionLayout);
            layoutDays.addView(dayLayout);
        }
    }
    private void updateAvailableIntervals(String date, Spinner startHourSpinner, Spinner endHourSpinner) {
        String startHour = startHourSpinner.getSelectedItem().toString();
        String endHour = endHourSpinner.getSelectedItem().toString();

        if (!unavailableDays.containsKey(date)) {
            if (!availableDaysAndIntervals.containsKey(date)) {
                availableDaysAndIntervals.put(date, new ArrayList<>());
            }
            List<String> intervals = availableDaysAndIntervals.get(date);
            intervals.clear(); // Clear previous selection
            intervals.add(startHour + " - " + endHour);
        }
    }

    private String getFormattedMonth(Calendar date) {
        int month = date.get(Calendar.MONTH) + 1;
        int year = date.get(Calendar.YEAR);

        return String.format("%02d-%04d", month, year);
    }

    private String getFormattedDate(Calendar date) {
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;
        int year = date.get(Calendar.YEAR);

        return String.format("%02d-%02d-%04d", day, month, year);
    }
}

