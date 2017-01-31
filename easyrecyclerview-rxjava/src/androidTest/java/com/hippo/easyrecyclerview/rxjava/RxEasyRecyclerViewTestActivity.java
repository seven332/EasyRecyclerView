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

package com.hippo.easyrecyclerview.rxjava;

/*
 * Created by Hippo on 1/31/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hippo.easyrecyclerview.EasyAdapter;
import com.hippo.easyrecyclerview.EasyRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RxEasyRecyclerViewTestActivity extends Activity {

  EasyRecyclerView recyclerView;

  List<String> values;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    values = new ArrayList<>(Arrays.asList("One", "Two", "Three"));

    recyclerView = new EasyRecyclerView(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new SimpleAdapter(this, values));

    setContentView(recyclerView);
  }

  public static class SimpleHolder extends RecyclerView.ViewHolder {

    public TextView text;

    public SimpleHolder(View itemView) {
      super(itemView);
      text = (TextView) itemView;
    }
  }

  public static class SimpleAdapter extends EasyAdapter<SimpleHolder> {

    Context context;
    List<String> values;

    public SimpleAdapter(Context context, List<String> values) {
      this.context = context;
      this.values = values;
    }

    @Override
    public SimpleHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
      return new SimpleHolder(new TextView(context));
    }

    @Override
    public void onBindViewHolder(SimpleHolder holder, int position) {
      holder.text.setText(values.get(position));
    }

    @Override
    public int getItemCount() {
      return values.size();
    }
  }

}
