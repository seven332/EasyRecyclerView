/*
 * Copyright 2017 Hippo Seven
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

package com.hippo.easyrecyclerview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.hippo.easyrecyclerview.EasyAdapter;
import com.hippo.easyrecyclerview.EasyRecyclerView;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    EasyRecyclerView recyclerView = (EasyRecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setAdapter(new SimpleAdapter());
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    recyclerView.setOnItemClickListener(new EasyRecyclerView.OnItemClickListener() {
      @Override
      public void onItemClick(EasyRecyclerView parent, RecyclerView.ViewHolder holder) {
        Toast.makeText(MainActivity.this, "click " + holder.toString(), Toast.LENGTH_SHORT).show();
      }
    });
    recyclerView.setOnItemLongClickListener(new EasyRecyclerView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(EasyRecyclerView parent, RecyclerView.ViewHolder holder) {
        Toast.makeText(MainActivity.this, "long click " + holder.toString(), Toast.LENGTH_SHORT).show();
        return true;
      }
    });
  }

  private static class SimpleHolder extends RecyclerView.ViewHolder {

    public TextView text;

    public SimpleHolder(View itemView) {
      super(itemView);
      text = (TextView) itemView;

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(v.getContext(), "click", Toast.LENGTH_SHORT).show();
        }
      });

      itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          Toast.makeText(v.getContext(), "long click", Toast.LENGTH_SHORT).show();
          return true;
        }
      });
    }
  }

  private static class SimpleAdapter extends EasyAdapter<SimpleHolder> {

    @Override
    public SimpleHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
      return new SimpleHolder(new TextView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(SimpleHolder holder, int position) {
      holder.text.setText("Position = " + position);
    }

    @Override
    public int getItemCount() {
      return 10000;
    }
  }
}
