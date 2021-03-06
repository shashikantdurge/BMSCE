package com.projects.psps.bmsce.syllabus;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.projects.psps.bmsce.Manifest;
import com.projects.psps.bmsce.R;
import com.projects.psps.bmsce.realm.Course;
import com.projects.psps.bmsce.realm.MyCourses;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.util.IllegalFormatException;
import java.util.Objects;

import io.realm.Realm;

public class SyllabusViewActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener{

    final static  String TAG="SYLLABUS_VIEW_ACTIVITY";
    public final static String COURSE_CODE="courseCode";
    TextView syllabusTv;
    Spannable spannable;
    ProgressBar progressBar;
    File file;
    ExpandableLayout courseInfoExpand;
    public final static String IS_COURSE_ONLINE="iscourseonine";
    boolean isCourseOnline =true;
    Course course;
    private final static int WRITE_STORAGE_CODE=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_main);
        Intent intent=getIntent();
        course=Realm.getDefaultInstance().where(Course.class).equalTo(COURSE_CODE,intent.getStringExtra(COURSE_CODE)).findFirst();
        isCourseOnline =intent.getBooleanExtra(IS_COURSE_ONLINE,true);
        Log.d(TAG,course.getCourseName());
        syllabusTv=(TextView)findViewById(R.id.tv_syllabus);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        fillInfo(course);
        courseInfoExpand=(ExpandableLayout)findViewById(R.id.expand_course_info);

        ActionBar toolbar=getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(course.getShortName());
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
        checkForPermission();
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            File versionPath=new File(getFilesDir(),String.valueOf(course.getVersion()));
            file= new File(versionPath,course.getCourseCode()+".html");
            if (file.exists()) {
                htmlFileToString(file);
                Log.d(TAG,"Fetching from OFFLINE");
            } else {
                versionPath.mkdirs();
                getFromOnline();
            }
        }

    }



    @TargetApi(23)
    private void checkForPermission(){
        Log.d(TAG,"Debug 1");
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            Log.d(TAG,"Debug 2");
            if(ActivityCompat.shouldShowRequestPermissionRationale(SyllabusViewActivity.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Log.d(TAG,"Debug 3");
                AlertDialog.Builder alertBuilder= new AlertDialog.Builder(this);
                alertBuilder.setTitle("Permission Required");
                alertBuilder.setMessage("Storage permission is required to enable the syllabus in offline.");
                alertBuilder.setCancelable(true);
                alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SyllabusViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_CODE);
                        Log.d(TAG,"Debug 4");
                    }
                });
                alertBuilder.show();
            }else{
                Log.d(TAG,"Debug 5");
                try {
                    Log.d(TAG,"Trying to put it in local cache");
                    file=File.createTempFile(course.getCourseCode(),"html",this.getBaseContext().getCacheDir());
                    Log.d(TAG,"Debug 6");
                    getFromOnline();
                    Log.d(TAG,"Debug 7");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"onRequestPermissionsResult "+e.getMessage());
                }
                ActivityCompat.requestPermissions(SyllabusViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_CODE);
            }
        }
        else{
            File versionPath=new File(getFilesDir(),String.valueOf(course.getVersion()));
            file= new File(versionPath,course.getCourseCode()+".html");
            if (file.exists()) {
                htmlFileToString(file);
                Log.d(TAG,"Fetching from OFFLINE");
            } else {
                versionPath.mkdirs();
                getFromOnline();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==WRITE_STORAGE_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                File versionPath=new File(getFilesDir(),String.valueOf(course.getVersion()));
                file= new File(versionPath,course.getCourseCode()+".html");
                if (file.exists()) {
                    htmlFileToString(file);
                    Log.d(TAG,"Fetching from OFFLINE");
                } else {
                    versionPath.mkdirs();
                    getFromOnline();
                }

            }else{
                try {
                    Log.d(TAG,"Fetching from OnLIne");
                    file=File.createTempFile(course.getCourseCode(),"html",this.getBaseContext().getCacheDir());
                    getFromOnline();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"onRequestPermissionsResult "+e.getMessage());
                }
            }
        }
    }

    void fillInfo(Course course) {
        TextView ltpsTv=(TextView)findViewById(R.id.tv_l);
        ltpsTv.setText(String.valueOf(course.getL()));
        ltpsTv=(TextView)findViewById(R.id.tv_t);
        ltpsTv.setText(String.valueOf(course.getT()));
        ltpsTv=(TextView)findViewById(R.id.tv_p) ;
        ltpsTv.setText(String.valueOf(course.getP()));
        ltpsTv=(TextView)findViewById(R.id.tv_s);
        ltpsTv.setText(String.valueOf(course.getS()));

        //ltpsTv is also used to fill course name and code
        ltpsTv=(TextView)findViewById(R.id.tv_course_name);
        ltpsTv.setText(course.getCourseName());
        ltpsTv=(TextView)findViewById(R.id.tv_course_code);
        ltpsTv.setText(course.getCourseCode());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isCourseOnline) {
            getMenuInflater().inflate(R.menu.my_course_online, menu);
            //menu.findItem(R.id.menu_add_to_my_course).setOnMenuItemClickListener(this);
            menu.findItem(R.id.menu_info).setOnMenuItemClickListener(this);
            menu.findItem(R.id.menu_refresh).setOnMenuItemClickListener(this);
        }
        else {
            getMenuInflater().inflate(R.menu.my_course_offline,menu);
            menu.findItem(R.id.menu_info).setOnMenuItemClickListener(this);
        }


        return super.onCreateOptionsMenu(menu);
    }



    void getFromOnline(){
        if(file==null)
            return;
        FirebaseStorage.getInstance().getReference().child("syllabus/" + course.getCourseCode() + ".html").getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                htmlFileToString(file);
                Log.d(TAG,"fetching from ONLINE ");
                //Local file is created by Firebase itself.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if(Objects.equals(exception.getLocalizedMessage(), "Object does not exist at location.")){
                    try{
                        NotFoundDailog notFoundDailog = NotFoundDailog.newInstance(course.getCourseName(),"");
                        notFoundDailog.show(getSupportFragmentManager(),"not found");
                        progressBar.setVisibility(View.GONE);
                    }catch (IllegalStateException e){
                        Log.e(TAG,e.getLocalizedMessage());
                    }

                }

                Log.d(TAG,exception.getLocalizedMessage());

                // Handle any errors
            }
        });
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void htmlFileToString(File file)  {

        new AsyncTask<File,Void,Spannable>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Spannable doInBackground(File... params) {
                Document document = null;
                try {
                    document=Jsoup.parse(params[0],null);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"Exception"+e);
                }
                SpannableStringBuilder stringBuilder = null;
                if (document != null) {
                    if(Build.VERSION.SDK_INT>=24){
                        stringBuilder=new SpannableStringBuilder();
                        stringBuilder.append(Html.fromHtml(document.outerHtml(),Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV),null,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else
                        stringBuilder= new SpannableStringBuilder(Html.fromHtml(document.outerHtml()));
                }
                return stringBuilder;
            }

            @Override
            protected void onPostExecute(Spannable o) {
                super.onPostExecute(o);
                spannable=o;
                progressBar.setVisibility(View.GONE);
                syllabusTv.setText(o);
                //spannable=o;
            }
        }.execute(file);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_info:
                if (courseInfoExpand.isExpanded())
                    courseInfoExpand.collapse();
                else
                    courseInfoExpand.expand();
                //return true;
            /*case R.id.menu_add_to_my_course:
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MyCourses myCourses=realm.where(MyCourses.class).findFirst();
                        if(myCourses==null){
                            myCourses=realm.createObject(MyCourses.class);
                        }
                        myCourses.addToMyCourses(course);
                    }
                });
                break;*/
            case R.id.menu_refresh:
                getFromOnline();
                return true;
        }
        return false;
    }

}
