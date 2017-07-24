package com.projects.psps.bmsce.realm;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by vasan on 23-07-2017.
 */

public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        // The Realm image will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

}
