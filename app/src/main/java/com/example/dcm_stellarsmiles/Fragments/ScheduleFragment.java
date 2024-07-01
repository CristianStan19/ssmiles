package com.example.dcm_stellarsmiles.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import android.app.DatePickerDialog;
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

import com.example.dcm_stellarsmiles.Auth.LogIn;
import com.example.dcm_stellarsmiles.Classes.Schedule.Schedule;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String selectedMonth;
    private FirebaseFirestore db;
    private Map<String, List<String>> availableDaysAndIntervals = new HashMap<>();
    private Map<String, Boolean> unavailableDays = new HashMap<>();
    private Button btnEditSchedule;

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
        } else {
            // Handle the case where user is not logged in
        }

        db = FirebaseFirestore.getInstance();

        btnSaveSchedule = view.findViewById(R.id.btnSaveSchedule);
        btnEditSchedule = view.findViewById(R.id.btnEditSchedule);
        btnDate = view.findViewById(R.id.btnDate);
        layoutDays = view.findViewById(R.id.layoutDays);
        textViewScheduleStatus = view.findViewById(R.id.textViewScheduleStatus);

        btnDate.setOnClickListener(v -> showMonthPickerDialog());

        btnSaveSchedule.setOnClickListener(v -> saveSchedule());

        // Initialize current month
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        String firstMonth = getMonthFormat(month + 1);
        String secondMonth = getMonthFormat(month + 2);

        // Handle the year change for December
        if (month == 11) {
            secondMonth = getMonthFormat(1);
            year++;
        }

        selectedMonth = firstMonth + "-" + secondMonth + " " + year;
        btnDate.setText(selectedMonth);

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
        if(item.getItemId() == R.id.logoutOption) {
            Toast.makeText(getContext(),  R.string.logout, Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LogIn.class);
            startActivity(intent);
            getActivity().finish();
        }
        return true;
    }

    private void saveSchedule() {
        if (selectedMonth == null) {
            Toast.makeText(getContext(), "Please select a month.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] months = selectedMonth.split("-");
        if (months.length != 2) {
            Toast.makeText(getContext(), "Invalid month format", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstMonth = months[0];
        String secondMonthWithYear = months[1];
        String[] secondMonthParts = secondMonthWithYear.split(" ");
        if (secondMonthParts.length != 2) {
            Toast.makeText(getContext(), "Invalid month format", Toast.LENGTH_SHORT).show();
            return;
        }

        String secondMonth = secondMonthParts[0] + " " + secondMonthParts[1];

        Schedule scheduleFirstMonth = new Schedule(doctorID, firstMonth + " " + secondMonthParts[1], availableDaysAndIntervals);
        Schedule scheduleSecondMonth = new Schedule(doctorID, secondMonth, availableDaysAndIntervals);

        db.collection("schedules").document(doctorID + "_" + firstMonth + " " + secondMonthParts[1]).set(scheduleFirstMonth)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("schedules").document(doctorID + "_" + secondMonth).set(scheduleSecondMonth)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Toast.makeText(getContext(), "Schedule saved successfully", Toast.LENGTH_SHORT).show();
                                        lockSchedule();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to save schedule for the second month", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Failed to save schedule for the first month", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void checkIfScheduleExists() {
        String[] months = selectedMonth.split("-");
        if (months.length != 2) {
            Toast.makeText(getContext(), "Invalid month format", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstMonth = months[0];
        String secondMonthWithYear = months[1];
        String[] secondMonthParts = secondMonthWithYear.split(" ");
        if (secondMonthParts.length != 2) {
            Toast.makeText(getContext(), "Invalid month format", Toast.LENGTH_SHORT).show();
            return;
        }

        String secondMonth = secondMonthParts[0] + " " + secondMonthParts[1];

        db.collection("schedules").document(doctorID + "_" + firstMonth + " " + secondMonthParts[1]).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            lockSchedule();
                        } else {
                            db.collection("schedules").document(doctorID + "_" + secondMonth).get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot document2 = task2.getResult();
                                            if (document2.exists()) {
                                                lockSchedule();
                                            } else {
                                                generateUnavailableDaysLayout(firstMonth + " " + secondMonthParts[1], secondMonth);
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Failed to check schedule", Toast.LENGTH_SHORT).show();
                                        }
                                    });
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

        // Hide the save button and show the schedule status
        btnSaveSchedule.setVisibility(View.GONE);
        textViewScheduleStatus.setVisibility(View.VISIBLE);
        textViewScheduleStatus.setText("You have already done your schedule for the month.");

        // Show the edit button
        Button btnEditSchedule = getView().findViewById(R.id.btnEditSchedule);
        btnEditSchedule.setVisibility(View.VISIBLE);
        btnEditSchedule.setOnClickListener(v -> editSchedule());
    }

    private void editSchedule() {
        layoutDays.removeAllViews(); // Clear any existing views
        textViewScheduleStatus.setVisibility(View.GONE);

        String[] months = selectedMonth.split("-");
        if (months.length != 2) {
            Toast.makeText(getContext(), "Invalid month format", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstMonth = months[0];
        String secondMonthWithYear = months[1];
        String[] secondMonthParts = secondMonthWithYear.split(" ");
        if (secondMonthParts.length != 2) {
            Toast.makeText(getContext(), "Invalid month format", Toast.LENGTH_SHORT).show();
            return;
        }

        String secondMonth = secondMonthParts[0] + " " + secondMonthParts[1];

        generateDaysLayoutForMonth(firstMonth + " " + secondMonthParts[1]);
        generateDaysLayoutForMonth(secondMonth);

        // Show save button and hide edit button
        btnSaveSchedule.setVisibility(View.VISIBLE);
        Button btnEditSchedule = getView().findViewById(R.id.btnEditSchedule);
        btnEditSchedule.setVisibility(View.GONE);
    }



    private void showMonthPickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    String firstMonth = getMonthFormat(month + 1);
                    String secondMonth = getMonthFormat(month + 2);

                    // Handle the year change for December
                    if (month == 11) {
                        secondMonth = getMonthFormat(1);
                        year++;
                    }

                    selectedMonth = firstMonth + "-" + secondMonth + " " + year;
                    btnDate.setText(selectedMonth);
                    checkIfScheduleExists();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().findViewById(
                getResources().getIdentifier("android:id/day", null, null)
        ).setVisibility(View.GONE); // Hide the day spinner

        datePickerDialog.show();
    }



    private void generateUnavailableDaysLayout(String firstMonth, String secondMonth) {
        layoutDays.removeAllViews();
        availableDaysAndIntervals.clear();

        generateDaysLayoutForMonth(firstMonth);
        generateDaysLayoutForMonth(secondMonth);
    }

    private void generateDaysLayoutForMonth(String monthYear) {
        String[] parts = monthYear.split(" ");
        int month = getMonthNumber(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] dayAbbr = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (int week = 0; week < 8; week++) {
            final int currentWeek = week; // Capture the current value of week
            for (String day : dayAbbr) {
                String key = day + " - Week " + (currentWeek + 1) + " (" + monthYear + ")";

                LinearLayout dayLayout = new LinearLayout(getContext());
                dayLayout.setOrientation(LinearLayout.VERTICAL);
                dayLayout.setPadding(40, 8, 40, 8);
                dayLayout.setBackgroundColor(getResources().getColor(R.color.lightPurple));

                TextView dayText = new TextView(getContext());
                dayText.setText(day + " - Week " + (currentWeek + 1) + " (" + monthYear + ")");
                dayText.setTextColor(getResources().getColor(R.color.darkPurple));
                dayText.setTextSize(16);
                dayLayout.addView(dayText);

                // Checkbox for "I cannot come this day"
                CheckBox cannotComeCheckbox = new CheckBox(getContext());
                cannotComeCheckbox.setText("I cannot come this day");
                cannotComeCheckbox.setTextColor(getResources().getColor(R.color.darkPurple));
                cannotComeCheckbox.setTextSize(16);

                // Set the checkbox state based on the unavailableDays map
                cannotComeCheckbox.setChecked(unavailableDays.containsKey(key));

                cannotComeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        unavailableDays.put(key, true);
                        availableDaysAndIntervals.remove(key);
                    } else {
                        unavailableDays.remove(key);
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
                        updateAvailableIntervals(day, currentWeek, startHourSpinner, endHourSpinner, monthYear);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                endHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        updateAvailableIntervals(day, currentWeek, startHourSpinner, endHourSpinner, monthYear);
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
    }


    private void updateAvailableIntervals(String day, int week, Spinner startHourSpinner, Spinner endHourSpinner, String monthYear) {
        String startHour = startHourSpinner.getSelectedItem().toString();
        String endHour = endHourSpinner.getSelectedItem().toString();
        String key = day + " - Week " + (week + 1) + " (" + monthYear + ")";

        if (!unavailableDays.containsKey(key)) {
            if (!availableDaysAndIntervals.containsKey(key)) {
                availableDaysAndIntervals.put(key, new ArrayList<>());
            }
            List<String> intervals = availableDaysAndIntervals.get(key);
            intervals.clear(); // Clear previous selection
            intervals.add(startHour + " - " + endHour);
        }
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

    private int getMonthNumber(String month) {
        switch (month) {
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
            default:
                return 1;
        }
    }
}
