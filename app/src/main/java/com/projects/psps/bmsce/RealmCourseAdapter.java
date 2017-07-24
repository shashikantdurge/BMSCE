package com.projects.psps.bmsce;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.projects.psps.bmsce.realm.Course;

import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by vasan on 22-07-2017.
 */

class RealmCourseAdapter  extends RealmRecyclerViewAdapter<Course,RealmCourseAdapter.MyViewHolder> {


    RealmCourseAdapter(@Nullable OrderedRealmCollection<Course> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Course course = getItem(position);
        try {
            holder.courseName.setText(course.getCourseName());
            holder.courseCode.setText(course.getCourseCode());
            holder.totalCredits.setText(String.format(Locale.ENGLISH, "Credits %d", course.getTotalCredits()));
        } catch (NullPointerException e) {
            Log.e("Error", e.getMessage());
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName, courseCode, totalCredits;
        public Course data;

        MyViewHolder(View view) {
            super(view);
            courseName = (TextView) view.findViewById(R.id.tv_course_name);
            courseCode = (TextView) view.findViewById(R.id.tv_course_code);
            totalCredits = (TextView) view.findViewById(R.id.tv_total_credtis);
        }

        @Override
        public void onClick(View v) {
        }
    }

}
