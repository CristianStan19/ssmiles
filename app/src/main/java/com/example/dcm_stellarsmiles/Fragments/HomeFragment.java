package com.example.dcm_stellarsmiles.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {

    private TextView tvWelcomeUser, tvAppointmentUser;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvWelcomeUser = view.findViewById(R.id.tvWelcomeUser);
        tvAppointmentUser = view.findViewById(R.id.tvAppointmentStatusUser);
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String customerID = user != null ? user.getUid() : null;
        fetchAppointments(customerID);
        if (customerID != null) {
            DocumentReference docRef = db.collection("customers").document(customerID);
            docRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (isAdded() && documentSnapshot.exists()) {
                                String fullNameString = documentSnapshot.getString("fullName");
                                String firstName = fullNameString.split(" ")[0];
                                tvWelcomeUser.setText(tvWelcomeUser.getText().toString() + " " + firstName+"!");
                            } else {
                                Log.d("Firestore", "No document found for customer ID: " + customerID);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firestore", "Error getting documents: ", e);
                        }
                    });
        } else {
            Log.d("HomeFragment", "User is not logged in");
        }

        return view;
    }

    private void fetchAppointments(String userUid) {
        db.collection("appointments")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), userUid + "0")
                .whereLessThanOrEqualTo(FieldPath.documentId(), userUid + "999")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean hasOngoingAppointment = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                if (appointment.getAppointmentStatus().equals(Constants.APP_ON_GOING)) {
                                    hasOngoingAppointment = true;
                                    break; // Exit loop after finding an ongoing appointment
                                }
                            }

                            if (hasOngoingAppointment) {
                                tvAppointmentUser.setText("You currently have an appointment ongoing!");
                            } else {
                                tvAppointmentUser.setText("You currently don't have any appointments!");
                            }
                        } else {
                            Log.w("AppointmentsFragment", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}