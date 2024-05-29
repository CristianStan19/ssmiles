package com.example.dcm_stellarsmiles.Fragments;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment {

    private TextView tvWelcomeUser, tvAppointmentUser, tvNotification;
    private RelativeLayout rlNotification;
    private Button btnCloseNotification;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvWelcomeUser = view.findViewById(R.id.tvWelcomeUser);
        tvAppointmentUser = view.findViewById(R.id.tvAppointmentStatusUser);
        tvNotification = view.findViewById(R.id.tvNotification);
        rlNotification = view.findViewById(R.id.rlNotification);
        btnCloseNotification = view.findViewById(R.id.btnCloseNotification);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("dismissed_notifications", Context.MODE_PRIVATE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String customerID = user != null ? user.getUid() : null;
        fetchUserDetails(customerID);
        if (customerID != null) {
            setupAppointmentListener(customerID);
        } else {
            Log.d("HomeFragment", "User is not logged in");
        }

        btnCloseNotification.setOnClickListener(v -> {
            rlNotification.setVisibility(View.GONE);
            saveDismissedNotification();
        });
        return view;
    }

    private void fetchUserDetails(String customerID) {
        db.collection("customers").document(customerID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        String fullNameString = documentSnapshot.getString("fullName");
                        String firstName = fullNameString.split(" ")[0];
                        tvWelcomeUser.setText(tvWelcomeUser.getText().toString() + " " + firstName + "!");
                    } else {
                        Log.d("Firestore", "No document found for customer ID: " + customerID);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting documents: ", e));
    }

    private void setupAppointmentListener(String userUid) {
        db.collection("appointments")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), userUid + "0")
                .whereLessThanOrEqualTo(FieldPath.documentId(), userUid + "999")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("AppointmentsFragment", "listen:error", e);
                        return;
                    }
                    boolean hasOngoingAppointment = false;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Appointment appointment = dc.getDocument().toObject(Appointment.class);
                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                if (!isNotificationDismissed(appointment.getAppointmentId())) {
                                    showNotification(appointment);
                                }
                                if (appointment.getAppointmentStatus().equals(Constants.APP_ON_GOING)) {
                                    hasOngoingAppointment = true;
                                }
                                break;
                            case REMOVED:
                                // Handle removed appointment if needed
                                break;
                        }
                    }
                    updateAppointmentStatusText(hasOngoingAppointment);
                });
    }

    private void showNotification(Appointment appointment) {
        String message;
        if (appointment.getAppointmentStatus().equals(Constants.APP_CANCELED)) {
            message = "Your appointment on " + appointment.getAppointmentDate() + " for " + appointment.getType() + " has been canceled.";
        } else if (appointment.getAppointmentStatus().equals("rescheduled")) {
            message = "Your appointment on " + appointment.getAppointmentDate() + " for " + appointment.getType() + " has been rescheduled.";
        } else {
            return; // No need to show notification for other statuses
        }

        tvNotification.setText(message);
        rlNotification.setTag(appointment.getAppointmentId());
        rlNotification.setVisibility(View.VISIBLE);
    }

    private void updateAppointmentStatusText(boolean hasOngoingAppointment) {
        if (hasOngoingAppointment) {
            tvAppointmentUser.setText("You currently have an ongoing appointment.");
        } else {
            tvAppointmentUser.setText("You don't have an ongoing appointment!");
        }
    }

    private boolean isNotificationDismissed(String appointmentId) {
        return sharedPreferences.getBoolean(appointmentId, false);
    }

    private void saveDismissedNotification() {
        String appointmentId = (String) rlNotification.getTag();
        if (appointmentId != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(appointmentId, true);
            editor.apply();
        }
    }
}