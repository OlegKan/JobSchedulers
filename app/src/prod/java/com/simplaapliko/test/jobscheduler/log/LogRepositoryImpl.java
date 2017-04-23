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

import com.simplaapliko.test.jobscheduler.util.FileManager;
import com.simplaapliko.test.jobscheduler.util.JsonSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogRepositoryImpl implements LogRepository {

    private static final String LOG_FILE = "log.txt";

    private final FileManager mFileManager;
    private final JsonSerializer mJsonSerializer;
    private Context mContext;

    public LogRepositoryImpl(Context context) {
        mFileManager = new FileManager();
        mJsonSerializer = new JsonSerializer();
        mContext = context;
    }

    @Override
    public List<LogEntry> getAll() {
        String json = mFileManager.readFileContent(buildFile());
        LogEntry[] array = mJsonSerializer.deserializeToArray(json);

        List<LogEntry> list = new ArrayList<>();
        if (array != null) {
            Collections.addAll(list, array);
        }

        return list;
    }

    @Override
    public void log(LogEntry logEntry) {
        String json = mFileManager.readFileContent(buildFile());
        LogEntry[] array = mJsonSerializer.deserializeToArray(json);
        LogEntry[] newArray;
        if (array != null) {
            newArray = Arrays.copyOf(array, array.length + 1);
        } else {
            newArray = new LogEntry[1];
        }
        newArray[newArray.length - 1] = logEntry;
        String newJson = mJsonSerializer.serialize(newArray);
        mFileManager.writeToFile(buildFile(), newJson);
    }

    @Override
    public void clear() {
        mFileManager.writeToFile(buildFile(), "");
    }

    private File buildFile() {
        return new File(mContext.getFilesDir(), LOG_FILE);
    }
}
