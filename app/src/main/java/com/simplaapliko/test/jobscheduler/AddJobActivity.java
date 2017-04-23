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

            boolean requiresUnmetered = mWiFiConnectivityRadioButton.isChecked();
            boolean requiresAnyConnectivity = mAnyConnectivityRadioButton.isChecked();
            if (requiresUnmetered) {
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            } else if (requiresAnyConnectivity) {
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
        sendOkResult();
    }

    private void sendOkResult() {
        mPreferenceManager.setJobId(mJobId);
        setResult(RESULT_OK);
        finish();
    }
}
