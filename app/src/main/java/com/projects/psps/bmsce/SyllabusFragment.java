package com.projects.psps.bmsce;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.psps.bmsce.firebase.FbCourse;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SyllabusFragment extends Fragment implements View.OnClickListener,CompoundButton.OnCheckedChangeListener,AdapterView.OnItemSelectedListener{

    ListView portionList,myCourseList,respctiveCourseList;
    Spinner branchSpn,semSpn;
    ImageButton addPortionImgBtn,addToMyCourseImgBtn;
    ExpandableLayout portionExpandCachappa,myCourseExpandCachappa,allCourseExpandCachappa;
    ArrayAdapter<String> spinnerAdapter;
    String lastSelectedBranch="--";
    ArrayList<String> semesters;
    Context context;
    DatabaseReference dataBaseRef;

    public SyllabusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=inflater.getContext();
        View rootView=inflater.inflate(R.layout.fragment_syllabus, container, false);
        //Initailize widgets and set onClick Listener
        portionList=(ListView)rootView.findViewById(R.id.list_of_portion);
        myCourseList=(ListView)rootView.findViewById(R.id.list_of_my_course);
        respctiveCourseList=(ListView)rootView.findViewById(R.id.list_respective_course);
        branchSpn=(Spinner) rootView.findViewById(R.id.spn_branch);
        semSpn=(Spinner) rootView.findViewById(R.id.spn_sem);
        portionExpandCachappa=(ExpandableLayout)rootView.findViewById(R.id.cachappa_expand_portion);
        myCourseExpandCachappa=(ExpandableLayout)rootView.findViewById(R.id.cachappa_expand_my_course);
        allCourseExpandCachappa=(ExpandableLayout)rootView.findViewById(R.id.cachappa_expand_all_course);
        addPortionImgBtn=(ImageButton)rootView.findViewById(R.id.btn_add_portion);
        addToMyCourseImgBtn=(ImageButton)rootView.findViewById(R.id.btn_add_to_my_course);
        ToggleButton expandPortionToggle=(ToggleButton)rootView.findViewById(R.id.toggle_btn_expand_portion);
        ToggleButton expandMyCourseToggle=(ToggleButton)rootView.findViewById(R.id.toggle_btn_expand_my_course);
        ToggleButton expandALlCourseToggle=(ToggleButton)rootView.findViewById(R.id.toggle_btn_expand_all_courses);

        addToMyCourseImgBtn.setOnClickListener(this);
        addPortionImgBtn.setOnClickListener(this);
        expandPortionToggle.setOnCheckedChangeListener(this);
        expandMyCourseToggle.setOnCheckedChangeListener(this);
        expandALlCourseToggle.setOnCheckedChangeListener(this);
        branchSpn.setOnItemSelectedListener(this);
        semSpn.setOnItemSelectedListener(this);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        semesters=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.semesters_for_majority)));
        spinnerAdapter=new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,semesters);
        semSpn.setAdapter(spinnerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_portion:
                break;
            case R.id.btn_add_to_my_course:
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.toggle_btn_expand_portion:
                if(isChecked){
                    addPortionImgBtn.setBackgroundResource(R.drawable.btn_bg_tl);
                    portionExpandCachappa.expand();
                }
                else {
                    addPortionImgBtn.setBackgroundResource(R.drawable.btn_bg_tl_bl);
                    portionExpandCachappa.collapse();
                }
                break;
            case R.id.toggle_btn_expand_my_course:
                if(isChecked){
                    addToMyCourseImgBtn.setBackgroundResource(R.drawable.btn_bg_tl);
                    myCourseExpandCachappa.expand();
                }
                else {
                    addToMyCourseImgBtn.setBackgroundResource(R.drawable.btn_bg_tl_bl);
                    myCourseExpandCachappa.collapse();
                }
                break;
            case R.id.toggle_btn_expand_all_courses:
                if(isChecked){
                    allCourseExpandCachappa.expand();
                }
                else {
                    allCourseExpandCachappa.collapse();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String branch=String.valueOf(branchSpn.getSelectedItem()).substring(0,2);
        String sem=String.valueOf(semSpn.getSelectedItem()).substring(0,1);

        if(parent.getId()==R.id.spn_branch) {
            if (branch.equals("AT") && !lastSelectedBranch.equals("AT")) {        //Add 9th and 10th smesters to the list
                semesters.add("9th sem");
                semesters.add("Xth sem");
                spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, semesters);
                semSpn.setAdapter(spinnerAdapter);
                if(!sem.equals("-")){
                    semSpn.setSelection(Integer.parseInt(sem));
                }
            } else if (!branch.equals("AT") && lastSelectedBranch.equals("AT")) {           //Remove 9th and 10th semesters from list
                semesters.remove(10);
                semesters.remove(9);
                spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, semesters);
                semSpn.setAdapter(spinnerAdapter);
                if(!sem.equals("9") && !sem.equals("X") && !sem.equals("-")){
                    semSpn.setSelection(Integer.parseInt(sem));
                }
            }
            lastSelectedBranch=branch;
        }

        if(Objects.equals(branch, "--") || Objects.equals(sem,"-")){
            respctiveCourseList.setAdapter(null);
            return;
        }


        dataBaseRef=FirebaseDatabase.getInstance().getReference("/branch_sem_courses/"+branch+sem);
        dataBaseRef.addListenerForSingleValueEvent(courseReader);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    ValueEventListener courseReader=new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            FbCourse[] courses=new FbCourse[(int) dataSnapshot.getChildrenCount()];
            int i=0;
            ArrayList<FbCourse> coursesList;
            coursesList=new ArrayList<>();
            for(DataSnapshot course:dataSnapshot.getChildren()){
                courses[i]=course.getValue(FbCourse.class);
                courses[i].setCourseCode(course.getKey());
                courses[i].setTotalCredits();
                coursesList.add(courses[i]);
            }

            respctiveCourseList.setAdapter(new CourseAdapter(context,coursesList));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}

