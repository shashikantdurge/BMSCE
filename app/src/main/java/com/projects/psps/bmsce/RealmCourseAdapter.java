package com.projects.psps.bmsce;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Created by vasan on 22-07-2017.
 */

class RealmCourseAdapter  extends RealmRecyclerViewAdapter<Course,RealmCourseAdapter.MyViewHolder> implements CourseHeaderAdapter<RealmCourseAdapter.HeaderHolder>{

    private static int courseType=0,n;
    private static int[] courseTypeCount;
    RealmCourseAdapter(@Nullable OrderedRealmCollection<Course> data, boolean autoUpdate) {
        super(data, autoUpdate);
        courseTypeCount=new int[data.where().distinct("courseType").size()];
        n=courseTypeCount.length;
        if (n == 0) {
            Log.d("NO COURSES FOUND"," RETURNING!!!");
            return;
        }
        courseTypeCount[0]=(int) data.where().equalTo("courseType",0).count();
        for(int i=1;i<n;i++) {
            courseTypeCount[i]=(int) data.where().equalTo("courseType",i).count()+courseTypeCount[i-1];
        }
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
    @Override
    public long getHeaderId(int position) {
        Log.d("Header Position ",String.valueOf(n));
        for(int i=0;i<n;i++){
            if(position>courseTypeCount[i])
                return position/courseTypeCount[i]+1;
        }
        return  position/(courseTypeCount[0])+1;
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_test, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        int head= (int) (getHeaderId(position)-1);
        switch (head){
            case 0:
                viewholder.header.setText("Core/Lab/Mandatory");
                break;
            default:
                viewholder.header.setText("Elective "+head);
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
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView header;

        HeaderHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView;
        }
    }
}
