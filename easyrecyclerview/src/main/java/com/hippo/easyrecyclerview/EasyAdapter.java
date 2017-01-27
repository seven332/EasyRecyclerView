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

package com.hippo.easyrecyclerview;

/*
 * Created by Hippo on 1/27/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@code EasyAdapter} is a adapter for {@link EasyRecyclerView}.
 * <p>
 * Implements {@link #onCreateViewHolder2(ViewGroup, int)}
 * instead of {@link #onCreateViewHolder(ViewGroup, int)}.
 * <p>
 * One {@code EasyAdapter} for one {@code EasyRecyclerView} only.
 */
public abstract class EasyAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH>
    implements View.OnClickListener, View.OnLongClickListener {

  private EasyRecyclerView recyclerView;

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);

    if (!(recyclerView instanceof EasyRecyclerView)) {
      throw new IllegalStateException("EasyAdapter can only be attached to EasyRecyclerView");
    }
    if (this.recyclerView != null) {
      throw new IllegalStateException("The EasyAdapter is already attached a EasyRecyclerView");
    }
    this.recyclerView = (EasyRecyclerView) recyclerView;
  }

  @Override
  public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    this.recyclerView = null;
  }

  @Override
  public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
    VH viewHolder = onCreateViewHolder2(parent, viewType);
    View view = viewHolder.itemView;
    view.setOnClickListener(this);
    view.setOnLongClickListener(this);
    // Let EasyRecyclerView handle SoundEffects and HapticFeedback
    view.setSoundEffectsEnabled(false);
    view.setHapticFeedbackEnabled(false);
    return viewHolder;
  }

  /**
   * The same as {@link #onCreateViewHolder(ViewGroup, int)}.
   */
  public abstract VH onCreateViewHolder2(ViewGroup parent, int viewType);

  @Override
  public void onClick(View v) {
    RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
    if (holder != null) {
      recyclerView.performItemClick(holder);
    }
  }

  @Override
  public boolean onLongClick(View v) {
    RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
    if (holder != null) {
      return recyclerView.performItemLongClick(holder);
    } else {
      return false;
    }
  }
}
