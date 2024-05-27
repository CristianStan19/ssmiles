package com.example.dcm_stellarsmiles.Dashboard;

import static com.example.dcm_stellarsmiles.Constants.Constants.APPOINTMENT_COSTS;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
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
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.Enum.AppointmentStatus;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth auth;
    FirebaseUser user;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    DatePickerDialog datePickerDialog;
    Button btnDate, btnSchedule;
    Spinner consultationDoctor, consultationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user==null){
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationVIew = findViewById(R.id.nav_view);
        navigationVIew.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home)
            {
                replaceFragment(new HomeFragment());
            }
            if(item.getItemId() == R.id.Appointments)
            {
                replaceFragment(new AppointmentsFragment());
            }
            if(item.getItemId() == R.id.employees)
            {
                replaceFragment(new EmployeeFragment());
            }
            if(item.getItemId() == R.id.price)
            {
                replaceFragment(new PriceFragment());
            }

            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });



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
        if(item.getItemId() == R.id.logoutOption) {
            Toast.makeText(getApplicationContext(),  R.string.logout, Toast.LENGTH_SHORT).show();
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
        TextView textCost = dialog.findViewById(R.id.textCost);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        consultationSpinner = dialog.findViewById(R.id.consultationSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(Constants.APPOINTMENT_COSTS.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        consultationSpinner.setAdapter(adapter);
        consultationDoctor = dialog.findViewById(R.id.consultationDoctor);
        CollectionReference doctorsRef = db.collection("doctors");

        consultationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) parent.getItemAtPosition(position);
                int cost = Constants.APPOINTMENT_COSTS.getOrDefault(type, 0);
                textCost.setText(String.valueOf(cost));

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
                                    consultationDoctor.setAdapter(doctorAdapter);
                                } else {
                                    Log.w("Dashboard", "Error getting doctors: ", task.getException());
                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textCost.setText("0.00");
            }
        });

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = makeDateString(dayOfMonth, month, year);
            btnDate.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        // Set the minimum date to today to restrict past dates
        cal.set(year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

        // Set the maximum date to the last day of the current month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        btnSchedule.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String customerID = user != null ? user.getUid() : null;

            if (customerID != null) {
                DocumentReference customerDocRef = db.collection("customers").document(customerID);
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(customerDocRef);
                    Customer customer = snapshot.toObject(Customer.class);
                    int newVisits = customer.getVisits() + 1;
                    customer.setVisits(newVisits);
                    transaction.update(customerDocRef, "visits", newVisits);
                    String appointmentDate = btnDate.getText().toString();
                    String type = consultationSpinner.getSelectedItem().toString();
                    String doctor = consultationDoctor.getSelectedItem().toString();
                    int cost = Integer.parseInt(textCost.getText().toString());
                    if (customer.getVisits() >= Constants.LOYALITY_REQUIRMENT) {
                        cost = (int) (cost - cost * Constants.LOYALITY_DISCOUNT);
                    }
                    Appointment appointment = new Appointment(appointmentDate, type, doctor);
                    appointment.setCost(cost);
                    appointment.setAppointmentStatus(Constants.APP_ON_GOING);
                    appointment.setAppointmentId(customerID + customer.getVisits());
                    appointment.setPatientName(customer.getFullName());
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


    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month+1;
        return makeDateString(day, month, year);
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + "/" + getMonthFormat(month) + "/" + year;
    }

    private String getMonthFormat(int month)
    {
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

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
}
