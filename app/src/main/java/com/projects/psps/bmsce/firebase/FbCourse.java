package com.projects.psps.bmsce.firebase;

import java.io.Serializable;

/**
 * Created by vasan on 29-06-2017.
 */

public class FbCourse implements Serializable{
    private String courseName;
    private String shortName;
    private String courseCode;
    private int l,t,p,s;



    private int totalCredits;

    public FbCourse(String courseName, String courseCode, int l) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.l = l;
    }

    public FbCourse(){
        //Empty Contructor Required
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
}
