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

package com.simplaapliko.test.jobscheduler.util;

import com.simplaapliko.test.jobscheduler.App;
import com.simplaapliko.test.jobscheduler.log.LogEntry;
import com.simplaapliko.test.jobscheduler.log.LogRepository;
import com.simplaapliko.test.jobscheduler.log.LogRepositoryImpl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogManager {

    private static LogManager sLogManager;

    private final LogRepository mLogRepository;

    public static LogManager get() {
        if (sLogManager == null) {
            sLogManager = new LogManager();
        }
        return sLogManager;
    }

    public LogManager() {
        mLogRepository = new LogRepositoryImpl(App.getAppContext());
    }

    public synchronized List<LogEntry> getAll() {
        List<LogEntry> list = mLogRepository.getAll();
        Collections.reverse(list);
        return list;
    }

    public void clear() {
        mLogRepository.clear();
    }

    public synchronized void log(String message) {
        log(new LogEntry(new Date(), message));
    }

    public synchronized void log(LogEntry logEntry) {
        mLogRepository.log(logEntry);
    }
}
