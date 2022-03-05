package com.example.damusasa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Branch;
import com.example.damusasa.R;

import java.util.List;

public class MyBranchAdapter extends RecyclerView.Adapter<MyBranchAdapter.MyViewHolder> {

    Context context;
    List<Branch> branchList; //His Salon is my Branch and salonList is branchList

    public MyBranchAdapter(Context context, List<Branch> branchList) {
        this.context = context;
        this.branchList = branchList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_branch, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_branch_name.setText(branchList.get(position).getName());
        holder.txt_branch_address.setText(branchList.get(position).getAddress());

    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_branch_name, txt_branch_address;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_branch_name = (TextView) itemView.findViewById(R.id.txt_branch_name);
            txt_branch_address = (TextView) itemView.findViewById(R.id.txt_branch_address);
        }
    }
}
