package com.projects.psps.bmsce.realm;

import android.os.AsyncTask;
import android.util.Log;

import com.projects.psps.bmsce.firebase.FbCourse;

import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by vasan on 22-07-2017.
 */

public class MyCourses extends RealmObject {

    private RealmList<Course> courses;
    String branch;
    int sem;

    public MyCourses() {
    }

    public void addToMyCourses(Course course) {
        new AsyncTask<String,Void,Boolean>() {
            @Override
            protected Boolean doInBackground(final String... params) {
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d("MY_COURSES","Adding"+params[0]);
                        MyCourses myCourses=realm.where(MyCourses.class).findFirst();
                        Course course1=realm.where(Course.class).equalTo("courseCode",params[0]).findFirst();
                        myCourses.getCourses().add(course1);
                    }
                });
                return true;
            }


        }.execute(course.getCourseCode());

    }
    public void removeFromMyCourses(RealmResults<Course> courseCodes, Realm realm) {
        courses.removeAll(courseCodes);

    }

    public RealmList<Course> getCourses() {
        return courses;
    }

    public void setCourses(RealmList<Course> courses) {
        this.courses = courses;
    }


}
