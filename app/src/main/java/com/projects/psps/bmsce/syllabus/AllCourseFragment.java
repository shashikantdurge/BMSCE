package com.projects.psps.bmsce.syllabus;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.psps.bmsce.R;
import com.projects.psps.bmsce.firebase.SyllabusIService;
import com.projects.psps.bmsce.realm.BranchSemCourses;
import com.projects.psps.bmsce.realm.Course;
import com.projects.psps.bmsce.realm.MyCourses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;

/*
  Created by vasan on 22-07-2017.
 */

public class AllCourseFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final String DOWNLOAD_STATUS="download.status";
    public static final String COURSE_CODE="courseCode";
    public static final String BROADCAST_ACTION="com.projects.psps.bmsce.syllabus.downloadbroadcast";
    private Spinner branchSpn;
    private Spinner semSpn;
    private RecyclerView respectiveCourseListRv;
    private String lastSelectedBranch = "--";
    private ArrayList<String> semesters;
    private SpinnerAdapter spinnerAdapter;
    private RealmList<Course> courseRealmList;
    private BranchSemCourses branchSemCourses;
    private final static String TAG = "ALL_COURSES";
    private ProgressBar progressBar;
    DownloadBroadcast downloadBroadcast;
    RealmAllCourseAdapter courseAdapter;


    public AllCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_course, container, false);
        branchSpn = (Spinner) rootView.findViewById(R.id.spn_branch);
        semSpn = (Spinner) rootView.findViewById(R.id.spn_sem);
        respectiveCourseListRv = (RecyclerView) rootView.findViewById(R.id.rv_respective_course);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        respectiveCourseListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        respectiveCourseListRv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        courseAdapter = new RealmAllCourseAdapter(null, false);
        StickyHeaderDecoration decoration = new StickyHeaderDecoration(courseAdapter);
        respectiveCourseListRv.addItemDecoration(decoration, 1);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        semesters = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.semesters_for_majority)));
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, semesters);
        semSpn.setAdapter(spinnerAdapter);
        semSpn.setOnItemSelectedListener(this);
        branchSpn.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_branch:
                String branch = String.valueOf(branchSpn.getSelectedItem()).substring(0, 2);
                String sem = String.valueOf(semSpn.getSelectedItem()).substring(0, 1);
                if (branch.equals("AT") && !lastSelectedBranch.equals("AT")) {        //Add 9th and 10th semesters to the list
                    semesters.add("9th sem");
                    semesters.add("Xth sem");
                    spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, semesters);
                    semSpn.setAdapter(spinnerAdapter);
                    if (!sem.equals("-")) {
                        semSpn.setSelection(Integer.parseInt(sem));
                    }
                } else if (!branch.equals("AT") && lastSelectedBranch.equals("AT")) {           //Remove 9th and 10th semesters from list
                    semesters.remove(10);
                    semesters.remove(9);
                    spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, semesters);
                    semSpn.setAdapter(spinnerAdapter);
                    if (!sem.equals("9") && !sem.equals("X") && !sem.equals("-")) {
                        semSpn.setSelection(Integer.parseInt(sem));
                    }
                }
                lastSelectedBranch = branch;
                if (Objects.equals(branch, "--") || Objects.equals(sem, "-")) {
                    respectiveCourseListRv.setAdapter(null);
                    return;
                }
                loadCourses(branch + sem);

                break;
            case R.id.spn_sem:
                String branch1 = String.valueOf(branchSpn.getSelectedItem()).substring(0, 2);
                String sem1 = String.valueOf(semSpn.getSelectedItem()).substring(0, 1);
                if (Objects.equals(branch1, "--") || Objects.equals(sem1, "-")) {
                    respectiveCourseListRv.setAdapter(null);
                    return;
                }
                //syllabusReference.child(branch1+sem1).addListenerForSingleValueEvent(courseReader);
                loadCourses(branch1 + sem1);
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadCourses(String branchSem) {

        Log.d(TAG, "progressbar " + progressBar.isShown());

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        //Check for offline . if not present get it from online and show that its from offline and might have changed.
        branchSemCourses = Realm.getDefaultInstance().where(BranchSemCourses.class).equalTo("branchSem", branchSem).findFirst();
        //TODO : if the courses were some days(1 week) old then only load from online
        if (branchSemCourses == null || isConnected  ) {
            progressBar.setVisibility(View.VISIBLE);
            //Load from the cloud
            Log.d(TAG, "loadCourses , ONLINE ");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/branch_sem_courses/" + branchSem);
            Log.d(TAG, "Database Reference" + databaseReference.toString());
            databaseReference.addListenerForSingleValueEvent(courseReader);
        } else {
            setAdapter();
        }

    }

    void setAdapter(){
        courseAdapter = new RealmAllCourseAdapter(branchSemCourses.getCourses().sort("courseType"), false);
        progressBar.setVisibility(View.GONE);
        Log.d(TAG, "Realm course " + branchSemCourses.getBranchSem());
        respectiveCourseListRv.setAdapter(courseAdapter);

    }

    private final ValueEventListener courseReader = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            Realm realm = Realm.getDefaultInstance();
            courseRealmList = new RealmList<>();
            //BranchSemCourses branchSemCourses=new BranchSemCourses(dataSnapshot.getKey());
            if (!dataSnapshot.exists()) {
                NotFoundDailog notFoundDailog = NotFoundDailog.newInstance(String.valueOf(branchSpn.getSelectedItem()), String.valueOf(semSpn.getSelectedItem()));
                notFoundDailog.show(getChildFragmentManager(), "NOT_FOUND");
                progressBar.setVisibility(View.INVISIBLE);
                respectiveCourseListRv.setAdapter(null);
                return;

            }
            for (final DataSnapshot courseSnapShot : dataSnapshot.child("core_lab_mandatory").getChildren()) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d("REALM", "COURSE CREATING");
                        Course course = new Course(courseSnapShot.getKey().trim());
                        course.createOrUpdate(courseSnapShot, Course.CREATE_WITHOUT_PRIMARY, 0);
                        course = realm.copyToRealmOrUpdate(course);
                        courseRealmList.add(course);
                    }
                });
            }
            if (dataSnapshot.hasChild("electives")) {
                for (final DataSnapshot electivesSnapshot : dataSnapshot.child("electives").getChildren()) {
                    for (final DataSnapshot courseSnapshot : electivesSnapshot.getChildren()) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Log.d("REALM", "COURSE CREATING");
                                Course course = new Course(courseSnapshot.getKey());
                                course.createOrUpdate(courseSnapshot, Course.CREATE_WITHOUT_PRIMARY, Integer.parseInt(electivesSnapshot.getKey()));
                                course = realm.copyToRealmOrUpdate(course);
                                courseRealmList.add(course);
                            }
                        });
                    }
                }
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    branchSemCourses = new BranchSemCourses();
                    branchSemCourses.setBranchSem(dataSnapshot.getKey());
                    branchSemCourses.setCourses(courseRealmList);
                    branchSemCourses = realm.copyToRealmOrUpdate(branchSemCourses);
                }
            });
            setAdapter();


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getContext(), "Some problem while loading.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onPause() {
        getContext().unregisterReceiver(downloadBroadcast);
        super.onPause();
    }

    @Override
    public void onResume() {
        IntentFilter filter=new IntentFilter(AllCourseFragment.BROADCAST_ACTION);
        downloadBroadcast=new DownloadBroadcast();
        getContext().registerReceiver(downloadBroadcast,filter);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        try {
            courseAdapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
            Log.d(TAG, "onStart" + e.getMessage());
        }

    }

    public class DownloadBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            //Toast.makeText(context, intent.getStringExtra(DOWNLOAD_STATUS), Toast.LENGTH_SHORT).show();
            int i;
            final Course course=Realm.getDefaultInstance().where(Course.class).equalTo("courseCode",intent.getStringExtra(COURSE_CODE)).findFirst();
            switch (intent.getStringExtra(DOWNLOAD_STATUS)){
                case "DOWNLOADED":
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
                    Toast.makeText(context,"Downloaded\n"+ course.getCourseName(), Toast.LENGTH_SHORT).show();

                    break;
                case "STORAGE PERMISSION DENIED":
                    Toast.makeText(context, "Please enable the Storage permission and try again.", Toast.LENGTH_SHORT).show();
                    break;
                case "DOES NOT EXIST":
                    Toast.makeText(context,"Not Found"+ course.getCourseName(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            if((i=intent.getIntExtra("position",-1))!=-1){
                courseAdapter.notifyItemChanged(i);
            }

        }
    }


}
class RealmAllCourseAdapter extends RealmRecyclerViewAdapter<Course,RealmAllCourseAdapter.MyViewHolder> implements CourseHeaderAdapter<RealmAllCourseAdapter.HeaderHolder> {
        private int n;
        private static int[] courseTypeCount;
        private RealmList<Course> myCoursesList;
        RealmAllCourseAdapter(@Nullable OrderedRealmCollection<Course> data, boolean autoUpdate) {
            super(data, autoUpdate);
            if(data==null){
                Log.d("COURSE_ADAPTER "," Data is null!!!");
                return;
            }
            courseTypeCount=new int[data.where().distinct("courseType").size()];
            n=courseTypeCount.length;
            if (n == 0) {
                Log.d("NO COURSES FOUND"," RETURNING!!!");
                return;
            }
            courseTypeCount[0]=(int) data.where().equalTo("courseType",0).count();
            for(int i=1;i<n;i++) {
                courseTypeCount[i]=(int) data.where().equalTo("courseType",i).count()+courseTypeCount[i-1];
            }
            myCoursesList=Realm.getDefaultInstance().where(MyCourses.class).findFirst().getCourses();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_course, parent, false);
            return new MyViewHolder(v);
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Course course = getItem(position);
            try {
                holder.courseName.setText(course.getCourseName());
                holder.courseCode.setText(course.getCourseCode());
                holder.totalCredits.setText(String.format(Locale.ENGLISH, "Credits %d", course.getTotalCredits()));
                if(myCoursesList.contains(course)){
                    holder.downloadImgBtn.setBackgroundResource(R.drawable.anim_check_circle);
                    holder.downloadImgBtn.setClickable(false);
                    AnimationDrawable animationDrawable=(AnimationDrawable)holder.downloadImgBtn.getBackground();
                    animationDrawable.start();
                }else{
                    holder.downloadImgBtn.setBackgroundResource(R.drawable.ic_download_3);
                    holder.downloadImgBtn.setClickable(true);
                }

            } catch (NullPointerException e) {
                Log.e("Error", e.getMessage());
            }

        }


        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView courseName;
            final TextView courseCode;
            final TextView totalCredits;
            final ImageButton downloadImgBtn;

            MyViewHolder(View view) {
                super(view);
                courseName = (TextView) view.findViewById(R.id.tv_course_name);
                courseCode = (TextView) view.findViewById(R.id.tv_course_code);
                totalCredits = (TextView) view.findViewById(R.id.tv_total_credtis);
                downloadImgBtn=(ImageButton) view.findViewById(R.id.downloadIb);
                view.setOnClickListener(this);
                downloadImgBtn.setOnClickListener(this);
            }

            @Override
            public void onClick(final View v) {
                if(v.getId()==R.id.downloadIb){
                    final Course course=getItem(getAdapterPosition());
                    try{
                        if(v.isClickable()){

                            Intent intent=new Intent(v.getContext(), SyllabusIService.class);
                            intent.putExtra("position",getAdapterPosition());
                            intent.putExtra("courseCode",course.getCourseCode());
                            v.getContext().startService(intent);
                        }
                    }catch (NullPointerException e){
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

                        Log.d("COURSE_ADAPTER"," Your first course is inserted");
                    }finally {
                        v.setClickable(false);
                        v.setBackgroundResource(R.drawable.anim_download);
                        AnimationDrawable animationDrawable=(AnimationDrawable)v.getBackground();
                        animationDrawable.start();
                    }

                   // notifyDataSetChanged();
                }
                else{
                    @SuppressWarnings("ConstantConditions") String courseCode=getData().get(getAdapterPosition()).getCourseCode();
                    Intent intent=new Intent(v.getContext(),SyllabusViewActivity.class);
                    if(Realm.getDefaultInstance().where(MyCourses.class).equalTo("courses.courseCode",courseCode).findFirst()!=null){
                        intent.putExtra(SyllabusViewActivity.IS_COURSE_ONLINE,false);
                    }
                    else {
                        intent.putExtra(SyllabusViewActivity.IS_COURSE_ONLINE,true);
                    }
                    intent.putExtra(SyllabusViewActivity.COURSE_CODE,courseCode);
                    v.getContext().startActivity(intent);
                }
            }
        }

        @Override
        public long getHeaderId(int position) {
            Log.d("Header Position ",String.valueOf(n));
            for(int i=0;i<n;i++){
                if(position>courseTypeCount[i])
                    return position/courseTypeCount[i]+1;
            }
            return  position/(courseTypeCount[0])+1;
        }

        @Override
        public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_test, parent, false);
            return new HeaderHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
            int head= (int) (getHeaderId(position)-1);
            switch (head){
                case 0:
                    viewholder.header.setText("Core/Lab/Mandatory");
                    break;
                default:
                    viewholder.header.setText("Elective "+head);
            }
        }
        class HeaderHolder extends RecyclerView.ViewHolder {
            final TextView header;
            HeaderHolder(View itemView) {
                super(itemView);
                header = (TextView) itemView;
            }
        }
}

