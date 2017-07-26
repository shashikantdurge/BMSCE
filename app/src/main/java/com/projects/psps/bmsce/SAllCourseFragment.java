package com.projects.psps.bmsce;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.psps.bmsce.realm.BranchSemCourses;
import com.projects.psps.bmsce.realm.Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;

/*
  Created by vasan on 22-07-2017.
 */

public class SAllCourseFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private Spinner branchSpn;
    private Spinner semSpn;
    private RecyclerView respectiveCourseListRv;
    private String lastSelectedBranch="--";
    private ArrayList<String> semesters;
    private SpinnerAdapter spinnerAdapter;
    private DatabaseReference syllabusReference;
    private RealmList<Course> courseRealmList;
    private BranchSemCourses branchSemCourses;
    private final static String TAG="ALL_COURSES";



    public SAllCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syllabusReference=FirebaseDatabase.getInstance().getReference("/branch_sem_courses");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_all_course,container,false);
        branchSpn=(Spinner)rootView.findViewById(R.id.spn_branch);
        semSpn=(Spinner)rootView.findViewById(R.id.spn_sem);
        respectiveCourseListRv=(RecyclerView)rootView.findViewById(R.id.rv_respective_course);
        respectiveCourseListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        respectiveCourseListRv.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        RealmAllCourseAdapter courseAdapter=new RealmAllCourseAdapter(null,false);
        StickyHeaderDecoration decoration=new StickyHeaderDecoration(courseAdapter);
        respectiveCourseListRv.addItemDecoration(decoration,1);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        semesters=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.semesters_for_majority)));
        spinnerAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,semesters);
        semSpn.setAdapter(spinnerAdapter);
        semSpn.setOnItemSelectedListener(this);
        branchSpn.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spn_branch:
                String branch=String.valueOf(branchSpn.getSelectedItem()).substring(0,2);
                String sem=String.valueOf(semSpn.getSelectedItem()).substring(0,1);
                if (branch.equals("AT") && !lastSelectedBranch.equals("AT")) {        //Add 9th and 10th semesters to the list
                    semesters.add("9th sem");
                    semesters.add("Xth sem");
                    spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, semesters);
                    semSpn.setAdapter(spinnerAdapter);
                    if(!sem.equals("-")){
                        semSpn.setSelection(Integer.parseInt(sem));
                    }
                } else if (!branch.equals("AT") && lastSelectedBranch.equals("AT")) {           //Remove 9th and 10th semesters from list
                    semesters.remove(10);
                    semesters.remove(9);
                    spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, semesters);
                    semSpn.setAdapter(spinnerAdapter);
                    if(!sem.equals("9") && !sem.equals("X") && !sem.equals("-")){
                        semSpn.setSelection(Integer.parseInt(sem));
                    }
                }
                lastSelectedBranch=branch;
                if(Objects.equals(branch, "--") || Objects.equals(sem,"-")){
                    respectiveCourseListRv.setAdapter(null);
                    return;
                }
                loadCourses(branch+sem);

                break;
            case R.id.spn_sem:
                String branch1 = String.valueOf(branchSpn.getSelectedItem()).substring(0, 2);
                String sem1 = String.valueOf(semSpn.getSelectedItem()).substring(0, 1);
                if(Objects.equals(branch1, "--") || Objects.equals(sem1,"-")){
                    respectiveCourseListRv.setAdapter(null);
                    return;
                }
                syllabusReference.child(branch1+sem1).addListenerForSingleValueEvent(courseReader);
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadCourses(String branchSem){
        //Check for offline . if not present get it from online and show that its from offline and might have changed.
        branchSemCourses=Realm.getDefaultInstance().where(BranchSemCourses.class).equalTo("branchSem",branchSem).findFirst();
        if(branchSemCourses==null){
            //Load from the cloud
            Log.d(TAG,"loadCourses , ONLINE ");
            FirebaseDatabase.getInstance().getReference("/branch_sem_courses/"+branchSem).addListenerForSingleValueEvent(courseReader);
        }
        else{
            RealmAllCourseAdapter courseAdapter=new RealmAllCourseAdapter(branchSemCourses.getCourses().sort("courseType"),false);
            respectiveCourseListRv.setAdapter(courseAdapter);
        }

    }

    private final ValueEventListener courseReader=new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            Realm realm=Realm.getDefaultInstance();
            courseRealmList=new RealmList<>();
            //BranchSemCourses branchSemCourses=new BranchSemCourses(dataSnapshot.getKey());
            for(final DataSnapshot courseSnapShot:dataSnapshot.child("core_lab_mandatory").getChildren()){
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d("REALM","COURSE CREATING");
                        Course course=new Course(courseSnapShot.getKey());
                        course.createOrUpdate(courseSnapShot,Course.CREATE_WITHOUT_PRIMARY,0);
                        course=realm.copyToRealmOrUpdate(course);
                        courseRealmList.add(course);
                    }
                });
            }
            if(dataSnapshot.hasChild("electives")){
                for(final DataSnapshot electivesSnapshot:dataSnapshot.child("electives").getChildren()){
                    for(final DataSnapshot courseSnapshot:electivesSnapshot.getChildren()){
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Log.d("REALM","COURSE CREATING");
                                Course course=new Course(courseSnapshot.getKey());
                                course.createOrUpdate(courseSnapshot,Course.CREATE_WITHOUT_PRIMARY,Integer.parseInt(electivesSnapshot.getKey()));
                                course=realm.copyToRealmOrUpdate(course);
                                courseRealmList.add(course);
                            }
                        });
                    }
                }
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    branchSemCourses=new BranchSemCourses();
                    branchSemCourses.setBranchSem(dataSnapshot.getKey());
                    branchSemCourses.setCourses(courseRealmList);
                    branchSemCourses=realm.copyToRealmOrUpdate(branchSemCourses);
                }
            });
            RealmAllCourseAdapter courseAdapter=new RealmAllCourseAdapter(branchSemCourses.getCourses().sort("courseType"),false);
            respectiveCourseListRv.setAdapter(courseAdapter);


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        try{
            respectiveCourseListRv.getAdapter().notifyDataSetChanged();
        }catch (NullPointerException e){
            Log.d(TAG,"onStart"+e.getMessage());
        }

    }
}
