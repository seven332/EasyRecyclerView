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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import com.hippo.easyrecyclerview.EasyRecyclerView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@RunWith(AndroidJUnit4.class)
public class RxEasyRecyclerViewTest {
  @Rule
  public final ActivityTestRule<RxEasyRecyclerViewTestActivity> activityRule =
      new ActivityTestRule<>(RxEasyRecyclerViewTestActivity.class);

  private Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private RxEasyRecyclerViewTestActivity activity;
  private EasyRecyclerView recyclerView;

  @Before
  public void setUp() {
    activity = activityRule.getActivity();
    recyclerView = activity.recyclerView;
  }

  @Test
  public void itemClicks() {
    RecordingObserver<RecyclerView.ViewHolder> o = new RecordingObserver<>();
    Subscription subscription = RxEasyRecyclerView.itemClicks(recyclerView) //
        .subscribeOn(AndroidSchedulers.mainThread()) //
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertTrue(recyclerView.performItemClick(2));
      }
    });
    assertThat(o.takeNext()).isSameAs(recyclerView.findViewHolderForAdapterPosition(2));

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertTrue(recyclerView.performItemClick(0));
      }
    });
    assertThat(o.takeNext()).isSameAs(recyclerView.findViewHolderForAdapterPosition(0));

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertFalse(recyclerView.performItemClick(1));
      }
    });
    o.assertNoMoreEvents();
  }

  @Test
  public void itemLongClicks() {
    RecordingObserver<RecyclerView.ViewHolder> o = new RecordingObserver<>();
    Subscription subscription = RxEasyRecyclerView.itemLongClicks(recyclerView) //
        .subscribeOn(AndroidSchedulers.mainThread()) //
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertTrue(recyclerView.performItemLongClick(2));
      }
    });
    assertThat(o.takeNext()).isSameAs(recyclerView.findViewHolderForAdapterPosition(2));

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertTrue(recyclerView.performItemLongClick(0));
      }
    });
    assertThat(o.takeNext()).isSameAs(recyclerView.findViewHolderForAdapterPosition(0));

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertFalse(recyclerView.performItemLongClick(1));
      }
    });
    o.assertNoMoreEvents();
  }
}
