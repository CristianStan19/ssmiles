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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import java.util.Date;
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
    private Map<String, List<CheckBox>> dayCheckBoxes = new HashMap<>();

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
        btnDate = view.findViewById(R.id.btnDate);
        layoutDays = view.findViewById(R.id.layoutDays);
        textViewScheduleStatus = view.findViewById(R.id.textViewScheduleStatus);

        btnDate.setOnClickListener(v -> showMonthPickerDialog());

        btnSaveSchedule.setOnClickListener(v -> saveSchedule());

        // Initialize current month
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        selectedMonth = getMonthFormat(month + 1) + " " + year;
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
        List<String> availableDays = new ArrayList<>();
        for (Map.Entry<String, List<CheckBox>> entry : dayCheckBoxes.entrySet()) {
            for (CheckBox checkBox : entry.getValue()) {
                if (checkBox.isChecked()) {
                    availableDays.add(checkBox.getText().toString());
                }
            }
        }

        if (selectedMonth == null) {
            Toast.makeText(getContext(), "Please select a month.", Toast.LENGTH_SHORT).show();
            return;
        }

        Schedule schedule = new Schedule(doctorID, selectedMonth, availableDays);

        db.collection("schedules").document(doctorID + "_" + selectedMonth).set(schedule)
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
        db.collection("schedules").document(doctorID + "_" + selectedMonth).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            lockSchedule();
                        } else {
                            generateCheckBoxesForMonth(selectedMonth);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to check schedule", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void lockSchedule() {
        // Hide all CheckBox layouts
        layoutDays.setVisibility(View.GONE);
        btnSaveSchedule.setVisibility(View.GONE);

        // Show TextView with schedule status
        textViewScheduleStatus.setVisibility(View.VISIBLE);
        textViewScheduleStatus.setText("You have already done your schedule for the month.");
    }

    private void showMonthPickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedMonth = getMonthFormat(month + 1) + " " + year;
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

    private void generateCheckBoxesForMonth(String monthYear) {
        layoutDays.removeAllViews();
        dayCheckBoxes.clear();

        String[] parts = monthYear.split(" ");
        int month = getMonthNumber(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] dayAbbr = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(year, month - 1, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String dayLabel = dayAbbr[dayOfWeek - 1] + "-" + day;

            LinearLayout dayLayout = new LinearLayout(getContext());
            dayLayout.setOrientation(LinearLayout.HORIZONTAL);
            dayLayout.setPadding(40, 8, 40, 8);
            dayLayout.setBackgroundColor(getResources().getColor(R.color.lightPurple));

            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(dayLabel);
            checkBox.setTextColor(getResources().getColor(R.color.darkPurple));
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);
            checkBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 8);
            checkBox.setLayoutParams(layoutParams);

            dayLayout.addView(checkBox);
            layoutDays.addView(dayLayout);

            List<CheckBox> checkBoxList = dayCheckBoxes.getOrDefault(dayAbbr[dayOfWeek - 1], new ArrayList<>());
            checkBoxList.add(checkBox);
            dayCheckBoxes.put(dayAbbr[dayOfWeek - 1], checkBoxList);
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