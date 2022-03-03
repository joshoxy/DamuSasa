package com.example.damusasa.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Interface.AllCenters;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BookingStep1Fragment extends Fragment implements AllCenters {

//His allSalon Ref is my Centers **
    CollectionReference Centers;
CollectionReference Branches;


//His IAllSalonLoadListener is here **
AllCenters allCenters;

    @BindView(R.id.recycler_one) RecyclerView recycler_centers;
    @BindView(R.id.spinner) MaterialSpinner spinner;
    Unbinder unbinder;

    static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance() {
        if (instance==null)
            instance = new BookingStep1Fragment();
            return instance;
        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Centers = FirebaseFirestore.getInstance().collection("DonationCenters");
        allCenters = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_one, container,false);
        unbinder = ButterKnife.bind(this, itemView);

        loadAllCenters();
        return itemView;
    }

    private void loadAllCenters() {
        Centers.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> list = new ArrayList<>();
                            list.add("Please choose location");
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                                list.add(documentSnapshot.getId());
                            allCenters.onAllCentersLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                allCenters.onAllCenterLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllCentersLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);

    }

    @Override
    public void onAllCenterLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }
}
