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

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;

/**
 * A {@code EasyRecyclerView} is a {@link RecyclerView}
 * with some features of {@link android.widget.ListView}.
 */
public class EasyRecyclerView extends RecyclerView {

  private OnItemClickListener onItemClickListener;
  private OnItemLongClickListener onItemLongClickListener;

  public EasyRecyclerView(Context context) {
    super(context);
  }

  public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  /**
   * Register a callback to be invoked when an item in this
   * {@code EasyRecyclerView} has been clicked.
   *
   * @param listener The callback that will be invoked.
   */
  public void setOnItemClickListener(OnItemClickListener listener) {
    onItemClickListener = listener;
  }

  /**
   * Register a callback to be invoked when an item in this
   * {@code EasyRecyclerView} has been clicked and held
   *
   * @param listener The callback that will run
   */
  public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    onItemLongClickListener = listener;
  }

  @Override
  public void setAdapter(Adapter adapter) {
    super.setAdapter(adapter);
  }

  void performItemClick(ViewHolder holder) {
    if (onItemClickListener != null) {
      onItemClickListener.onItemClick(this, holder);
      playSoundEffect(SoundEffectConstants.CLICK);
    }
  }

  boolean performItemLongClick(ViewHolder holder) {
    if (onItemLongClickListener != null) {
      boolean handled = onItemLongClickListener.onItemLongClick(this, holder);
      if (handled) {
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
      }
      return handled;
    } else {
      return false;
    }
  }


  /**
   * Interface definition for a callback to be invoked when an item in the
   * {@code EasyRecyclerView} has been clicked.
s   */
  public interface OnItemClickListener {

    /**
     * Callback method to be invoked when an item in this
     * {@code EasyRecyclerView} has been clicked.
     *
     * @param parent the EasyRecyclerView where the click happened
     * @param holder the ViewHolder of the view within the EasyRecyclerView that was clicked
     */
    void onItemClick(EasyRecyclerView parent, RecyclerView.ViewHolder holder);
  }

  /**
   * Interface definition for a callback to be invoked when an item in the
   * {@code EasyRecyclerView} has been clicked and held.
   */
  public interface OnItemLongClickListener {

    /**
     * Callback method to be invoked when an item in this
     * {@code EasyRecyclerView} has been clicked and held.
     *
     * @param parent the EasyRecyclerView where the click happened
     * @param holder the ViewHolder of the view within the EasyRecyclerView that was clicked
     */
    boolean onItemLongClick(EasyRecyclerView parent, RecyclerView.ViewHolder holder);
  }
}
