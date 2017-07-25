package com.projects.psps.bmsce;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.psps.bmsce.realm.MyCourses;

import io.realm.Realm;

/**
 * Created by vasan on 24-07-2017.
 */

public class SMyCourseFragment extends Fragment {
    RecyclerView recyclerView;
    MyCourses myCourses;

    SMyCourseFragment(){
        //Empty Constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_my_course,container,false);
        recyclerView=(RecyclerView)view.findViewById(R.id.rv_course_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                myCourses=realm.where(MyCourses.class).findFirst();
            }
        });
        if(myCourses!=null)
            recyclerView.setAdapter(new RealmCourseAdapter(myCourses.getCourses(),true));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
