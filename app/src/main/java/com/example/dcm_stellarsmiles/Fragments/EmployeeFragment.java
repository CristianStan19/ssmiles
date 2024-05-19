package com.example.dcm_stellarsmiles.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Adapter.EmployeeAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Employees.Assistant;
import com.example.dcm_stellarsmiles.Classes.Employees.Doctor;
import com.example.dcm_stellarsmiles.Classes.Employees.Employee;
import com.example.dcm_stellarsmiles.Classes.Employees.Receptionist;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployeeFragment extends Fragment {

    private RecyclerView rvDoctors, rvAssistants, rvReceptionists;
    private List<Employee> doctors, assistants, receptionists;
    private EmployeeAdapter doctorsAdapter, assistantsAdapter, receptionistsAdapter;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);

        rvDoctors = view.findViewById(R.id.rvDoctors);
        rvAssistants = view.findViewById(R.id.rvAssistants);
        rvReceptionists = view.findViewById(R.id.rvReceptionists);

        doctors = new ArrayList<>();
        assistants = new ArrayList<>();
        receptionists = new ArrayList<>();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerViews
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        rvDoctors.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        rvAssistants.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        rvReceptionists.addItemDecoration(new SpaceItemDecoration(spaceHeight));

        doctorsAdapter = new EmployeeAdapter(doctors, getContext());
        rvDoctors.setAdapter(doctorsAdapter);
        rvDoctors.setLayoutManager(new LinearLayoutManager(getContext()));

        assistantsAdapter = new EmployeeAdapter(assistants, getContext());
        rvAssistants.setAdapter(assistantsAdapter);
        rvAssistants.setLayoutManager(new LinearLayoutManager(getContext()));

        receptionistsAdapter = new EmployeeAdapter(receptionists, getContext());
        rvReceptionists.setAdapter(receptionistsAdapter);
        rvReceptionists.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch data for doctors, assistants, and receptionists
        fetchDoctors("doctors", doctors, doctorsAdapter);
        fetchAssistants("assistants", assistants, assistantsAdapter);
        fetchReceptionists("receptionists", receptionists, receptionistsAdapter);

        return view;
    }

    private void fetchDoctors(String collectionName, final List<Employee> employeeList, final EmployeeAdapter adapter) {
        CollectionReference collectionRef = db.collection(collectionName);
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert each document to an Employee object and add it to the list
                        Doctor employee = document.toObject(Doctor.class);
                        employeeList.add(employee);
                    }
                    // Notify the adapter of changes in the data
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void fetchAssistants(String collectionName, final List<Employee> employeeList, final EmployeeAdapter adapter) {
        CollectionReference collectionRef = db.collection(collectionName);
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert each document to an Employee object and add it to the list
                        Assistant employee = document.toObject(Assistant.class);
                        employeeList.add(employee);
                    }
                    // Notify the adapter of changes in the data
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void fetchReceptionists(String collectionName, final List<Employee> employeeList, final EmployeeAdapter adapter) {
        CollectionReference collectionRef = db.collection(collectionName);
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert each document to an Employee object and add it to the list
                        Receptionist employee = document.toObject(Receptionist.class);
                        employeeList.add(employee);
                    }
                    // Notify the adapter of changes in the data
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
