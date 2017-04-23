/*
 * Copyright (C) 2017 Oleg Kan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplaapliko.test.jobscheduler;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.simplaapliko.test.jobscheduler.service.JobDispatcherService;
import com.simplaapliko.test.jobscheduler.service.JobSchedulerService;
import com.simplaapliko.test.jobscheduler.util.LogManager;
import com.simplaapliko.test.jobscheduler.util.PreferenceManager;

public class AddJobActivity extends AppCompatActivity {

    private PreferenceManager mPreferenceManager;
    private int mJobId;

    private EditText mDelayEditText;
    private EditText mDeadlineEditText;
    private EditText mIntervalEditText;
    private RadioButton mJobSchedulerRadioButton;
    private RadioButton mFirebaseJobDispatcherRadioButton;
    private RadioButton mWiFiConnectivityRadioButton;
    private RadioButton mAnyConnectivityRadioButton;
    private RadioButton mPeriodicRadioButton;
    private RadioButton mOneTimeRadioButton;
    private CheckBox mRequiresChargingCheckBox;
    private CheckBox mRequiresIdleCheckbox;
    private CheckBox mPersistedCheckbox;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, AddJobActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreferenceManager = new PreferenceManager(this);
        mJobId = mPreferenceManager.getJobId() + 1;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        Button scheduleJobButton = (Button) findViewById(R.id.schedule_button);
        mDelayEditText = (EditText) findViewById(R.id.delay_time);
        mDeadlineEditText = (EditText) findViewById(R.id.deadline_time);
        mIntervalEditText = (EditText) findViewById(R.id.interval_time);
        mJobSchedulerRadioButton = (RadioButton) findViewById(R.id.checkbox_job_scheduler);
        mFirebaseJobDispatcherRadioButton = (RadioButton) findViewById(R.id.checkbox_firebase_jobdispatcher);
        mWiFiConnectivityRadioButton = (RadioButton) findViewById(R.id.checkbox_unmetered);
        mAnyConnectivityRadioButton = (RadioButton) findViewById(R.id.checkbox_any);
        mPeriodicRadioButton = (RadioButton) findViewById(R.id.checkbox_periodic);
        mOneTimeRadioButton = (RadioButton) findViewById(R.id.checkbox_one_time);
        mRequiresChargingCheckBox = (CheckBox) findViewById(R.id.checkbox_charging);
        mRequiresIdleCheckbox = (CheckBox) findViewById(R.id.checkbox_idle);
        mPersistedCheckbox = (CheckBox) findViewById(R.id.checkbox_persisted);

        scheduleJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScheduleJobClick();
            }
        });
        mPeriodicRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDelayEditText.setEnabled(!isChecked);
                mDeadlineEditText.setEnabled(!isChecked);
                mIntervalEditText.setEnabled(isChecked);
            }
        });
        mPeriodicRadioButton.setChecked(true);
    }

    private void onScheduleJobClick() {
        boolean jobScheduler = mJobSchedulerRadioButton.isChecked();
        boolean firebaseJobDispatcher = mFirebaseJobDispatcherRadioButton.isChecked();

        if (jobScheduler) {
            scheduleJobSchedulerJob();
        } else if (firebaseJobDispatcher) {
            scheduleFirebaseJobDispatcher();
        }
    }

    @SuppressLint("DefaultLocale")
    private void scheduleJobSchedulerJob() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            LogManager.get().log("schedule JobScheduler job, start. id: " + mJobId);

            ComponentName componentName = new ComponentName(this, JobSchedulerService.class);
            JobInfo.Builder builder = new JobInfo.Builder(mJobId, componentName);

            if (mPeriodicRadioButton.isChecked()) {
                String interval = mIntervalEditText.getText().toString();
                if (!TextUtils.isEmpty(interval)) {
                    // don't set this for periodic jobs
                    builder.setPeriodic(Long.valueOf(interval) * 1000);
                }
            } else if (mOneTimeRadioButton.isChecked()) {
                String delay = mDelayEditText.getText().toString();
                if (!TextUtils.isEmpty(delay)) {
                    // don't set this for periodic jobs
                    builder.setMinimumLatency(Long.valueOf(delay) * 1000);
                }

                String deadline = mDeadlineEditText.getText().toString();
                if (!TextUtils.isEmpty(deadline)) {
                    // don't set this for periodic jobs
                    builder.setOverrideDeadline(Long.valueOf(deadline) * 1000);
                }
            }

            if (mWiFiConnectivityRadioButton.isChecked()) {
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            } else if (mAnyConnectivityRadioButton.isChecked()) {
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            }

            builder.setRequiresDeviceIdle(mRequiresIdleCheckbox.isChecked());
            builder.setRequiresCharging(mRequiresChargingCheckBox.isChecked());
            builder.setPersisted(mPersistedCheckbox.isChecked());

            JobScheduler js = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            js.schedule(builder.build());

            LogManager.get().log("schedule JobScheduler job, end. id: " + mJobId);
        }
        sendOkResult();
    }

    private void scheduleFirebaseJobDispatcher() {
        LogManager.get().log("schedule JobDispatcher job, start. id: " + mJobId);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job.Builder builder = dispatcher.newJobBuilder();

        // the JobService that will be called
        builder.setService(JobDispatcherService.class);

        if (mPeriodicRadioButton.isChecked()) {
            String interval = mIntervalEditText.getText().toString();
            int start = 0;
            if (!TextUtils.isEmpty(interval)) {
                start = Integer.valueOf(interval);
            }
            int end = start;
            start = Math.min(0, start - 5);

            builder.setRecurring(true);

            // start between @start and @end seconds from now
            builder.setTrigger(Trigger.executionWindow(start, end));
        } else if (mOneTimeRadioButton.isChecked()) {
            builder.setRecurring(false);

            String delay = mDelayEditText.getText().toString();
            int start = 0;
            if (!TextUtils.isEmpty(delay)) {
                start = Integer.valueOf(delay);
            }

            String deadline = mDeadlineEditText.getText().toString();
            int end = 0;
            if (!TextUtils.isEmpty(deadline)) {
                end = Integer.valueOf(deadline);
            }

            // start between @start and @end seconds from now
            builder.setTrigger(Trigger.executionWindow(start, end));
        }

        // constraints that need to be satisfied for the job to run
        if (mWiFiConnectivityRadioButton.isChecked()) {
            builder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        } else if (mAnyConnectivityRadioButton.isChecked()) {
            builder.addConstraint(Constraint.ON_ANY_NETWORK);
        }

        if (mRequiresIdleCheckbox.isChecked()) {
            // not applicable
        }

        if (mRequiresChargingCheckBox.isChecked()) {
            builder.addConstraint(Constraint.DEVICE_CHARGING);
        }

        // don't persist past a device reboot
        if (mPersistedCheckbox.isChecked()) {
            builder.setLifetime(Lifetime.FOREVER);
        } else {
            builder.setLifetime(Lifetime.UNTIL_NEXT_BOOT);
        }

        // uniquely identifies the job
        builder.setTag("id_" + mJobId);

        // don't overwrite an existing job with the same tag
        builder.setReplaceCurrent(true);

        // retry with exponential backoff
        builder.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL);

        dispatcher.mustSchedule(builder.build());

        LogManager.get().log("schedule JobDispatcher job, end. id: " + mJobId);

        sendOkResult();
    }

    private void sendOkResult() {
        mPreferenceManager.setJobId(mJobId);
        setResult(RESULT_OK);
        finish();
    }
}
