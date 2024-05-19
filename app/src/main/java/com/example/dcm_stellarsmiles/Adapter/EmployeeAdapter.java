package com.example.dcm_stellarsmiles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Classes.Employees.Employee;
import com.example.dcm_stellarsmiles.R;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employees;
    private Context context;

    public EmployeeAdapter(List<Employee> employees, Context context) {
        this.employees = employees;
        this.context = context;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_list_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.tvEmployeeName.setText(employee.getName());
        holder.tvEmployeeType.setText(employee.getPosition());
        holder.ivEmployeeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.profile)); // Set a default image
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        private TextView tvEmployeeName, tvEmployeeType;
        private ImageView ivEmployeeImage;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeType = itemView.findViewById(R.id.tvEmployeeType);
            ivEmployeeImage = itemView.findViewById(R.id.ivEmployeeImage);
        }
    }
}
