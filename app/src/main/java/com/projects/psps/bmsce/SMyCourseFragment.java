package com.projects.psps.bmsce;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.psps.bmsce.realm.MyCourses;

import io.realm.Realm;

/**
 Created by vasan on 24-07-2017.
 */

public class SMyCourseFragment extends Fragment {
    private MyCourses myCourses;
    private final static  String TAG="MY_COURSES";

    SMyCourseFragment(){
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_my_course,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_course_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                myCourses=realm.where(MyCourses.class).findFirst();
            }
        });
        /*if(myCourses!=null)
            recyclerView.setAdapter(new RealmAllCourseAdapter(myCourses.getCourses(),true));*/
        try {
            recyclerView.setAdapter(new RealmMyCourseAdapter(myCourses.getCourses(),true));
        }catch (NullPointerException e){
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    myCourses= realm.createObject(MyCourses.class);

                }
            });
            recyclerView.setAdapter(new RealmMyCourseAdapter(myCourses.getCourses(),true));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG,"onAttachFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG,"onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

    }

}
