package com.projects.psps.bmsce.realm;

import com.projects.psps.bmsce.firebase.FbCourse;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by vasan on 22-07-2017.
 */

public class MyCourses extends RealmObject {

    RealmList<Course> courses;
    String branch;
    int sem;

    public MyCourses() {
    }

    void addToMyCourses(FbCourse course) {

    }
    void removeFromMyCourses(String course) {

    }
}