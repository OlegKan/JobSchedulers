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

import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.simplaapliko.test.jobscheduler.util.LogManager;

public class MainActivity extends AppCompatActivity {

    private LogAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LogManager mLogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLogManager = LogManager.get();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onAddJobClick();
                return true;
            case R.id.action_cancel_all_jobs:
                onCancelAllJobsClick();
                return true;
            case R.id.action_clear:
                onClearLogClick();
                return true;
            case R.id.action_refresh:
                loadLog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        loadLog();
    }

    private void setupRecyclerView() {
        mAdapter = new LogAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        loadLog();
    }

    private void loadLog() {
        mAdapter.setData(mLogManager.getAll());
    }

    private void onAddJobClick() {
        Intent intent = AddJobActivity.getStartIntent(this);
        startActivityForResult(intent, 1);
    }

    private void onCancelAllJobsClick() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler js = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            js.cancelAll();
        }
    }

    private void onClearLogClick() {
        mLogManager.clear();
        loadLog();
    }
}
