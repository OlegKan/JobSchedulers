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

package com.simplaapliko.test.jobscheduler.service;

import android.os.Handler;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.simplaapliko.test.jobscheduler.util.LogManager;

public class JobDispatcherService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters params) {
        LogManager.get().log("on start JobDispatcher job, start: " + params.getTag());

        long duration = 5000;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogManager.get().log("on start JobDispatcher job, end: " + params.getTag());

                jobFinished(params, false);
            }
        }, duration);

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogManager.get().log("on stop JobDispatcher job: " + params.getTag());

        return false; // Answers the question: "Should this job be retried?"
    }
}