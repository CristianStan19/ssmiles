package com.example.dcm_stellarsmiles.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dcm_stellarsmiles.R;

import java.util.Random;

public class AboutUsFragment extends Fragment {

    private static final double LAT = 44.480982;
    private static final double LON = 26.096415;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        Button btnOpenMap = view.findViewById(R.id.btn_open_map);
        btnOpenMap.setOnClickListener(v -> {
            // Generate random coordinates within the specified range

            // Define the geo URI
            String geoUri = String.format("geo:%f,%f?q=%f,%f(StellarSmiles)", LAT, LON, LAT, LON);
            Uri gmmIntentUri = Uri.parse(geoUri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Handle the case where Google Maps is not installed
                String location = String.format("%f,%f", LAT, LON);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + location));
                startActivity(webIntent);
            }
        });

        return view;
    }
}
