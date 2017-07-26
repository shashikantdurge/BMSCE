package com.projects.psps.bmsce;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.psps.bmsce.realm.Course;
import com.projects.psps.bmsce.realm.MyCourses;

import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 Created by vasan on 22-07-2017.
 */

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


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        }

        @Override
        public void onClick(View v) {
            //noinspection ConstantConditions
            String courseCode=getData().get(getAdapterPosition()).getCourseCode();
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
    }



}
