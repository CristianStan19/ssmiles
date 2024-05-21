package com.example.dcm_stellarsmiles.Schedule;

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
import java.util.List;

public class ScheduleFragment extends Fragment {
    private CheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday, checkBoxSunday;
    private LinearLayout layoutMonday, layoutTuesday, layoutWednesday, layoutThursday, layoutFriday, layoutSaturday, layouSunday;
    private Button btnSaveSchedule, btnDate;
    private TextView textViewScheduleStatus;
    private String doctorID;
    private String selectedWeek;
    private FirebaseFirestore db;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        checkBoxMonday = view.findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = view.findViewById(R.id.checkBoxTuesday);
        checkBoxWednesday = view.findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = view.findViewById(R.id.checkBoxThursday);
        checkBoxFriday = view.findViewById(R.id.checkBoxFriday);
        checkBoxSaturday = view.findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = view.findViewById(R.id.checkBoxSunday);
        btnSaveSchedule = view.findViewById(R.id.btnSaveSchedule);
        btnDate = view.findViewById(R.id.btnDate);
        layoutMonday = view.findViewById(R.id.layoutMonday);
        layoutTuesday = view.findViewById(R.id.layoutTuesday);
        layoutWednesday = view.findViewById(R.id.layoutWednesday);
        layoutThursday = view.findViewById(R.id.layoutThursday);
        layoutFriday = view.findViewById(R.id.layoutFriday);
        layoutSaturday = view.findViewById(R.id.layoutSaturday);
        layouSunday = view.findViewById(R.id.layoutSunday);
        textViewScheduleStatus = view.findViewById(R.id.textViewScheduleStatus);

        btnSaveSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchedule();
            }
        });

        // Initialize current week
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // Month is 0-based
        int day = cal.get(Calendar.DAY_OF_MONTH);

        selectedWeek = year + "-W" + getWeekOfYear(year, month, day);
        btnDate.setText(selectedWeek);

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
        if (checkBoxMonday.isChecked()) availableDays.add("Monday");
        if (checkBoxTuesday.isChecked()) availableDays.add("Tuesday");
        if (checkBoxWednesday.isChecked()) availableDays.add("Wednesday");
        if (checkBoxThursday.isChecked()) availableDays.add("Thursday");
        if (checkBoxFriday.isChecked()) availableDays.add("Friday");
        if (checkBoxSaturday.isChecked()) availableDays.add("Saturday");
        if (checkBoxSunday.isChecked()) availableDays.add("Sunday");

        if (selectedWeek == null) {
            Toast.makeText(getContext(), "Please select a week.", Toast.LENGTH_SHORT).show();
            return;
        }

        Schedule schedule = new Schedule(doctorID, selectedWeek, availableDays);

        db.collection("schedules").document(doctorID + "_" + selectedWeek).set(schedule)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Schedule saved successfully", Toast.LENGTH_SHORT).show();
                            lockSchedule();
                        } else {
                            Toast.makeText(getContext(), "Failed to save schedule", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfScheduleExists() {
        db.collection("schedules").document(doctorID + "_" + selectedWeek).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                lockSchedule();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to check schedule", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void lockSchedule() {
        // Hide all CheckBox layouts
        layoutMonday.setVisibility(View.GONE);
        layoutTuesday.setVisibility(View.GONE);
        layoutThursday.setVisibility(View.GONE);
        layoutWednesday.setVisibility(View.GONE);
        layoutFriday.setVisibility(View.GONE);
        layoutSaturday.setVisibility(View.GONE);
        layouSunday.setVisibility(View.GONE);
        btnSaveSchedule.setVisibility(View.GONE);

        // Show TextView with schedule status
        textViewScheduleStatus.setVisibility(View.VISIBLE);
        textViewScheduleStatus.setText("You have already done your schedule for the week");
    }


    private int getWeekOfYear(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, dayOfMonth);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
}
