package com.example.kontrol.plan.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by kontrol on 6/14/2018.
 */

public class PlanJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch(tag) {
            case PlanRemoveJob.TAG:
                return new PlanRemoveJob();

            default:
                return null;
        }
    }
}
