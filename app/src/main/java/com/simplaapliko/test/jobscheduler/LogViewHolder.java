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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.simplaapliko.test.jobscheduler.log.LogEntry;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LogViewHolder extends RecyclerView.ViewHolder{

    private TextView mTime;
    private TextView mService;

    public LogViewHolder(View itemView) {
        super(itemView);
        mTime = (TextView) itemView.findViewById(R.id.time);
        mService = (TextView) itemView.findViewById(R.id.service);
    }

    public void bind(LogEntry logEntry) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        mTime.setText(format.format(logEntry.time));
        mService.setText(logEntry.message);
    }
}
