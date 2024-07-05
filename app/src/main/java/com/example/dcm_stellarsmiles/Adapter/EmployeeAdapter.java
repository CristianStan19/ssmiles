package com.example.dcm_stellarsmiles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcm_stellarsmiles.Classes.DatabaseHelper;
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
        DatabaseHelper dbHelper = new DatabaseHelper();
        switch (employee.getPosition()) {
            case "Doctor":
                dbHelper.getDoctorSpecialization(employee.getName(), new DatabaseHelper.Callback<String>() {
                    @Override
                    public void onSuccess(String specialization) {
                        holder.tvEmployeeType.setText(specialization);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        holder.tvEmployeeType.setText("Specialization not found");
                    }
                });
                break;
            case "Assistant":
                dbHelper.getAssistantDepartment(employee.getName(), new DatabaseHelper.Callback<String>() {
                    @Override
                    public void onSuccess(String department) {
                        holder.tvEmployeeType.setText(department);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        holder.tvEmployeeType.setText("Department not found");
                    }
                });
                break;
            case "Receptionist":
                holder.tvEmployeeType.setVisibility(View.GONE);
                break;
            default:
                holder.tvEmployeeType.setText(employee.getPosition());
                break;
        }
        switch (employee.getName()) {
            case "Mihai Dorcea":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.doctor1));
                break;
            case "Ana Maria Ionescu":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.doctor5));
                break;
            case "Mircea Alexandru":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.doctor3));
                break;
            case "Andrei Popescu":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.doctor4));
                break;
            case "Mihai Popa":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.assistant4));
                break;
            case "Mihaela Ivan":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.receptionist1));
                break;
            case "Doina Radu":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.doctor6));
                break;
            case "Cristina Popescu":
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.assistant1));
                break;
            default:
                // Set default image if no case matches
                holder.ivEmployeeImage.setImageDrawable(context.getDrawable(R.drawable.profile));
                break;
        }
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
