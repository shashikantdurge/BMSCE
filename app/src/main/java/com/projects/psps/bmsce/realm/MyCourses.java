package com.projects.psps.bmsce.realm;

import com.projects.psps.bmsce.firebase.FbCourse;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

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
        courses.add(course);

    }
    public void removeFromMyCourses(String courseCode) {

    }

    public RealmList<Course> getCourses() {
        return courses;
    }

    public void setCourses(RealmList<Course> courses) {
        this.courses = courses;
    }
}
