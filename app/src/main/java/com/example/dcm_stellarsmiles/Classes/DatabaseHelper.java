package com.example.dcm_stellarsmiles.Classes;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseHelper {
    private FirebaseFirestore db;

    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void getDoctorSpecialization(String name, final Callback<String> callback) {
        db.collection("doctors")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String specialization = task.getResult().getDocuments().get(0).getString("specialization");
                        callback.onSuccess(specialization);
                    } else {
                        callback.onFailure(new Exception("Specialization not found"));
                    }
                });
    }

    public void getAssistantDepartment(String name, final Callback<String> callback) {
        db.collection("assistants")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String department = task.getResult().getDocuments().get(0).getString("department");
                        callback.onSuccess(department);
                    } else {
                        callback.onFailure(new Exception("Department not found"));
                    }
                });
    }

    public void isReceptionist(String name, final Callback<Boolean> callback) {
        db.collection("receptionists")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        callback.onSuccess(true);
                    } else {
                        callback.onSuccess(false);
                    }
                });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}
