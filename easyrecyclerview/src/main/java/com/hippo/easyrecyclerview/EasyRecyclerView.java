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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Checkable;

/**
 * A {@code EasyRecyclerView} is a {@link RecyclerView}
 * with some features of {@link android.widget.ListView}.
 * <p>
 * Features:
 * <ul>
 * <li>{@code setOnItemClickListener}</li>
 * <li>{@code setOnItemLongClickListener}</li>
 * <li>{@code choice mode}</li>
 * </ul>
 * <p>
 * {@link #setOnItemClickListener(OnItemClickListener)}
 * and {@link #setOnItemLongClickListener(OnItemLongClickListener)}
 * are implemented via {@link View#setOnClickListener(OnClickListener)}
 * and {@link View#setOnLongClickListener(OnLongClickListener)}
 * to child view.
 * <p>
 * No action mode reaction for choice mode.
 * Single or multiple choices, implements it by your own.
 * <p>
 * Choice mode state is saved. When restores state,
 * {@link ChoiceModeListener#onIntoChoiceMode(EasyRecyclerView)}
 * and {@link ChoiceModeListener#onItemCheckedStateChanged(EasyRecyclerView, int, long, boolean)}
 * are called, if the view was in choice mode before.
 * <p>
 * You can call {@code Adapter.notifyXXX()} during choice mode.
 * {@link ChoiceModeListener#onItemsCheckedStateChanged(EasyRecyclerView)}
 * is called if any position of checked item is changed.
 * It's better to call {@link #getCheckedItemPositions()} to update the choice state you saved in it.
 */
public class EasyRecyclerView extends RecyclerView {

  private static final String LOG_TAG = EasyRecyclerView.class.getSimpleName();

  private static final boolean HAS_ACTIVATED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

  private Adapter adapter;

  private boolean inChoiceMode;
  private ChoiceState choiceState;
  private ChoiceObserver choiceObserver;
  private ChoiceModeListener choiceModeListener;

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

  @Override
  public void setAdapter(Adapter adapter) {
    if (adapter != null && !(adapter instanceof EasyAdapter)) {
      throw new IllegalStateException("Only EasyAdapter can be set to EasyRecyclerView");
    }

    if (inChoiceMode) {
      Log.w(LOG_TAG, "Out of ChoiceMode because adapter changed.");
      outOfChoiceMode();
    }

    this.adapter = adapter;

    super.setAdapter(adapter);
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

  /**
   * Register a callback to be invoked when an choice action happened.
   *
   * @param listener The callback that will run
   */
  public void setChoiceModeListener(ChoiceModeListener listener) {
    choiceModeListener = listener;
  }

  /**
   * Starts choice mode.
   * <p>
   * Ignores it, if the {@code EasyRecyclerView} is already in choice mode.
   *
   * @throws IllegalStateException if not adapter attached to the {@code EasyRecyclerView}.
   */
  public void intoChoiceMode() {
    if (!inChoiceMode) {
      if (adapter == null) {
        throw new IllegalStateException("Please set adapter first");
      }

      inChoiceMode = true;

      if (choiceState == null) {
        choiceState = new ChoiceState();
      }

      if (choiceObserver == null) {
        choiceObserver = new ChoiceObserver();
      }
      adapter.registerAdapterDataObserver(choiceObserver);

      if (choiceModeListener != null) {
        choiceModeListener.onIntoChoiceMode(this);
      }
    }
  }

  /**
   * Ends choice mode.
   * <p>
   * Ignore it, if the {@code EasyRecyclerView} isn't in choice mode.
   */
  public void outOfChoiceMode() {
    if (inChoiceMode) {
      inChoiceMode = false;

      choiceState.clear();

      adapter.unregisterAdapterDataObserver(choiceObserver);

      uncheckOnScreenViews();

      if (choiceModeListener != null) {
        choiceModeListener.onOutOfChoiceMode(this);
      }
    }
  }

  /**
   * Returns {@code true} if {@code EasyRecyclerView} is in choice mode.
   *
   * @return {@code true} if {@code EasyRecyclerView} is in choice mode.
   */
  public boolean isInChoiceMode() {
    return inChoiceMode;
  }

  /**
   * Sets the checked state of the specified position.
   *
   * @param position The item whose checked state is to be checked
   * @param value The new checked state for the item
   * @throws IllegalStateException if the {@code EasyRecyclerView} isn't in choice mode,
   *          or position is out of range.
   */
  public void setItemChecked(int position, boolean value) {
    if (!inChoiceMode) {
      throw new IllegalStateException("Must call intoChoiceMode() first");
    }
    int count = adapter.getItemCount();
    if (position < 0 || position >= count) {
      throw new IllegalStateException("Out of range: position = " + position + ", count = " + count);
    }

    // Check old value and new value
    if (choiceState.isChecked(position) == value) {
      return;
    }

    choiceState.setChecked(position, value);

    setViewChecked(position, value);

    if (choiceModeListener != null) {
      long id = adapter.getItemId(position);
      choiceModeListener.onItemCheckedStateChanged(this, position, id, value);
    }
  }

  /**
   * Toggle the checked state of the specified position.
   *
   * @param position The item whose checked state is to be toggled
   * @throws IllegalStateException if the {@code EasyRecyclerView} isn't in choice mode,
   *          or position is out of range.
   */
  public void toggleItemChecked(int position) {
    if (inChoiceMode) {
      setItemChecked(position, !choiceState.isChecked(position));
    } else {
      throw new IllegalStateException("Must call intoChoiceMode() first");
    }
  }

  /**
   * Checks all!
   *
   * @throws IllegalStateException if the {@code EasyRecyclerView} isn't in choice mode
   */
  public void checkAll() {
    if (!inChoiceMode) {
      throw new IllegalStateException("Must call intoChoiceMode() first");
    }

    Adapter adapter = this.adapter;
    for (int i = 0, n = adapter.getItemCount(); i < n; i++) {
      // Skip checked item
      if (choiceState.isChecked(i)) {
        continue;
      }

      choiceState.setChecked(i, true);

      if (choiceModeListener != null) {
        long id = getAdapter().getItemId(i);
        choiceModeListener.onItemCheckedStateChanged(this, i, id, true);
      }
    }

    updateOnScreenViews();
  }

  /**
   * Returns the checked state of the specified position.
   *
   * @param position The item whose checked state to return
   * @return The item's checked state
   * @throws IllegalStateException if the {@code EasyRecyclerView} isn't in choice mode
   */
  public boolean isItemChecked(int position) {
    if (!inChoiceMode) {
      throw new IllegalStateException("Must call intoChoiceMode() first");
    }
    return choiceState.isChecked(position);
  }

  /**
   * Returns the number of items currently selected.
   *
   * @return the number of items currently selected
   * @throws IllegalStateException if the {@code EasyRecyclerView} isn't in choice mode
   */
  public int getCheckedItemCount() {
    if (!inChoiceMode) {
      throw new IllegalStateException("Must call intoChoiceMode() first");
    }
    return choiceState.getCheckedItemCount();
  }

  /**
   * Returns the positions of checked items in the int array.
   *
   * @return the set of checked item position
   * @throws IllegalStateException if the {@code EasyRecyclerView} isn't in choice mode
   */
  public int[] getCheckedItemPositions() {
    if (!inChoiceMode) {
      throw new IllegalStateException("Must call intoChoiceMode() first");
    }
    return choiceState.getCheckedItemPositions();
  }

  private void setViewChecked(int position, boolean checked) {
    ViewHolder holder = findViewHolderForAdapterPosition(position);
    if (holder != null) {
      setViewChecked(holder.itemView, checked);
    }
  }

  private void updateOnScreenViews() {
    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      final int position = getChildAdapterPosition(child);
      if (position >= 0) {
        setViewChecked(child, choiceState.isChecked(position));
      } else {
        Log.e(LOG_TAG, "Can't get adapter position for a child in updateOnScreenViews()");
      }
    }
  }

  private void uncheckOnScreenViews() {
    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      final int position = getChildAdapterPosition(child);
      if (position >= 0) {
        setViewChecked(child, false);
      } else {
        Log.e(LOG_TAG, "Can't get adapter position for a child in updateOnScreenViews()");
      }
    }
  }

  @Override
  public void onChildAttachedToWindow(View child) {
    super.onChildAttachedToWindow(child);

    // Apply check state to child view
    if (choiceState != null) {
      int position = getChildAdapterPosition(child);
      if (position >= 0) {
        setViewChecked(child, choiceState.isChecked(position));
      }
    }
  }

  /**
   * Calls this {@code EasyRecyclerView}'s {@code OnItemClickListener}, if it is defined.
   *
   * @return {@code true} if the view is on screen and
   *          {@code OnItemClickListener} consumed the event,
   *          {@code false} otherwise
   */
  public boolean performItemClick(int position) {
    ViewHolder holder = findViewHolderForAdapterPosition(position);
    if (holder != null) {
      performItemClick(holder);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Calls this {@code EasyRecyclerView}'s {@code OnItemLongClickListener}, if it is defined.
   *
   * @return {@code true} if the view is on screen and
   *          {@code OnItemLongClickListener} consumed the event,
   *          {@code false} otherwise
   */
  public boolean performItemLongClick(int position) {
    ViewHolder holder = findViewHolderForAdapterPosition(position);
    if (holder != null) {
      return performItemLongClick(holder);
    } else {
      return false;
    }
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
   * This saved state class is a Parcelable and should not extend
   * {@link android.view.View.BaseSavedState} nor {@link android.view.AbsSavedState}
   * because its super class AbsSavedState's constructor
   * {@link android.view.AbsSavedState#AbsSavedState(Parcel)} currently passes null
   * as a class loader to read its superstate from Parcelable.
   * This causes {@link android.os.BadParcelableException} when restoring saved states.
   * <p/>
   * The super class "RecyclerView" is a part of the support library,
   * and restoring its saved state requires the class loader that loaded the RecyclerView.
   * It seems that the class loader is not required when restoring from RecyclerView itself,
   * but it is required when restoring from RecyclerView's subclasses.
   */
  static class SavedState implements Parcelable {

    public static final SavedState EMPTY_STATE = new SavedState() {};

    boolean inChoiceMode;
    ChoiceState choiceState;

    // This keeps the parent(RecyclerView)'s state
    Parcelable mSuperState;

    SavedState() {
      mSuperState = null;
    }

    /**
     * Constructor called from {@link #onSaveInstanceState()}
     */
    SavedState(Parcelable superState) {
      mSuperState = superState != EMPTY_STATE ? superState : null;
    }

    /**
     * Constructor called from {@link #CREATOR}
     */
    private SavedState(Parcel in) {
      // Parcel 'in' has its parent(RecyclerView)'s saved state.
      // To restore it, class loader that loaded RecyclerView is required.
      Parcelable superState = in.readParcelable(RecyclerView.class.getClassLoader());
      mSuperState = superState != null ? superState : EMPTY_STATE;
      inChoiceMode = (in.readInt() != 0);
      choiceState = ChoiceState.readFromParcel(in);
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
      out.writeParcelable(mSuperState, flags);
      out.writeInt(inChoiceMode ? 1 : 0);
      ChoiceState.writeToParcel(choiceState, out);
    }

    public Parcelable getSuperState() {
      return mSuperState;
    }

    public static final Parcelable.Creator<SavedState> CREATOR
        = new Parcelable.Creator<SavedState>() {
      @Override
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      @Override
      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
  }

  @Override
  public Parcelable onSaveInstanceState() {
    final SavedState ss = new SavedState(super.onSaveInstanceState());

    ss.inChoiceMode = inChoiceMode;
    ss.choiceState = choiceState;

    return ss;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());

    if (ss.inChoiceMode) {
      intoChoiceMode();

      int[] positions = ss.choiceState.getCheckedItemPositions();
      for (int position: positions) {
        choiceState.setChecked(position, true);
        if (choiceModeListener != null) {
          long id = adapter.getItemId(position);
          choiceModeListener.onItemCheckedStateChanged(this, position, id, true);
        }
      }

      updateOnScreenViews();
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static void setViewChecked(View view, boolean checked) {
    if (view instanceof Checkable) {
      ((Checkable) view).setChecked(checked);
    } else if (HAS_ACTIVATED) {
      view.setActivated(checked);
    }
  }

  private class ChoiceObserver extends RecyclerView.AdapterDataObserver {

    @Override
    public void onChanged() {
      if (inChoiceMode) {
        if (choiceState.onChanged()) {
          updateOnScreenViews();
          if (choiceModeListener != null) {
            choiceModeListener.onItemsCheckedStateChanged(EasyRecyclerView.this);
          }
        }
      }
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      if (itemCount < 1) {
        return;
      }

      if (inChoiceMode) {
        if (choiceState.onItemRangeChanged(positionStart, itemCount)) {
          updateOnScreenViews();
          if (choiceModeListener != null) {
            choiceModeListener.onItemsCheckedStateChanged(EasyRecyclerView.this);
          }
        }
      }
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      if (itemCount < 1) {
        return;
      }

      if (inChoiceMode) {
        if (choiceState.onItemRangeInserted(positionStart, itemCount)) {
          updateOnScreenViews();
          if (choiceModeListener != null) {
            choiceModeListener.onItemsCheckedStateChanged(EasyRecyclerView.this);
          }
        }
      }
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      if (itemCount < 1) {
        return;
      }

      if (inChoiceMode) {
        if (choiceState.onItemRangeRemoved(positionStart, itemCount)) {
          updateOnScreenViews();
          if (choiceModeListener != null) {
            choiceModeListener.onItemsCheckedStateChanged(EasyRecyclerView.this);
          }
        }
      }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      if (itemCount < 1 || fromPosition == toPosition) {
        return;
      }
      if (itemCount != 1) {
        throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
      }

      if (inChoiceMode) {
        if (choiceState.onItemRangeMoved(fromPosition, toPosition)) {
          updateOnScreenViews();
          if (choiceModeListener != null) {
            choiceModeListener.onItemsCheckedStateChanged(EasyRecyclerView.this);
          }
        }
      }
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

  /**
   * Interface definition for a callback to be invoked when an choice action happened.
   */
  public interface ChoiceModeListener {

    /**
     * Callback method to be invoked when action mode starts.
     *
     * @param view the {@code EasyRecyclerView}
     */
    void onIntoChoiceMode(EasyRecyclerView view);

    /**
     * Callback method to be invoked when action mode ends.
     *
     * @param view the {@code EasyRecyclerView}
     */
    void onOutOfChoiceMode(EasyRecyclerView view);

    /**
     * Callback method to be invoked when an item checked state changes.
     *
     * @param view the {@code EasyRecyclerView}
     * @param position the position of the item
     * @param id the id of the view
     * @param checked the checked state of the view
     */
    void onItemCheckedStateChanged(EasyRecyclerView view, int position, long id, boolean checked);

    /**
     * Callback method to be invoked when multiple item checked state changes.
     * <p>
     * It always caused by {@code Adapter.notifyXXX()}.
     * But {@code Adapter.notifyXXX()} may not cause it.
     *
     * @param view the {@code EasyRecyclerView}
     */
    void onItemsCheckedStateChanged(EasyRecyclerView view);
  }
}
