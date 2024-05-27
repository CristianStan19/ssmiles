package com.example.dcm_stellarsmiles.Auth;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dcm_stellarsmiles.Dashboard.Dashboard;
import com.example.dcm_stellarsmiles.Dashboard.EmployeeDashboard;
import com.example.dcm_stellarsmiles.Dashboard.ReceptionistDashboard;
import com.example.dcm_stellarsmiles.Dashboard.RoleCenterDashboard;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {

    TextView registerNow;
    TextInputEditText etEmail, etPw;
    TextInputLayout tlPw;
    Button btnLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRole(currentUser);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        registerNow = findViewById(R.id.tvRegisterNow);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        etEmail = findViewById(R.id.etEmailLogin);
        etPw = findViewById(R.id.etPwLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tlPw = findViewById(R.id.tlPwLogin);

        Drawable endIconDrawable = tlPw.getEndIconDrawable();
        if (endIconDrawable != null) {
            endIconDrawable.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
        }

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = etEmail.getText().toString();
                password = etPw.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LogIn.this, "Authentication successful.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        checkUserRole(currentUser);
                                    }
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LogIn.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkUserRole(FirebaseUser currentUser) {
        String email = currentUser.getEmail();
        if (email != null && email.equals("rolecenter@stellarsmiles.ro")) {
            Intent intent = new Intent(getApplicationContext(), RoleCenterDashboard.class);
            startActivity(intent);
            finish();
        } else if (email != null && email.contains("@stellarsmiles.ro")) {
            db.collection("doctors").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Intent intent = new Intent(getApplicationContext(), EmployeeDashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        db.collection("receptionists").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    Intent intent = new Intent(getApplicationContext(), ReceptionistDashboard.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    db.collection("assistants").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful() && task.getResult().exists()) {
                                                // If needed, you can navigate to a specific activity for assistants here
                                                Toast.makeText(LogIn.this, "Logged in as Assistant", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LogIn.this, "Role not found", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }
}
