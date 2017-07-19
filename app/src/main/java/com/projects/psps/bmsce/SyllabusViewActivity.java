package com.projects.psps.bmsce;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
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
import com.projects.psps.bmsce.firebase.FbCourse;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class SyllabusViewActivity extends AppCompatActivity {

    final static  String TAG="SYLLABUS_MAIN_ACTIVITY";
    TextView syllabusTv;
    Spannable spannable;
    ProgressBar progressBar;
    File file;
    ExpandableLayout courseInfoExpand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_main);
        FbCourse course= (FbCourse) getIntent().getSerializableExtra("course");
        Log.d(TAG,course.getCourseName());
        syllabusTv=(TextView)findViewById(R.id.tv_syllabus);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        fillInfo(course);
        courseInfoExpand=(ExpandableLayout)findViewById(R.id.expand_course_info);
        file= new File(getFilesDir(),course.getCourseCode()+".html");
        ActionBar toolbar=getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(course.getShortName());
        }
        if(file.exists())
            htmlFileToString(file);
        else{
            FirebaseStorage.getInstance().getReference().child("syllabus/"+course.getCourseCode()+".html").getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    htmlFileToString(file);

                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG,"Failed to download");
                    // Handle any errors
                }
            });
        }

    }

    void fillInfo(FbCourse course) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_course_online,menu);
        menu.findItem(R.id.menu_info).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(courseInfoExpand.isExpanded())
                    courseInfoExpand.collapse();
                else
                    courseInfoExpand.expand();
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
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


}