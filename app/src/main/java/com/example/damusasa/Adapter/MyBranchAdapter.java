package com.example.damusasa.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Common.Common;
import com.example.damusasa.Interface.IRecyclerItemSelected;
import com.example.damusasa.Model.Branch;
import com.example.damusasa.R;

import java.util.ArrayList;
import java.util.List;

public class MyBranchAdapter extends RecyclerView.Adapter<MyBranchAdapter.MyViewHolder> {

    Context context;
    List<Branch> branchList; //His Salon is my Branch and salonList is branchList
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyBranchAdapter(Context context, List<Branch> branchList) {
        this.context = context;
        this.branchList = branchList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
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
        if (!cardViewList.contains(holder.card_branch))
            cardViewList.add(holder.card_branch);

        holder.setiRecyclerItemSelected(new IRecyclerItemSelected() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //Set white bg for card snot selected
                for (CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                //Set color for selected item
                holder.card_branch.setCardBackgroundColor(context.getResources()
                .getColor(android.R.color.holo_blue_dark));

                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SALON_STORE, branchList.get(pos));
                intent.putExtra(Common.KEY_STEP, 1);  //Might need to disable this
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_branch_name, txt_branch_address;
        CardView card_branch;
        IRecyclerItemSelected iRecyclerItemSelected;

        public void setiRecyclerItemSelected(IRecyclerItemSelected iRecyclerItemSelected) {
            this.iRecyclerItemSelected = iRecyclerItemSelected;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_branch = (CardView) itemView.findViewById(R.id.card_branch);
            txt_branch_name = (TextView) itemView.findViewById(R.id.txt_branch_name);
            txt_branch_address = (TextView) itemView.findViewById(R.id.txt_branch_address);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelected.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
