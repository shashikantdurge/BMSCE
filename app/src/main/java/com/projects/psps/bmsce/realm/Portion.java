package com.projects.psps.bmsce.realm;

import java.util.Date;
import io.realm.RealmList;
import io.realm.RealmObject;

/*
 Created by vasan on 22-07-2017.
 */

public class Portion extends RealmObject {
    Course course;
    Float courseVersion;
    String title;
    String description;
    Date createdOn;
    Boolean isCourseUpToDate;
    RealmList<Portion> portions;

    public Portion() {
    }
}
