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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplaapliko.test.jobscheduler.log.LogEntry;

import java.util.Collections;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

    private List<LogEntry> mData;

    public LogAdapter() {
        mData = Collections.emptyList();
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_list_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        LogEntry log = mData.get(position);
        holder.bind(log);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<LogEntry> data) {
        this.mData = data;
        notifyDataSetChanged();
    }
}
