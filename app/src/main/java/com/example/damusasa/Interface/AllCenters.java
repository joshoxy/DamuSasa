package com.example.damusasa.Interface;

import java.util.List;

public interface AllCenters {
    void onAllCentersLoadSuccess(List<String> areaNameList);
    void onAllCenterLoadFailed(String message);
}
