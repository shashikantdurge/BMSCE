package com.projects.psps.bmsce;

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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.projects.psps.bmsce.firebase.FbCourse;
import com.projects.psps.bmsce.realm.BranchSemCourses;
import com.projects.psps.bmsce.realm.Course;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionSnapshot;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by vasan on 22-07-2017.
 */

public class SAllCourseFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    Spinner branchSpn,semSpn;
    RecyclerView respectiveCourseListRv;
    String lastSelectedBranch="--";
    ArrayList<String> semesters;
    SpinnerAdapter spinnerAdapter;
    DatabaseReference syllabusReference;
    RealmList<Course> courseRealmList;
    BranchSemCourses branchSemCourses;
    boolean decorationNotGiven=true;


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
                if (branch.equals("AT") && !lastSelectedBranch.equals("AT")) {        //Add 9th and 10th smesters to the list
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
                FirebaseDatabase.getInstance().getReference("/branch_sem_courses/"+branch+sem).addListenerForSingleValueEvent(courseReader);
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

    ValueEventListener courseReader=new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
           //TODO : put it in the local realm database.PENDING.......
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
            RealmCourseAdapter courseAdapter=new RealmCourseAdapter(branchSemCourses.getCourses().sort("courseType"),false);
            StickyHeaderDecoration decoration=new StickyHeaderDecoration(courseAdapter);
            respectiveCourseListRv.setAdapter(courseAdapter);
            if (decorationNotGiven) {
                respectiveCourseListRv.addItemDecoration(decoration,1);
                decorationNotGiven=false;
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
