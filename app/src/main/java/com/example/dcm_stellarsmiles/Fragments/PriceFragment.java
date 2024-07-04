package com.example.dcm_stellarsmiles.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dcm_stellarsmiles.Adapter.PriceAdapter;
import com.example.dcm_stellarsmiles.Adapter.SpaceItemDecoration;
import com.example.dcm_stellarsmiles.Classes.Price.PriceItem;
import com.example.dcm_stellarsmiles.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.dcm_stellarsmiles.Constants.Constants.APPOINTMENT_COSTS;
import static com.example.dcm_stellarsmiles.Constants.Constants.APPOINTMENT_SPECIALIZATIONS;

public class PriceFragment extends Fragment {

    private RecyclerView priceRecyclerView;
    private PriceAdapter priceAdapter;
    private Map<String, List<PriceItem>> priceItemMap;
    private List<String> specializationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        // Initialize RecyclerView and List
        priceRecyclerView = view.findViewById(R.id.priceRecyclerView);
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        priceRecyclerView.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        priceItemMap = new HashMap<>();
        specializationList = new ArrayList<>();
        priceAdapter = new PriceAdapter(priceItemMap, specializationList);
        priceRecyclerView.setAdapter(priceAdapter);
        priceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data from the APPOINTMENT_COSTS hashmap
        loadPriceData();

        return view;
    }

    private void loadPriceData() {
        for (Map.Entry<String, Integer> entry : APPOINTMENT_COSTS.entrySet()) {
            String appointmentType = entry.getKey();
            int cost = entry.getValue();
            String specialization = APPOINTMENT_SPECIALIZATIONS.get(appointmentType);

            if (!priceItemMap.containsKey(specialization)) {
                priceItemMap.put(specialization, new ArrayList<>());
                specializationList.add(specialization);
            }
            priceItemMap.get(specialization).add(new PriceItem(appointmentType, cost));
        }
        priceAdapter.notifyDataSetChanged();
    }
}
