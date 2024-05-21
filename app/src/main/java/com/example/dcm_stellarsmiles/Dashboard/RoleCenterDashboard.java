package com.example.dcm_stellarsmiles.Dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dcm_stellarsmiles.Auth.LogIn;
import com.example.dcm_stellarsmiles.Classes.Employees.Doctor;
import com.example.dcm_stellarsmiles.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class RoleCenterDashboard extends AppCompatActivity {

    private Spinner spinnerDoctors, spinnerAssistants, spinnerReceptionists;
    private Button buttonAdd, buttonDelete;
    private FirebaseFirestore db;
    private List<String> doctorIds = new ArrayList<>();
    private List<String> assistantIds = new ArrayList<>();
    private List<String> receptionistIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role_center_dashboard);
        LinearLayout rootLayout = findViewById(R.id.root_layout);
        spinnerDoctors = findViewById(R.id.spinner_doctors);
        spinnerAssistants = findViewById(R.id.spinner_assistants);
        spinnerReceptionists = findViewById(R.id.spinner_receptionists);
        buttonAdd = findViewById(R.id.button_add);
        buttonDelete = findViewById(R.id.button_delete);

        db = FirebaseFirestore.getInstance();

        loadEmployees("doctors", spinnerDoctors, doctorIds);
        loadEmployees("assistants", spinnerAssistants, assistantIds);
        loadEmployees("receptionists", spinnerReceptionists, receptionistIds);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example of adding a new doctor
                Doctor newDoctor = new Doctor("John Doe", "123456789", "johndoe@example.com", "001");
                db.collection("doctors").add(newDoctor).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RoleCenterDashboard.this, "Doctor added", Toast.LENGTH_SHORT).show();
                            recreate(); // Refresh the activity to reload employees
                        } else {
                            Toast.makeText(RoleCenterDashboard.this, "Error adding doctor: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedEmployee();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
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

    private void loadEmployees(String collection, Spinner spinner, List<String> ids) {
        CollectionReference employeesRef = db.collection(collection);
        employeesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> employeeNames = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        employeeNames.add(name);
                        ids.add(document.getId());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RoleCenterDashboard.this, android.R.layout.simple_spinner_item, employeeNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(RoleCenterDashboard.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteSelectedEmployee() {
        int doctorPosition = spinnerDoctors.getSelectedItemPosition();
        int assistantPosition = spinnerAssistants.getSelectedItemPosition();
        int receptionistPosition = spinnerReceptionists.getSelectedItemPosition();

        if (doctorPosition != -1) {
            deleteEmployee("doctors", doctorIds.get(doctorPosition));
        } else if (assistantPosition != -1) {
            deleteEmployee("assistants", assistantIds.get(assistantPosition));
        } else if (receptionistPosition != -1) {
            deleteEmployee("receptionists", receptionistIds.get(receptionistPosition));
        } else {
            Toast.makeText(this, "No employee selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEmployee(String collection, String id) {
        DocumentReference docRef = db.collection(collection).document(id);
        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RoleCenterDashboard.this, "Employee deleted", Toast.LENGTH_SHORT).show();
                    recreate(); // Refresh the activity to reload employees
                } else {
                    Toast.makeText(RoleCenterDashboard.this, "Error deleting document: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
