package com.example.dcm_stellarsmiles.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.R;

import java.util.HashMap;
import java.util.Map;

public class ConsultationFragment extends Fragment {

    private RadioGroup rgPainLevel, rgBleeding, rgSwelling;
    private RadioButton rbPainLevel, rbBleeding, rbSwelling;
    private Button btnSubmit;
    private TextView tvResult;

    public ConsultationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation, container, false);

        rgPainLevel = view.findViewById(R.id.rgPainLevel);
        rgBleeding = view.findViewById(R.id.rgBleeding);
        rgSwelling = view.findViewById(R.id.rgSwelling);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvResult = view.findViewById(R.id.tvResult);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPainLevelId = rgPainLevel.getCheckedRadioButtonId();
                int selectedBleedingId = rgBleeding.getCheckedRadioButtonId();
                int selectedSwellingId = rgSwelling.getCheckedRadioButtonId();

                rbPainLevel = view.findViewById(selectedPainLevelId);
                rbBleeding = view.findViewById(selectedBleedingId);
                rbSwelling = view.findViewById(selectedSwellingId);

                String painLevel = rbPainLevel.getText().toString();
                String bleeding = rbBleeding.getText().toString();
                String swelling = rbSwelling.getText().toString();

                String appointmentType = suggestAppointment(painLevel, bleeding, swelling);
                tvResult.setText("Suggested Appointment: " + appointmentType);
            }
        });

        return view;
    }

    private String suggestAppointment(String painLevel, String bleeding, String swelling) {
        Map<String, String> suggestions = new HashMap<>();
        suggestions.put("High, Yes, Yes", Constants.TOOTH_EXTRACTION);
        suggestions.put("High, Yes, No", Constants.ROOT_CANAL_TREATMENT);
        suggestions.put("High, No, Yes", Constants.GUM_TREATMENT);
        suggestions.put("Medium, Yes, Yes", Constants.DENTAL_FILLING);
        suggestions.put("Medium, Yes, No", Constants.BRACES_ADJUSTMENT);
        suggestions.put("Medium, No, Yes", Constants.SCALING_AND_ROOT_PLANING);
        suggestions.put("Low, Yes, Yes", Constants.TEETH_CLEANING);
        suggestions.put("Low, Yes, No", Constants.RETAINER_ADJUSTMENT);
        suggestions.put("Low, No, Yes", Constants.X_RAY);
        suggestions.put("Low, No, No", Constants.ROUTINE_CHECKUP);

        String key = painLevel + ", " + bleeding + ", " + swelling;
        return suggestions.getOrDefault(key, Constants.ROUTINE_CHECKUP);
    }
}
