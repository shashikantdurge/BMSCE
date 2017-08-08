package com.projects.psps.bmsce.firebase;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.projects.psps.bmsce.syllabus.AllCourseFragment;

import java.io.File;
import java.util.Objects;

/**
 * Created by ${SHASHIKANt} on 07-08-2017.
 */

public class SyllabusIService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    final static String TAG="SYLLABUS_SERVICE";
    public SyllabusIService(String name) {
        super(name);
    }
    public SyllabusIService(){
        super("com.projects.psps.bmsce.firebase.syllabusiservice");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    int position;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG,"Syllabus intent service");
        position=intent.getIntExtra("position",-1);
        if(permissionGranted()){
            Log.d(TAG,"Permission Granted");
            getSyllabus(intent.getStringExtra("courseCode"),intent.getFloatExtra("version", (float) 17.1));
        }
        else{
            Log.d(TAG,"Permission Denied");
            Intent broadcastIntent=new Intent(AllCourseFragment.BROADCAST_ACTION);
            broadcastIntent.putExtra("courseCode",intent.getStringExtra("courseCode"));
            broadcastIntent.putExtra(AllCourseFragment.DOWNLOAD_STATUS,"STORAGE PERMISSION DENIED");
            broadcastIntent.putExtra("position",position);
            sendBroadcast(broadcastIntent);
        }

    }

    void getSyllabus(final String courseCode, float version){
        Log.d(TAG,"getSyllabus");
        File versionPath=new File(getFilesDir(),String.valueOf(version));
        File file= new File(versionPath,courseCode+".html");
        FirebaseStorage.getInstance().getReference().child("syllabus/" + courseCode + ".html").getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Intent intent=new Intent(AllCourseFragment.BROADCAST_ACTION);
                intent.putExtra(AllCourseFragment.DOWNLOAD_STATUS,"DOWNLOADED");
                intent.putExtra("courseCode",courseCode);
                intent.putExtra("position",position);
                sendBroadcast(intent);
                Log.d(TAG,"Download SUCCESS");
                //Local file is created by Firebase itself.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if(Objects.equals(exception.getLocalizedMessage(), "Object does not exist at location.")){
                    Log.d(TAG,"Object does not exist at location.");
                    Intent intent=new Intent(AllCourseFragment.BROADCAST_ACTION);
                    intent.putExtra("courseCode",courseCode);
                    intent.putExtra(AllCourseFragment.DOWNLOAD_STATUS,"DOES NOT EXIST");
                    intent.putExtra("position",position);
                    sendBroadcast(intent);

                }
            }
        });
    }


    @TargetApi(23)
    private boolean  permissionGranted(){
        Log.d(TAG,"Checking Permission");
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED;
    }


}
