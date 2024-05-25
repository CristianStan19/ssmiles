package com.example.dcm_stellarsmiles.Auth;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    TextInputEditText etEmail, etPw, etFullName, etPhoneNumber, etBirthDate, etCNP;
    RadioGroup smokeRadioGroup, drinkRadioGroup;
    RadioButton smokeYes, smokeNo, drinkYes, drinkNo;
    TextInputLayout tlPw;
    Button btnRegister;
    FirebaseAuth mAuth;
    TextView loginNow;
    FirebaseFirestore db;
    DatePickerDialog datePickerDialog;

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
        smokeRadioGroup = findViewById(R.id.smokerRG);

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

        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
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
                RadioButton selectedSmokeButton = findViewById(smokeRadioGroup.getCheckedRadioButtonId());
                isSmoker = selectedSmokeButton.isChecked();
                RadioButton selectedDrinkButton = findViewById(drinkRadioGroup.getCheckedRadioButtonId());
                isDrinker = selectedDrinkButton.isChecked();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
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
                                                    // Handle success
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle failure
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

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                Register.this,
                (view, year1, month1, dayOfMonth) -> {
                    month1 += 1;
                    String selectedDate = makeDateString(dayOfMonth, month1, year1);
                    etBirthDate.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
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
}
