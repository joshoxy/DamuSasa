package com.example.damusasa.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Adapter.MyBranchAdapter;
import com.example.damusasa.Common.SpacesItemDecoration;
import com.example.damusasa.Interface.AllCenters;
import com.example.damusasa.Interface.IBranchLoadListener;
import com.example.damusasa.Model.Branch;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
import dmax.dialog.SpotsDialog;


public class BookingStep1Fragment extends Fragment implements AllCenters, IBranchLoadListener {

//His allSalon Ref is my Centers **
CollectionReference Centers;
CollectionReference branchRef;


//His IAllSalonLoadListener is here **
AllCenters allCenters;
IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.recycler_one) RecyclerView recycler_centers;
    @BindView(R.id.spinner) MaterialSpinner spinner;
    Unbinder unbinder;
    AlertDialog dialog;

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
        allCenters = this;  //His iAllSalonLoadListener is here **
        iBranchLoadListener = this;

        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_one, container,false);
        unbinder = ButterKnife.bind(this, itemView);
        initView();

        loadAllCenters();
        return itemView;
    }

    private void initView() {
        recycler_centers.setHasFixedSize(true);
        recycler_centers.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_centers.addItemDecoration(new SpacesItemDecoration(4));
    }

    //Original LoadAllCenters  (expand)
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
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0){
                    loadBranchofCity(item.toString());
                }else
                    recycler_centers.setVisibility(View.GONE);
            }
        });

    }

    //Original loadBranch
    private void loadBranchofCity(String cityName) {
        dialog.show();
        branchRef = FirebaseFirestore.getInstance()
                .collection("DonationCenters")
                .document(cityName)
                .collection("Branch");

        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Branch> list = new ArrayList<>();
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        Branch branch = documentSnapshot.toObject(Branch.class);
                        branch.setBranchId(documentSnapshot.getId());
                        list.add(branch);
                    }
                    iBranchLoadListener.onBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });

    }
    //end of load branch


    @Override
    public void onAllCenterLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }

    //Original on branchLoadSuccess
    @Override
    public void onBranchLoadSuccess(List<Branch> branchList) {
        MyBranchAdapter adapter = new MyBranchAdapter(getActivity(), branchList);
        recycler_centers.setAdapter(adapter);
        recycler_centers.setVisibility(View.VISIBLE);
        dialog.dismiss();

    }



    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();

    }
}
