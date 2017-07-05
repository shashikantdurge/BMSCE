package com.projects.psps.bmsce;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.psps.bmsce.firebase.FbCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vasan on 02-07-2017.
 */

class CourseAdapter extends ArrayAdapter<FbCourse> implements View.OnClickListener{

    CourseAdapter(@NonNull Context context, @NonNull ArrayList<FbCourse> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FbCourse course=getItem(position);
        if(convertView==null) convertView=LayoutInflater.from(getContext()).inflate(R.layout.item_course,parent,false);
        TextView courseNameTv= (TextView) convertView.findViewById(R.id.tv_course_name);
        TextView courseCodeTv=(TextView) convertView.findViewById(R.id.tv_course_code);
        TextView totalCreditsTv=(TextView) convertView.findViewById(R.id.tv_total_credtis);
        courseNameTv.setText(course.getCourseName());
        courseCodeTv.setText(course.getCourseCode());
        totalCreditsTv.setText(String.format(Locale.ENGLISH,"Credits %d",course.getTotalCredits()));
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

}
