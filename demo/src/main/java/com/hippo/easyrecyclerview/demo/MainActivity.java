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
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hippo.easyrecyclerview.EasyAdapter;
import com.hippo.easyrecyclerview.EasyRecyclerView;

public class MainActivity extends Activity {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final EasyRecyclerView recyclerView = (EasyRecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setAdapter(new SimpleAdapter());
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    recyclerView.setOnItemClickListener(new EasyRecyclerView.OnItemClickListener() {
      @Override
      public void onItemClick(EasyRecyclerView parent, RecyclerView.ViewHolder holder) {
        if (!recyclerView.isInChoiceMode()) {
          recyclerView.intoChoiceMode();
        }
        recyclerView.toggleItemChecked(holder.getAdapterPosition());
        if (recyclerView.getCheckedItemCount() == 0) {
          recyclerView.outOfChoiceMode();
        }
      }
    });

    recyclerView.setOnItemLongClickListener(new EasyRecyclerView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(EasyRecyclerView parent, RecyclerView.ViewHolder holder) {
        Log.d(LOG_TAG, "long click " + holder.toString());
        return true;
      }
    });

    recyclerView.setChoiceModeListener(new EasyRecyclerView.ChoiceModeListener() {

      private ActionMode actionMode;

      @Override
      public void onIntoChoiceMode(EasyRecyclerView view) {
        Log.d(LOG_TAG, "onIntoChoiceMode");
        startActionMode(new ActionMode.Callback() {
          @Override
          public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.d(LOG_TAG, "onCreateActionMode");
            menu.add("CheckAll");
            actionMode = mode;
            return true;
          }

          @Override
          public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
          }

          @Override
          public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if ("CheckAll".equals(item.getTitle())) {
              recyclerView.checkAll();
              return true;
            } else {
              return false;
            }
          }

          @Override
          public void onDestroyActionMode(ActionMode mode) {
            Log.d(LOG_TAG, "onDestroyActionMode");
            actionMode = null;
            if (recyclerView.isInChoiceMode()) {
              recyclerView.outOfChoiceMode();
            }
          }
        });
      }

      @Override
      public void onOutOfChoiceMode(EasyRecyclerView view) {
        Log.d(LOG_TAG, "onOutOfChoiceMode");
        if (actionMode != null) {
          actionMode.finish();
        }
      }

      @Override
      public void onItemCheckedStateChanged(EasyRecyclerView view, int position, long id, boolean checked) {
        Log.d(LOG_TAG, "onItemCheckedStateChanged position=" + position + " id=" + id + " checked=" + checked);
      }

      @Override
      public void onItemsCheckedStateChanged(EasyRecyclerView view) {
        Log.d(LOG_TAG, "onItemsCheckedStateChanged");
      }
    });
  }

  private static class SimpleHolder extends RecyclerView.ViewHolder {

    public TextView text;

    public SimpleHolder(View itemView) {
      super(itemView);
      text = (TextView) itemView;
    }
  }

  private static class SimpleAdapter extends EasyAdapter<SimpleHolder> {

    @Override
    public SimpleHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
      return new SimpleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(SimpleHolder holder, int position) {
      holder.text.setText("Position = " + position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public int getItemCount() {
      return 100;
    }
  }
}
