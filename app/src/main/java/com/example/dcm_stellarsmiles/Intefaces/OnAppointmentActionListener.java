package com.example.dcm_stellarsmiles.Intefaces;

import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;

public interface OnAppointmentActionListener {
    void onCancelAppointment(Appointment appointment);
    void onRescheduleAppointment(Appointment appointment);
    void onCompleteAppointment(Appointment appointment);
}
