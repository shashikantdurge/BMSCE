package com.projects.psps.bmsce.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vasan on 22-07-2017.
  */

public class BranchSemCourses extends RealmObject {
    @PrimaryKey
    private String branchSem;
    private RealmList<Course> courses;

    public BranchSemCourses(){
        //Should be Empty
    }

    public String getBranchSem() {
        return branchSem;
    }

    public void setBranchSem(String branchSem) {
        this.branchSem = branchSem;
    }

    public RealmList<Course> getCourses() {
        return courses;
    }

    public void setCourses(RealmList<Course> courses) {
        this.courses = courses;
    }
}
