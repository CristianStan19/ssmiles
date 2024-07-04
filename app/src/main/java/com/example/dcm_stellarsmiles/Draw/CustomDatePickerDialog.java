package com.example.dcm_stellarsmiles.Draw;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import java.util.List;

public class CustomDatePickerDialog extends DatePickerDialog {

    private List<String> availableDates;

    public CustomDatePickerDialog(Context context, OnDateSetListener listener, int year, int month, int dayOfMonth, List<String> availableDates) {
        super(context, listener, year, month, dayOfMonth);
        this.availableDates = availableDates;
        getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
        if (!availableDates.contains(selectedDate)) {
            view.updateDate(year, month, day + 1); // Move to the next available date if current is not available
        }
    }
}
