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
import com.example.damusasa.Model.TimeSlot;
import com.example.damusasa.R;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());
        //Paste without if()
        holder.card_time_slot.setTag(Common.DISABLE_TAG);
        holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        holder.txt_time_slot_description.setText("Available");
        holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
        holder.txt_time_slot.setTextColor(context.getResources()
                .getColor(android.R.color.black));

        //Add all cards to list (Might one to remove)
        if (!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);

        //Check if time slot is available
        holder.setiRecyclerItemSelected(new IRecyclerItemSelected() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //Loop all positions
                for (CardView cardView:cardViewList) {
                    if (cardView.getTag() == null)  //Change color of available slots
                        cardView.setCardBackgroundColor(context.getResources()
                                .getColor(android.R.color.white));
                }

                //Change colour of selected card
                holder.card_time_slot.setCardBackgroundColor(context.getResources()
                .getColor(android.R.color.holo_blue_dark));

                //Enable Next Button
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_TIME_SLOT, position); //Put index of selected slot
                intent.putExtra(Common.KEY_STEP, 2); //Go to step 2...his was 3
                localBroadcastManager.sendBroadcast(intent);
            }
        });


        /*if (timeSlotList.size() == 0) //Available time slots
        {
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_time_slot.setTextColor(context.getResources()
                    .getColor(android.R.color.black));

        }
        else{ //If positions are booked
            for (TimeSlot slotValue:timeSlotList)
            {
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == position){
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.txt_time_slot_description.setText("Full");
                    holder.txt_time_slot_description.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                    holder.txt_time_slot.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                }

            }
        }*/

    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_time_slot, txt_time_slot_description;
        CardView card_time_slot;
        IRecyclerItemSelected iRecyclerItemSelected;

        public void setiRecyclerItemSelected(IRecyclerItemSelected iRecyclerItemSelected) {
            this.iRecyclerItemSelected = iRecyclerItemSelected;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = (CardView) itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView) itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView) itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelected.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
