package com.projects.psps.bmsce;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_chat:
                //change the icon
                Toast.makeText(MainActivity.this, "CHAT", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_main_event:
                Toast.makeText(MainActivity.this, "EVENT", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_main_syllabus:
                Toast.makeText(MainActivity.this, "SYLLABUS", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
