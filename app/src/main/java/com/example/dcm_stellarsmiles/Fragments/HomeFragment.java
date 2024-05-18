package com.example.dcm_stellarsmiles.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
}