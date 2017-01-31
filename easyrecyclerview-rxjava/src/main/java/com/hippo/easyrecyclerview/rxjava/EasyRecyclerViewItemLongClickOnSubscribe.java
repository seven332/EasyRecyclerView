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

import static rx.android.MainThreadSubscription.verifyMainThread;

import android.support.v7.widget.RecyclerView;
import com.hippo.easyrecyclerview.EasyRecyclerView;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class EasyRecyclerViewItemLongClickOnSubscribe implements Observable.OnSubscribe<RecyclerView.ViewHolder> {

  final EasyRecyclerView view;

  public EasyRecyclerViewItemLongClickOnSubscribe(EasyRecyclerView view) {
    this.view = view;
  }

  @Override
  public void call(final Subscriber<? super RecyclerView.ViewHolder> subscriber) {
    verifyMainThread();

    EasyRecyclerView.OnItemLongClickListener listener = new EasyRecyclerView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(EasyRecyclerView parent, RecyclerView.ViewHolder holder) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(holder);
        }
        return true;
      }
    };

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnItemLongClickListener(null);
      }
    });

    view.setOnItemLongClickListener(listener);
  }
}
