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

package com.simplaapliko.test.jobscheduler.log;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogRepositoryImpl implements LogRepository {

    public LogRepositoryImpl(Context context) {
    }

    @Override
    public List<LogEntry> getAll() {
        List<LogEntry> list = new ArrayList<>();
        list.add(new LogEntry(new Date(), "Service 1"));
        list.add(new LogEntry(new Date(), "Service 2"));
        list.add(new LogEntry(new Date(), "Service 3"));

        return list;
    }

    @Override
    public void log(LogEntry logEntry) {

    }

    @Override
    public void clear() {

    }
}
