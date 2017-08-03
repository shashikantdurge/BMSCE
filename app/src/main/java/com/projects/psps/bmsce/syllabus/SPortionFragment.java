package com.projects.psps.bmsce.syllabus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.psps.bmsce.R;

/*
 Created by vasan on 22-07-2017.
 */

public class SPortionFragment extends Fragment {

    public SPortionFragment(){
        //Should be empty
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View potionRootView=inflater.inflate(R.layout.item_all_course,container,false);
        return potionRootView;
    }
}
