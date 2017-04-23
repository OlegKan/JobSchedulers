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

import com.google.gson.Gson;
import com.simplaapliko.test.jobscheduler.log.LogEntry;

public class JsonSerializer {

    private final Gson mGson = new Gson();

    public JsonSerializer() {}

    public String serialize(LogEntry logEntry) {
        return mGson.toJson(logEntry, LogEntry.class);
    }

    public String serialize(LogEntry[] logEntries) {
        return mGson.toJson(logEntries, LogEntry[].class);
    }

    public LogEntry deserialize(String jsonString) {
        return mGson.fromJson(jsonString, LogEntry.class);
    }

    public LogEntry[] deserializeToArray(String jsonString) {
        return mGson.fromJson(jsonString, LogEntry[].class);
    }
}
