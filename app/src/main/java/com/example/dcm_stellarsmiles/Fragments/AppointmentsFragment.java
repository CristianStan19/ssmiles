package com.example.dcm_stellarsmiles.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dcm_stellarsmiles.Adapter.AppointmentsAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Intefaces.OnCancelAppointmentClickListener;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AppointmentsFragment extends Fragment implements OnCancelAppointmentClickListener {
    private FirebaseFirestore db;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentsAdapter appointmentsAdapter;
    private List<Appointment> appointmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and List
        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        appointmentsRecyclerView.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        appointmentList = new ArrayList<>();
        appointmentsAdapter = new AppointmentsAdapter(appointmentList, this);
        appointmentsRecyclerView.setAdapter(appointmentsAdapter);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchAppointments(userUid);

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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointmentList.add(appointment);
                            }
                            appointmentsAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("AppointmentsFragment", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onCancelAppointment(Appointment appointment) {
        appointment.setAppointmentStatus("canceled");
        updateAppointmentInFirestore(appointment); // Call a new method to update Firestore
    }

    private void updateAppointmentInFirestore(Appointment appointment) {
        db.collection("appointments")
                .document(appointment.getAppointmentId()) // Assuming Appointment has an id field
                .update("appointmentStatus", "canceled")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("AppointmentsFragment", "Appointment status updated in Firestore.");
                        } else {
                            Log.w("AppointmentsFragment", "Error updating appointment in Firestore.", task.getException());
                        }
                    }
                });
    }
}