package com.example.dcm_stellarsmiles.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Adapter.CustomSpinnerAdapter;
import com.example.dcm_stellarsmiles.Adapter.CustomerAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomerFragment extends Fragment {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<DocumentSnapshot> customerList;
    private List<DocumentSnapshot> filteredList;

    private FirebaseFirestore db;
    private Spinner spinnerSort;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add space between items
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spaceHeight));

        spinnerSort = view.findViewById(R.id.spinnerSort);

        customerList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new CustomerAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchCustomerData();

        setupSpinner();

        return view;
    }

    private void fetchCustomerData() {
        db.collection("customers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        customerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            customerList.add(document);
                        }
                        updateSpinner();
                        filterCustomers(null); // Show all customers initially
                    }
                });
    }

    private void setupSpinner() {
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) spinnerSort.getSelectedItem();
                if (selectedName.equals("Select a patient")) {
                    filterCustomers(null); // Show all customers
                } else {
                    filterCustomers(selectedName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateSpinner() {
        List<String> names = new ArrayList<>();
        names.add("Select a patient");
        List<String> customerNames = new ArrayList<>();
        for (DocumentSnapshot document : customerList) {
            customerNames.add(document.getString("fullName"));
        }
        Collections.sort(customerNames);
        names.addAll(customerNames); // Add sorted customer names after "Select a patient"
        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, names);
        spinnerSort.setAdapter(spinnerAdapter);
        spinnerSort.setSelection(0); // Set "Select a patient" as the default selection
    }

    private void filterCustomers(String name) {
        filteredList.clear();
        if (name == null) {
            filteredList.addAll(customerList);
        } else {
            for (DocumentSnapshot document : customerList) {
                if (document.getString("fullName").equals(name)) {
                    filteredList.add(document);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}