package com.example.dcm_stellarsmiles.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView tvFN, tvEA, tvBD, tvPN, tvSK, tvDK, tvAC;
    // ... (add other UI elements for displaying user data)

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvFN = view.findViewById(R.id.tvFullName);
        tvEA = view.findViewById(R.id.tvEmailAddress);
        tvBD = view.findViewById(R.id.tvBirthDate);
        tvPN = view.findViewById(R.id.tvPhoneNumber);
        tvSK = view.findViewById(R.id.tvSmoker);
        tvDK = view.findViewById(R.id.tvDrinker);
        tvAC = view.findViewById(R.id.tvAppointmentsCompleted);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String customerID = user != null ? user.getUid() : null;

        if (customerID != null) {
            DocumentReference docRef = db.collection("customers").document(customerID);
            docRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String fullNameString = getResources().getString(R.string.full_name);
                                String emailAddressString = getResources().getString(R.string.email_address);
                                String birthDateString = getResources().getString(R.string.birth_date);
                                String phoneNumberString = getResources().getString(R.string.phone_number);
                                String drinkerString = getResources().getString(R.string.drinker);
                                String smokerString = getResources().getString(R.string.smoker);
                                String appointmentsCompletedString = getResources().getString(R.string.appointments_completed);

                                // Extract data from the document
                                String name = documentSnapshot.getString("fullName");
                                String email = documentSnapshot.getString("email");
                                String birthdate = documentSnapshot.getString("birthDate");
                                String phoneNumber = documentSnapshot.getString("phoneNumber");
                                String drinker = documentSnapshot.getBoolean("drinker") ? "Yes" : "No";
                                String smoker = documentSnapshot.getBoolean("smoker") ? "Yes" : "No";
                                long visits = documentSnapshot.getLong("visits");
                                // Update UI elements
                                tvFN.setText(fullNameString + ": " + name);
                                tvEA.setText(emailAddressString + ": " + email);
                                tvBD.setText(birthDateString + ": " + birthdate);
                                tvPN.setText(phoneNumberString + ": " + phoneNumber);
                                tvDK.setText(drinkerString + ": " + drinker);
                                tvSK.setText(smokerString + ": " + smoker);
                                tvAC.setText(appointmentsCompletedString + ": " + visits);
                                // ... (update other UI elements)
                            } else {
                                Log.d("Firestore", "No document found for customer ID: " + customerID);
                                // Handle the case where no document is found (optional)
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firestore", "Error getting documents: ", e);
                            // Handle errors
                        }
                    });
        } else {
            // Handle the case where user is not logged in (optional)
            Log.d("ProfileFragment", "User is not logged in");
        }

        return view;
    }
}
