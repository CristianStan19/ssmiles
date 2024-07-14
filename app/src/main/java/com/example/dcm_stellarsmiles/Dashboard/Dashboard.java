package com.example.dcm_stellarsmiles.Dashboard;

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
import android.widget.Button;
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

import com.example.dcm_stellarsmiles.Adapter.CustomSpinnerAdapter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    FirebaseUser user;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    Spinner spinnerDate, consultationDoctor, consultationSpinner, timeIntervalSpinner;
    TextView textCost, textDurationValue;
    Button btnSchedule;

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
        spinnerDate = dialog.findViewById(R.id.spinnerDate);
        textCost = dialog.findViewById(R.id.textCost);
        textDurationValue = dialog.findViewById(R.id.textDurationValue);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        consultationSpinner = dialog.findViewById(R.id.consultationSpinner);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, new ArrayList<>(Constants.APPOINTMENT_COSTS.keySet()));
        adapter.insert("Select a consultation", 0);
        adapter.setDropDownViewResource(R.layout.spinner_list_color);
        consultationSpinner.setAdapter(adapter);
        consultationDoctor = dialog.findViewById(R.id.consultationDoctor);
        timeIntervalSpinner = dialog.findViewById(R.id.timeIntervalSpinner);
        consultationSpinner.setSelection(0);
        CollectionReference doctorsRef = db.collection("doctors");

        consultationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) parent.getItemAtPosition(position);

                DocumentReference docRef = db.collection("customers").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                long visits = document.getLong("visits");
                                int cost = Constants.APPOINTMENT_COSTS.getOrDefault(type, 0);
                                if(visits >= Constants.LOYALITY_REQUIRMENT) {
                                    cost = (int) (cost - cost * Constants.LOYALITY_DISCOUNT);
                                }
                                textCost.setText(String.valueOf(cost));
                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });


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
                                    CustomSpinnerAdapter doctorAdapter = new CustomSpinnerAdapter(Dashboard.this, android.R.layout.simple_spinner_dropdown_item, doctorNames);
                                    doctorAdapter.setDropDownViewResource(R.layout.spinner_list_color);
                                    consultationDoctor.setAdapter(doctorAdapter);
                                    consultationDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            String selectedDoctor = consultationDoctor.getSelectedItem().toString();
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String customerID = user != null ? user.getUid() : null;

                                            if (customerID != null) {
                                                DocumentReference customerDocRef = db.collection("customers").document(customerID);

                                                // Fetch the customer's full name
                                                customerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                String fullName = document.getString("fullName");
                                                                fetchDoctorIDAndSchedules(selectedDoctor, fullName);
                                                            } else {

                                                            }
                                                        } else {

                                                        }
                                                    }
                                                });
                                            } else {
                                            }
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

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        btnSchedule.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String customerID = user != null ? user.getUid() : null;

            if (customerID != null) {
                DocumentReference customerDocRef = db.collection("customers").document(customerID);
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(customerDocRef);
                    Customer customer = snapshot.toObject(Customer.class);

                    // Get the values from the spinners and text fields
                    String appointmentDate = spinnerDate.getSelectedItem() != null ? spinnerDate.getSelectedItem().toString() : "";
                    String type = consultationSpinner.getSelectedItem() != null ? consultationSpinner.getSelectedItem().toString() : "";
                    String doctor = consultationDoctor.getSelectedItem() != null ? consultationDoctor.getSelectedItem().toString() : "";
                    String time = timeIntervalSpinner.getSelectedItem() != null ? timeIntervalSpinner.getSelectedItem().toString() : "";
                    String costText = textCost.getText().toString();

                    // Check for empty or null values
                    if (appointmentDate.isEmpty() || type.isEmpty() || doctor.isEmpty() || time.isEmpty() || costText.isEmpty() || "Select a consultation".equals(type)) {
                        Toast.makeText(dialog.getContext(), "All fields must be filled!", Toast.LENGTH_SHORT).show();
                        return null;
                    }

                    int cost;
                    try {
                        cost = Integer.parseInt(costText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(dialog.getContext(), "Invalid cost value!", Toast.LENGTH_SHORT).show();
                        return null;
                    }

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
                                    Toast.makeText(dialog.getContext(), "Appointment Scheduled!", Toast.LENGTH_SHORT).show();
                                }
                            });

                    return null;
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                    } else {
                        // Handle error during update (optional)
                        Toast.makeText(dialog.getContext(), "Error scheduling appointment. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(dialog.getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = androidx.appcompat.R.style.Animation_AppCompat_Dialog;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void fetchDoctorIDAndSchedules(String doctorName, String patientName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctors").whereEqualTo("name", doctorName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doctorDoc = task.getResult().getDocuments().get(0);
                        String doctorID = doctorDoc.getString("employeeID");
                        if (doctorID != null) {
                            fetchSchedulesForDoctor(doctorID, patientName);
                        }
                    } else {
                        Log.w("Dashboard", "No doctor found or error getting doctor: ", task.getException());
                    }
                });
    }

    private void fetchSchedulesForDoctor(String doctorID, String patientName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                            setAvailableDatesAndTimes(schedules, patientName);
                        } else {
                            Log.w("Dashboard", "No schedule available for document ID: " + documentID);
                        }
                    } else {
                        Log.w("Dashboard", "Error getting schedules: ", task.getException());
                    }
                });
    }


    private void setAvailableDatesAndTimes(List<Schedule> schedules, String patientName) {
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

        CustomSpinnerAdapter dateAdapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableDays);
        dateAdapter.setDropDownViewResource(R.layout.spinner_list_color);
        spinnerDate.setAdapter(dateAdapter);
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = spinnerDate.getSelectedItem().toString();
                updateAvailableTimeSlots(consultationDoctor.getSelectedItem().toString(), selectedDate, patientName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no date is selected
            }
        });
    }

    private void updateAvailableTimeSlots(String doctorName, String date, String patientName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                if (appointmentDoctor.equals(doctorName)) {
                                    bookedDoctorTimeSlots.add(appointmentTime);
                                }
                                if (appointmentPatient.equals(patientName)) {
                                    bookedPatientTimeSlots.add(appointmentTime);
                                }
                            }
                        }

                        // Fetch schedule for the doctor
                        schedulesRef.whereEqualTo("doctorName", doctorName)
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
                                            Toast.makeText(Dashboard.this, "No available time slots for the selected date.", Toast.LENGTH_SHORT).show();
                                        }

                                        CustomSpinnerAdapter timeAdapter = new CustomSpinnerAdapter(Dashboard.this, android.R.layout.simple_spinner_dropdown_item, availableTimeSlots);
                                        timeAdapter.setDropDownViewResource(R.layout.spinner_list_color);
                                        timeIntervalSpinner.setAdapter(timeAdapter);
                                    } else {
                                        Log.w("Dashboard", "No schedule found for doctor: " + doctorName);
                                    }
                                });
                    } else {
                        Log.w("Dashboard", "Error getting appointments: ", task.getException());
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

    private boolean isOverlapping(String slot, String appointmentTime, int appointmentDuration) {
        int slotHour = Integer.parseInt(slot.split(":")[0]);
        int slotMinute = Integer.parseInt(slot.split(":")[1]);
        int appointmentStartHour = Integer.parseInt(appointmentTime.split(":")[0]);
        int appointmentStartMinute = Integer.parseInt(appointmentTime.split(":")[1]);

        int slotStartMinutes = slotHour * 60 + slotMinute;
        int slotEndMinutes = slotStartMinutes + 30; // Assuming each slot is 30 minutes
        int appointmentStartMinutes = appointmentStartHour * 60 + appointmentStartMinute;
        int appointmentEndMinutes = appointmentStartMinutes + appointmentDuration;

        return (slotStartMinutes < appointmentEndMinutes && slotEndMinutes > appointmentStartMinutes);
    }
}
