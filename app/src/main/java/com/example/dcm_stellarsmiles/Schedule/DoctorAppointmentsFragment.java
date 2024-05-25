package com.example.dcm_stellarsmiles.Schedule;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Adapter.DoctorAppointmentsAdapter;
import com.example.dcm_stellarsmiles.Intefaces.OnCancelAppointmentClickListener;
import com.example.dcm_stellarsmiles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorAppointmentsFragment extends Fragment implements OnCancelAppointmentClickListener {

    private RecyclerView recyclerView;
    private DoctorAppointmentsAdapter adapter;
    private List<Appointment> appointmentList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DatePickerDialog datePickerDialog;

    public DoctorAppointmentsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        appointmentList = new ArrayList<>();
        adapter = new DoctorAppointmentsAdapter(appointmentList, this, this);
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fetchDoctorAppointments();
    }

    private void fetchDoctorAppointments() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUID = user.getUid();
        DocumentReference doctorRef = db.collection("doctors").document(userUID);

        doctorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String doctorFullName = document.getString("name");
                        if (doctorFullName != null) {
                            fetchAppointmentsForDoctor(doctorFullName);
                        } else {
                            Toast.makeText(getContext(), "Doctor's name not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Doctor document does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch doctor details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAppointmentsForDoctor(String doctorFullName) {
        CollectionReference appointmentsRef = db.collection("appointments");
        appointmentsRef.whereEqualTo("doctor", doctorFullName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointmentList.add(appointment);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch appointments", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onCancelAppointment(Appointment appointment) {
        appointment.setAppointmentStatus("canceled");
        updateAppointmentInFirestore(appointment); // Call a new method to update Firestore
    }

    @Override
    public void onRescheduleAppointment(Appointment appointment) {
        showDatePickerDialog(appointment); // Show date picker dialog to select new date
    }

    private void showDatePickerDialog(Appointment appointment) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    month1 += 1;
                    String selectedDate = makeDateString(dayOfMonth, month1, year1);
                    appointment.setAppointmentDate(selectedDate);
                    updateAppointmentInFirestore(appointment);
                },
                year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateAppointmentInFirestore(Appointment appointment) {
        db.collection("appointments")
                .document(appointment.getAppointmentId())
                .update("appointmentStatus", appointment.getAppointmentStatus(), "appointmentDate", appointment.getAppointmentDate())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DoctorAppointmentsFragment", "Appointment updated in Firestore.");
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("DoctorAppointmentsFragment", "Error updating appointment in Firestore.", task.getException());
                        }
                    }
                });
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
