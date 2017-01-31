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

import static com.hippo.yorozuya.precondition.Preconditions.checkNotNull;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import com.hippo.easyrecyclerview.EasyRecyclerView;
import rx.Observable;

public class RxEasyRecyclerView {

  /**
   * Create an observable of the ViewHolder of item clicks for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult
  @NonNull
  public static Observable<RecyclerView.ViewHolder> itemClicks(@NonNull EasyRecyclerView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new EasyRecyclerViewItemClickOnSubscribe(view));
  }

  /**
   * Create an observable of the ViewHolder of item long-clicks for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static Observable<RecyclerView.ViewHolder> itemLongClicks(@NonNull EasyRecyclerView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new EasyRecyclerViewItemLongClickOnSubscribe(view));
  }
}
