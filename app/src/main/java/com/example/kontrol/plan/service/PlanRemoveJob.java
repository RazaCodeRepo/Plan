package com.example.kontrol.plan.service;

import android.content.Context;
import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.example.kontrol.plan.activities.MainActivity;
import com.example.kontrol.plan.activities.NewPlanActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by kontrol on 6/14/2018.
 */

public class PlanRemoveJob extends Job {

    public static final String TAG = "job_plan_removal";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPlanDatabaseReference;

    private static Context context;
    public static HashMap<Integer,String> jobIdToPlanKey = new HashMap<>();


    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {

        int jobId = params.getId();
       // HashMap<Integer,String> temp = jobIdToPlanKey;

        context = getContext();

        String groupKey = jobIdToPlanKey.get(jobId);



        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mPlanDatabaseReference = mFirebaseDatabase.getReference().child("plan" ).child(groupKey);
        mPlanDatabaseReference.removeValue();


        MainActivity.childNodesKeys.remove(groupKey);
        ArrayList<String> temp = MainActivity.childNodesKeys;

        return Result.SUCCESS;
    }

    public static void scheduleExactjob() {

        HashMap<Integer,String> temp = jobIdToPlanKey;
        String groupID = NewPlanActivity.groupKeyCheck;
        int duration = NewPlanActivity.duration;
        int jobID = new JobRequest.Builder(PlanRemoveJob.TAG)
                .setExact(TimeUnit.MINUTES.toMillis(duration))
                .setUpdateCurrent(false)
                .build()
                .schedule();

        jobIdToPlanKey.put(jobID,groupID);
    }
}