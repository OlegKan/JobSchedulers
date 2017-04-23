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

import java.io.File;

public class FileWriter implements Runnable {
    private final FileManager mFileManager;
    private final File mFileToWrite;
    private final String mFileContent;

    public FileWriter(FileManager fileManager, File fileToWrite, String fileContent) {
        mFileManager = fileManager;
        mFileToWrite = fileToWrite;
        mFileContent = fileContent;
    }

    @Override public void run() {
        mFileManager.writeToFile(mFileToWrite, mFileContent);
    }
}
