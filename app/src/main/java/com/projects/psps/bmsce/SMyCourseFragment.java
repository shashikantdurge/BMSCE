package com.projects.psps.bmsce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.projects.psps.bmsce.realm.Course;
import com.projects.psps.bmsce.realm.MyCourses;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 Created by vasan on 24-07-2017.
 */

public class SMyCourseFragment extends Fragment {
    private MyCourses myCourses;
    private final static  String TAG="MY_COURSES";
    List<String> selectedCourses =null;
    boolean longClicked=false;
    ActionMode actionMode;
    RecyclerView recyclerView;

    MainActivityListener mainActivityListener;
    SMyCourseFragment(){
        //Empty Constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityListener= (MainActivityListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_my_course,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_course_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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


    class RealmMyCourseAdapter extends RealmRecyclerViewAdapter<Course,RealmMyCourseAdapter.MyViewHolder> {

        RealmMyCourseAdapter(@Nullable OrderedRealmCollection<Course> data, boolean autoUpdate) {
            super(data, autoUpdate);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_course, parent, false);
            return new MyViewHolder(v);
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Course course = getItem(position);
            if (course != null) {
                holder.courseName.setText(course.getCourseName());
                holder.courseCode.setText(course.getCourseCode());
                holder.totalCredits.setText(String.format(Locale.ENGLISH, "Credits %d", course.getTotalCredits()));
            }


        }


        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,AdapterView.OnLongClickListener{
            final TextView courseName;
            final TextView courseCode;
            final TextView totalCredits;

            public Course data;


            MyViewHolder(View view) {
                super(view);
                courseName = (TextView) view.findViewById(R.id.tv_course_name);
                courseCode = (TextView) view.findViewById(R.id.tv_course_code);
                totalCredits = (TextView) view.findViewById(R.id.tv_total_credtis);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {

                String courseCode=getData().get(getAdapterPosition()).getCourseCode();
                if(longClicked){
                    if(selectedCourses.contains(courseCode)){
                        v.setBackgroundColor(Color.argb(51,240,238,238));
                        selectedCourses.remove(courseCode);
                        if(selectedCourses.isEmpty())
                            actionMode.finish();

                    }else {
                        v.setBackgroundColor(Color.argb(100,120,120,144));
                        selectedCourses.add(courseCode);
                    }
                    return;

                }


                //noinspection ConstantConditions
                Intent intent=new Intent(v.getContext(),SyllabusViewActivity.class);
                if(Realm.getDefaultInstance().where(MyCourses.class).equalTo("courses.courseCode",courseCode).findFirst()!=null){
                    intent.putExtra(SyllabusViewActivity.IS_COURSE_ONLINE,false);
                }
                else {
                    intent.putExtra(SyllabusViewActivity.IS_COURSE_ONLINE,true);
                }
                intent.putExtra(SyllabusViewActivity.COURSE_CODE,courseCode);
                v.getContext().startActivity(intent);
            }

            @Override
            public boolean onLongClick(View view) {
                String courseCode=getData().get(getAdapterPosition()).getCourseCode();
                if(!longClicked){
                    longClicked=true;
                    //TODO Set Action Bar
                    actionMode=((MainActivity)getContext()).startActionMode(callback);
                    selectedCourses =new ArrayList<>();

                }
                if(selectedCourses.contains(courseCode)){
                    view.setBackgroundColor(Color.argb(51,240,238,238));
                    selectedCourses.remove(courseCode);
                }else {
                    view.setBackgroundColor(Color.argb(100,120,120,144));
                    selectedCourses.add(courseCode);
                }
                view.setBackgroundColor(Color.argb(100,120,120,144));

                return true;
            }
        }
    }

    ActionMode.Callback callback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if(mode==null)
                Log.d(TAG,"Mode Is Null");
            else
                mode.getMenuInflater().inflate(R.menu.my_course_fragment,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_delete:
                    Log.d(TAG,"Delete the items: "+ selectedCourses.toString());
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            String[] deleteCourses=new String[selectedCourses.size()];
                            deleteCourses=selectedCourses.toArray(deleteCourses);
                            RealmResults<Course> realmResults=RealmQuery.createQuery(realm,Course.class).in("courseCode",deleteCourses).findAll();

                            myCourses.removeFromMyCourses(realmResults,realm);
                        }
                    });
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            longClicked=false;
            selectedCourses.clear();

        }
    };


}
