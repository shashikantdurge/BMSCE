package com.projects.psps.bmsce;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.projects.psps.bmsce.syllabus.SyllabusFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,MainActivityListener{

    private FragmentManager fragmentManager;
    //final static String TAG="MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragmentManager=getSupportFragmentManager();

        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_main,new SyllabusFragment());
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        switch (item.getItemId()) {

            case R.id.menu_main_chat:
                //change the icon
                break;
            case R.id.menu_main_event:
                break;
            case R.id.menu_main_syllabus:
                fragmentTransaction.replace(R.id.fragment_container_main,new SyllabusFragment());
                fragmentTransaction.commit();
                break;
        }
        return true;
    }


    @Override
    public void onMenuItemClick() {

    }
}
