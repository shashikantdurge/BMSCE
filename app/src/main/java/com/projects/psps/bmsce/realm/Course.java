package com.projects.psps.bmsce.realm;

import com.google.firebase.database.DataSnapshot;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vasan on 29-06-2017.
 */

public class Course extends RealmObject{

    public final static int CREATE_WITHOUT_PRIMARY=1;
    @Index
    private String courseName;
    @Index
    private String shortName;
    private Float version;
    @PrimaryKey
    private String courseCode;
    private int l;
    private int t;
    private int p;
    private int s;
    private int totalCredits;
    private int courseType;


    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public int getCourseType() {
        return courseType;
    }

    public void setCourseType(int courseType) {
        this.courseType = courseType;
    }


    public Course(){
        //Should Be Empty
    }
    public Course(String courseCode){
        this.courseCode=courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Float getVersion() {
        return version;
    }

    public void setVersion(Float version) {
        this.version = version;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits() {
        this.totalCredits = l+t+p+s;
    }

    public void createOrUpdate(DataSnapshot dataSnapshot,int createAttr,int coursetype){
        if (createAttr == CREATE_WITHOUT_PRIMARY) {
            this.setL(Integer.parseInt(String.valueOf(dataSnapshot.child("l").getValue()) ));
            this.setT(Integer.parseInt(String.valueOf(dataSnapshot.child("t").getValue())));
            this.setP(Integer.parseInt(String.valueOf(dataSnapshot.child("p").getValue())));
            this.setS(Integer.parseInt(String.valueOf(dataSnapshot.child("s").getValue())));
            this.setCourseName((String)dataSnapshot.child("courseName").getValue());
            this.setShortName((String)dataSnapshot.child("shortName").getValue());
            this.setVersion(Float.parseFloat(String.valueOf(dataSnapshot.child("version").getValue())));
            this.setTotalCredits();
            this.setCourseType(coursetype);
        }


    }
}

