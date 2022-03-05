package com.example.damusasa.Interface;

import com.example.damusasa.Model.Branch;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Branch> branchList);  //His Salon is my branch
    void onBranchLoadFailed(String message);
}
