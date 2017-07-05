package com.projects.psps.bmsce.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vasan on 29-06-2017.
 */

public class Course extends RealmObject{
    String courseName;
    String shortName;
    @PrimaryKey
    String courseCode;
    int l,t,p,s,totalCredits;

}

