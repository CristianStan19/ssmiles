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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dcm_stellarsmiles.Classes.Customer.Customer;
import com.example.dcm_stellarsmiles.Dashboard.Dashboard;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    TextInputEditText etEmail, etPw, etFullName, etPhoneNumber, etBirthDate, etCNP;
    RadioGroup smokeRadioGroup, drinkRadioGroup;
    RadioButton smokeYes, smokeNo, drinkYes, drinkNo;
    TextInputLayout tlPw;
    Button btnRegister;
    FirebaseAuth mAuth;
    TextView loginNow;
    FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmailLogin);
        etPw = findViewById(R.id.etPwLogin);
        loginNow = findViewById(R.id.loginNow);
        btnRegister = findViewById(R.id.btnRegister);

        etBirthDate = findViewById(R.id.etBirthDate);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etCNP = findViewById(R.id.etCNP);

        drinkRadioGroup = findViewById(R.id.drinkerRG);
        smokeRadioGroup = findViewById(R.id.drinkerRG);


        tlPw = findViewById(R.id.tlPwLogin);
        Drawable endIconDrawable = tlPw.getEndIconDrawable();
        if (endIconDrawable != null) {
            endIconDrawable.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
        }

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, phoneNumber, birthDate, CNP, fullName;
                boolean isSmoker, isDrinker;
                email = etEmail.getText().toString();
                password = etPw.getText().toString();
                phoneNumber = etPhoneNumber.getText().toString();
                birthDate = etBirthDate.getText().toString();
                CNP = etCNP.getText().toString();
                fullName = etFullName.getText().toString();
                RadioButton selectedSmokeButton = (RadioButton) findViewById(smokeRadioGroup.getCheckedRadioButtonId());
                isSmoker = selectedSmokeButton.isChecked();
                RadioButton selectedDrinkButton = (RadioButton) findViewById(drinkRadioGroup.getCheckedRadioButtonId());
                isDrinker = selectedDrinkButton.isChecked();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account created successfully.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = task.getResult().getUser();
                                    String userID = user.getUid();
                                    db = FirebaseFirestore.getInstance();
                                    Customer customer = new Customer(fullName, email, phoneNumber, birthDate, CNP, isSmoker, isDrinker);
                                    customer.setCustomerID(userID);
                                    Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                    startActivity(intent);
                                    finish();

                                    db.collection("customers").document(userID).set(customer)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Register.this, "Account creation failure.",
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
}