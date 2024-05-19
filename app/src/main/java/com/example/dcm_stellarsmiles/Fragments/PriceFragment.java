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

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.dcm_stellarsmiles.Constants.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriceFragment extends Fragment {

    private RecyclerView priceRecyclerView;
    private PriceAdapter priceAdapter;
    private List<PriceItem> priceItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        // Initialize RecyclerView and List
        priceRecyclerView = view.findViewById(R.id.priceRecyclerView);
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.dp_12);
        priceRecyclerView.addItemDecoration(new SpaceItemDecoration(spaceHeight));
        priceItemList = new ArrayList<>();
        priceAdapter = new PriceAdapter(priceItemList);
        priceRecyclerView.setAdapter(priceAdapter);
        priceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data from the APPOINTMENT_COSTS hashmap
        loadPriceData();

        return view;
    }

    private void loadPriceData() {
        for (Map.Entry<String, Integer> entry : Constants.APPOINTMENT_COSTS.entrySet()) {
            priceItemList.add(new PriceItem(entry.getKey(), entry.getValue()));
        }
        priceAdapter.notifyDataSetChanged();
    }
}
