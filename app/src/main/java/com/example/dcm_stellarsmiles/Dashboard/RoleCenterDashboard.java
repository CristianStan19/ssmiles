package com.example.dcm_stellarsmiles.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dcm_stellarsmiles.Auth.LogIn;
import com.example.dcm_stellarsmiles.Classes.Employees.Assistant;
import com.example.dcm_stellarsmiles.Classes.Employees.Doctor;
import com.example.dcm_stellarsmiles.Classes.Employees.Receptionist;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RoleCenterDashboard extends AppCompatActivity {

    private Spinner spinnerDoctors, spinnerAssistants, spinnerReceptionists, spinnerEmployeeType, spinnerSpecialization, spinnerDepartment;
    private EditText editTextName, editTextPhone, editTextEmail;
    private Button buttonAdd, buttonDelete;
    private FirebaseFirestore db;
    private List<String> doctorIds = new ArrayList<>();
    private List<String> assistantIds = new ArrayList<>();
    private List<String> receptionistIds = new ArrayList<>();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role_center_dashboard);
        LinearLayout rootLayout = findViewById(R.id.root_layout);
        spinnerDoctors = findViewById(R.id.spinner_doctors);
        spinnerAssistants = findViewById(R.id.spinner_assistants);
        spinnerReceptionists = findViewById(R.id.spinner_receptionists);
        spinnerEmployeeType = findViewById(R.id.spinner_employee_type);
        spinnerSpecialization = findViewById(R.id.spinner_specialization);
        spinnerDepartment = findViewById(R.id.spinner_department);
        editTextName = findViewById(R.id.edit_text_name);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextEmail = findViewById(R.id.edit_text_email);
        buttonAdd = findViewById(R.id.button_add);
        buttonDelete = findViewById(R.id.button_delete);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadEmployees("doctors", spinnerDoctors, doctorIds, "Select a Doctor");
        loadEmployees("assistants", spinnerAssistants, assistantIds, "Select an Assistant");
        loadEmployees("receptionists", spinnerReceptionists, receptionistIds, "Select a Receptionist");

        ArrayAdapter<CharSequence> doctorSpecializationAdapter = ArrayAdapter.createFromResource(this, R.array.doctor_specializations, android.R.layout.simple_spinner_item);
        doctorSpecializationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> assistantDepartmentAdapter = ArrayAdapter.createFromResource(this, R.array.assistant_departments, android.R.layout.simple_spinner_item);
        assistantDepartmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEmployeeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEmployeeType = parent.getItemAtPosition(position).toString();
                if (selectedEmployeeType.equals("Doctor")) {
                    spinnerSpecialization.setAdapter(doctorSpecializationAdapter);
                    spinnerSpecialization.setVisibility(View.VISIBLE);
                    spinnerDepartment.setVisibility(View.GONE);
                } else if (selectedEmployeeType.equals("Assistant")) {
                    spinnerDepartment.setAdapter(assistantDepartmentAdapter);
                    spinnerDepartment.setVisibility(View.VISIBLE);
                    spinnerSpecialization.setVisibility(View.GONE);
                } else {
                    spinnerSpecialization.setVisibility(View.GONE);
                    spinnerDepartment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerSpecialization.setVisibility(View.GONE);
                spinnerDepartment.setVisibility(View.GONE);
            }
        });

        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String specialization = spinnerSpecialization.getSelectedItem() != null ? spinnerSpecialization.getSelectedItem().toString() : "";
            String department = spinnerDepartment.getSelectedItem() != null ? spinnerDepartment.getSelectedItem().toString() : "";
            String password = "111111";

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(RoleCenterDashboard.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            createEmployeeAccount(email, password, (task) -> {
                if (task.isSuccessful()) {
                    FirebaseUser newEmployee = task.getResult().getUser();
                    if (newEmployee != null) {
                        String uid = newEmployee.getUid();
                        String selectedEmployeeType = spinnerEmployeeType.getSelectedItem().toString();

                        switch (selectedEmployeeType) {
                            case "Doctor":
                                Doctor newDoctor = new Doctor(name, phone, email, uid);
                                newDoctor.setSpecialization(specialization);
                                db.collection("doctors").document(uid).set(newDoctor)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(RoleCenterDashboard.this, "Doctor added", Toast.LENGTH_SHORT).show();
                                                recreate();
                                            } else {
                                                Toast.makeText(RoleCenterDashboard.this, "Error adding doctor: " + task1.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;
                            case "Assistant":
                                Assistant newAssistant = new Assistant(name, phone, email, uid);
                                newAssistant.setDepartment(department);
                                db.collection("assistants").document(uid).set(newAssistant)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(RoleCenterDashboard.this, "Assistant added", Toast.LENGTH_SHORT).show();
                                                recreate();
                                            } else {
                                                Toast.makeText(RoleCenterDashboard.this, "Error adding assistant: " + task1.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;
                            case "Receptionist":
                                Receptionist newReceptionist = new Receptionist(name, phone, email, uid);
                                db.collection("receptionists").document(uid).set(newReceptionist)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(RoleCenterDashboard.this, "Receptionist added", Toast.LENGTH_SHORT).show();
                                                recreate();
                                            } else {
                                                Toast.makeText(RoleCenterDashboard.this, "Error adding receptionist: " + task1.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;
                            default:
                                Toast.makeText(RoleCenterDashboard.this, "Invalid employee type selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        Toast.makeText(RoleCenterDashboard.this, "Weak password", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(RoleCenterDashboard.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RoleCenterDashboard.this, "User already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RoleCenterDashboard.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        buttonDelete.setOnClickListener(v -> deleteSelectedEmployee());

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createEmployeeAccount(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, listener);
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

    private void loadEmployees(String collection, Spinner spinner, List<String> ids, String placeholder) {
        CollectionReference employeesRef = db.collection(collection);
        employeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> employeeNames = new ArrayList<>();
                employeeNames.add(placeholder);
                ids.add("");  // Add a placeholder ID for no selection
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    employeeNames.add(name);
                    ids.add(document.getId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RoleCenterDashboard.this, android.R.layout.simple_spinner_item, employeeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(0);  // Set the selection to the placeholder
            } else {
                Toast.makeText(RoleCenterDashboard.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSelectedEmployee() {
        int doctorPosition = spinnerDoctors.getSelectedItemPosition();
        int assistantPosition = spinnerAssistants.getSelectedItemPosition();
        int receptionistPosition = spinnerReceptionists.getSelectedItemPosition();

        if (doctorPosition > 0) {
            deleteEmployee("doctors", doctorIds.get(doctorPosition));
        } else if (assistantPosition > 0) {
            deleteEmployee("assistants", assistantIds.get(assistantPosition));
        } else if (receptionistPosition > 0) {
            deleteEmployee("receptionists", receptionistIds.get(receptionistPosition));
        } else {
            Toast.makeText(this, "No employee selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEmployee(String collection, String id) {
        DocumentReference docRef = db.collection(collection).document(id);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String email = task.getResult().getString("email");
                docRef.delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (email != null) {
                            deleteUserAuth(email);
                        }
                        Toast.makeText(RoleCenterDashboard.this, "Employee deleted", Toast.LENGTH_SHORT).show();
                        recreate();
                    } else {
                        Toast.makeText(RoleCenterDashboard.this, "Error deleting document: " + task1.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(RoleCenterDashboard.this, "Error retrieving document: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserAuth(String email) {
        auth.signInWithEmailAndPassword(email, "111111").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                if (user != null) {
                    user.delete().addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Toast.makeText(RoleCenterDashboard.this, "Error deleting user auth: " + task1.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(RoleCenterDashboard.this, "Error signing in user for deletion: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
