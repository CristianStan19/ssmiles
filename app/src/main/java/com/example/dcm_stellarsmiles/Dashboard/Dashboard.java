package com.example.dcm_stellarsmiles.Dashboard;

import static com.example.dcm_stellarsmiles.Constants.Constants.APPOINTMENT_COSTS;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.dcm_stellarsmiles.Auth.LogIn;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Classes.Customer.Customer;
import com.example.dcm_stellarsmiles.Classes.Schedule.Schedule;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.Fragments.AboutUsFragment;
import com.example.dcm_stellarsmiles.Fragments.AppointmentsFragment;
import com.example.dcm_stellarsmiles.Fragments.ConsultationFragment;
import com.example.dcm_stellarsmiles.Fragments.EmployeeFragment;
import com.example.dcm_stellarsmiles.Fragments.HomeFragment;
import com.example.dcm_stellarsmiles.Fragments.PriceFragment;
import com.example.dcm_stellarsmiles.Fragments.ProfileFragment;
import com.example.dcm_stellarsmiles.Fragments.SettingsFragment;
import com.example.dcm_stellarsmiles.Fragments.ShareFragment;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    FirebaseUser user;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    DatePickerDialog datePickerDialog;
    Button btnDate, btnSchedule;
    Spinner consultationDoctor, consultationSpinner;
    Spinner timeIntervalSpinner;
    TextView textCost, textDurationValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            }
            if (item.getItemId() == R.id.Appointments) {
                replaceFragment(new AppointmentsFragment());
            }
            if (item.getItemId() == R.id.employees) {
                replaceFragment(new EmployeeFragment());
            }
            if (item.getItemId() == R.id.price) {
                replaceFragment(new PriceFragment());
            }

            return true;
        });

        fab.setOnClickListener(view -> showBottomDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutOption) {
            Toast.makeText(getApplicationContext(), R.string.logout, Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            replaceFragment(new HomeFragment());
        } else if (menuItem.getItemId() == R.id.nav_about) {
            replaceFragment(new AboutUsFragment());
        } else if (menuItem.getItemId() == R.id.nav_consultation) {
            replaceFragment(new ConsultationFragment());
        } else if (menuItem.getItemId() == R.id.nav_profile) {
            replaceFragment(new ProfileFragment());
        } else if (menuItem.getItemId() == R.id.nav_logout) {
            Toast.makeText(getApplicationContext(), R.string.logout, Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        btnSchedule = dialog.findViewById(R.id.btnSchedule);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        btnDate = dialog.findViewById(R.id.btnDate);
        btnDate.setText(getTodaysDate());
        textCost = dialog.findViewById(R.id.textCost);
        textDurationValue = dialog.findViewById(R.id.textDurationValue);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        consultationSpinner = dialog.findViewById(R.id.consultationSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(Constants.APPOINTMENT_COSTS.keySet()));
        adapter.setDropDownViewResource(R.layout.spinner_list_color);
        consultationSpinner.setAdapter(adapter);
        consultationDoctor = dialog.findViewById(R.id.consultationDoctor);
        timeIntervalSpinner = dialog.findViewById(R.id.timeIntervalSpinner);
        CollectionReference doctorsRef = db.collection("doctors");

        consultationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) parent.getItemAtPosition(position);
                int cost = Constants.APPOINTMENT_COSTS.getOrDefault(type, 0);
                textCost.setText(String.valueOf(cost));
                int duration = Constants.APPOINTMENT_DURATIONS.getOrDefault(type, 0);
                textDurationValue.setText(duration + " minutes");

                String specialization = Constants.APPOINTMENT_SPECIALIZATIONS.get(type);
                if (specialization != null) {
                    doctorsRef.whereEqualTo("specialization", specialization).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    List<String> doctorNames = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String doctorName = document.getString("name");
                                        if (doctorName != null) {
                                            doctorNames.add(doctorName);
                                        }
                                    }
                                    ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(Dashboard.this, android.R.layout.simple_spinner_dropdown_item, doctorNames);
                                    doctorAdapter.setDropDownViewResource(R.layout.spinner_list_color);
                                    consultationDoctor.setAdapter(doctorAdapter);
                                    consultationDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            String selectedDoctor = consultationDoctor.getSelectedItem().toString();
                                            fetchDoctorIDAndSchedules(selectedDoctor);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            // Handle case when no doctor is selected
                                        }
                                    });
                                } else {
                                    Log.w("Dashboard", "Error getting doctors: ", task.getException());
                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textCost.setText("0.00");
                textDurationValue.setText("0 minutes");
            }
        });

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        btnDate.setOnClickListener(v -> datePickerDialog.show());

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            String date = makeDateString(dayOfMonth, month1, year1);
            btnDate.setText(date);
            updateAvailableTimeSlots(consultationDoctor.getSelectedItem().toString(), date);
        };

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                super.onDateChanged(view, year, month, day);
                updateAvailableDates(consultationDoctor.getSelectedItem().toString(), this);
            }
        };

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        btnSchedule.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String customerID = user != null ? user.getUid() : null;

            if (customerID != null) {
                DocumentReference customerDocRef = db.collection("customers").document(customerID);
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(customerDocRef);
                    Customer customer = snapshot.toObject(Customer.class);
                    String appointmentDate = btnDate.getText().toString();
                    String type = consultationSpinner.getSelectedItem().toString();
                    String doctor = consultationDoctor.getSelectedItem().toString();
                    String time = timeIntervalSpinner.getSelectedItem().toString();

                    int cost = Integer.parseInt(textCost.getText().toString());

                    if (customer.getVisits() >= Constants.LOYALITY_REQUIRMENT) {
                        cost = (int) (cost - cost * Constants.LOYALITY_DISCOUNT);
                    }

                    customer.setAppoitnmentsCreated();
                    int appointmentsCreated = customer.getAppoitnmentsCreated();

                    int duration = Constants.APPOINTMENT_DURATIONS.getOrDefault(type, 0);
                    Appointment appointment = new Appointment(appointmentDate, type, doctor, duration);
                    appointment.setCost(cost);
                    appointment.setTime(time);
                    appointment.setAppointmentStatus(Constants.APP_ON_GOING);
                    appointment.setAppointmentId(customerID + appointmentsCreated);
                    appointment.setPatientName(customer.getFullName());

                    transaction.update(customerDocRef, "appoitnmentsCreated", appointmentsCreated);
                    db.collection("appointments").document(appointment.getAppointmentId()).set(appointment)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                }
                            });

                    return null;
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                    } else {
                        // Handle error during update (optional)
                    }
                });
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = androidx.appcompat.R.style.Animation_AppCompat_Dialog;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void fetchDoctorIDAndSchedules(String doctorName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctors").whereEqualTo("name", doctorName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doctorDoc = task.getResult().getDocuments().get(0);
                        String doctorID = doctorDoc.getString("employeeID");
                        if (doctorID != null) {
                            fetchSchedulesForDoctor(doctorID);
                        }
                    } else {
                        Log.w("Dashboard", "No doctor found or error getting doctor: ", task.getException());
                    }
                });
    }

    private void fetchSchedulesForDoctor(String doctorID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference schedulesRef = db.collection("schedules");

        schedulesRef.whereEqualTo("doctorID", doctorID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Schedule> schedules = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Schedule schedule = document.toObject(Schedule.class);
                            schedules.add(schedule);
                        }
                        setAvailableDatesAndTimes(schedules);
                    } else {
                        Log.w("Dashboard", "No schedule available or error getting schedules: ", task.getException());
                    }
                });
    }

    private void setAvailableDatesAndTimes(List<Schedule> schedules) {
        List<Long> availableDates = new ArrayList<>();
        List<String> availableDays = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        for (Schedule schedule : schedules) {
            for (Map.Entry<String, List<String>> entry : schedule.getAvailableDaysAndIntervals().entrySet()) {
                String key = entry.getKey();
                String[] parts = key.split(" - ");
                String day = parts[0];
                String weekAndMonthYear = parts[1];
                String[] weekAndMonthYearParts = weekAndMonthYear.split(" ");
                int week = Integer.parseInt(weekAndMonthYearParts[1]);
                String monthYear = weekAndMonthYearParts[2] + " " + weekAndMonthYearParts[3].replace("(", "").replace(")", "");
                int month = getMonthNumber(monthYear.split(" ")[0]);
                int year = Integer.parseInt(monthYear.split(" ")[1]);

                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.WEEK_OF_MONTH, week);
                cal.set(Calendar.DAY_OF_WEEK, getDayOfWeek(day));
                availableDates.add(cal.getTimeInMillis());
                availableDays.add(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, cal.get(Calendar.DAY_OF_MONTH)));
            }
        }

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(availableDates.stream().min(Long::compare).orElse(System.currentTimeMillis()));
        datePicker.setMaxDate(availableDates.stream().max(Long::compare).orElse(System.currentTimeMillis()));

        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
            if (!availableDays.contains(selectedDate)) {
                Toast.makeText(Dashboard.this, "Selected date is not available.", Toast.LENGTH_SHORT).show();
                datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            } else {
                updateAvailableTimeSlots(consultationDoctor.getSelectedItem().toString(), selectedDate);
            }
        });
    }


    private int getMonthNumber(String month) {
        switch (month.toUpperCase()) {
            case "JAN": return 1;
            case "FEB": return 2;
            case "MAR": return 3;
            case "APR": return 4;
            case "MAY": return 5;
            case "JUN": return 6;
            case "JUL": return 7;
            case "AUG": return 8;
            case "SEP": return 9;
            case "OCT": return 10;
            case "NOV": return 11;
            case "DEC": return 12;
            default: return 1;
        }
    }

    private int getDayOfWeek(String day) {
        switch (day.toUpperCase()) {
            case "MON": return Calendar.MONDAY;
            case "TUE": return Calendar.TUESDAY;
            case "WED": return Calendar.WEDNESDAY;
            case "THU": return Calendar.THURSDAY;
            case "FRI": return Calendar.FRIDAY;
            case "SAT": return Calendar.SATURDAY;
            case "SUN": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }

    private void updateAvailableTimeSlots(String doctorName, String date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmentsRef = db.collection("appointments");
        String type = consultationSpinner.getSelectedItem().toString();
        int duration = Constants.APPOINTMENT_DURATIONS.getOrDefault(type, 0);

        appointmentsRef.whereEqualTo("doctor", doctorName).whereEqualTo("appointmentDate", date).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> availableTimeSlots = generateTimeSlots();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appointmentTime = document.getString("time");
                            String appointmentType = document.getString("type");
                            int appointmentDuration = Constants.APPOINTMENT_DURATIONS.getOrDefault(appointmentType, 0);
                            availableTimeSlots.removeIf(slot -> isOverlapping(slot, appointmentTime, appointmentDuration));
                        }
                        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(Dashboard.this, android.R.layout.simple_spinner_dropdown_item, availableTimeSlots);
                        timeAdapter.setDropDownViewResource(R.layout.spinner_list_color);
                        timeIntervalSpinner.setAdapter(timeAdapter);
                    } else {
                        Log.w("Dashboard", "Error getting appointments: ", task.getException());
                    }
                });
    }

    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                if ((hour == 17 && minute + 30 > 60)) continue;
                timeSlots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        return timeSlots;
    }

    private boolean isOverlapping(String slot, String appointmentTime, int appointmentDuration) {
        int slotHour = Integer.parseInt(slot.split(":")[0]);
        int slotMinute = Integer.parseInt(slot.split(":")[1]);
        int appointmentHour = Integer.parseInt(appointmentTime.split(":")[0]);
        int appointmentMinute = Integer.parseInt(appointmentTime.split(":")[1]);

        int slotEndHour = slotHour + (slotMinute + 30) / 60;
        int slotEndMinute = (slotMinute + 30) % 60;
        int appointmentEndHour = appointmentHour + (appointmentMinute + appointmentDuration) / 60;
        int appointmentEndMinute = (appointmentMinute + appointmentDuration) % 60;

        return !(slotEndHour < appointmentHour || (slotEndHour == appointmentHour && slotEndMinute <= appointmentMinute) ||
                appointmentEndHour < slotHour || (appointmentEndHour == slotHour && appointmentEndMinute <= slotMinute));
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        return makeDateString(day, month, year);
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

    private void updateAvailableDates(String doctorName, DatePickerDialog datePickerDialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference doctorsRef = db.collection("doctors");

        doctorsRef.whereEqualTo("name", doctorName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doctorDoc = task.getResult().getDocuments().get(0);
                        String doctorID = doctorDoc.getString("employeeID");

                        if (doctorID != null) {
                            fetchSchedulesForDoctor(doctorID);
                        }
                    } else {
                        Log.w("Dashboard", "No doctor found or error getting doctor: ", task.getException());
                    }
                });
    }
}
