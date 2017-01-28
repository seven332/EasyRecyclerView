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
 * Created by Hippo on 1/28/2017.
 */

import android.support.v7.widget.RecyclerView;
import java.util.Arrays;

class ChoiceState {

  /** Checked position set **/
  private OrderedIntArray array = new OrderedIntArray();

  /**
   * Returns {@code true} if the view in the position is checked.
   */
  public boolean isChecked(int position) {
    return array.contains(position);
  }

  /**
   * Set checked state for special position.
   */
  public void setChecked(int position, boolean checked) {
    if (checked) {
      array.add(position);
    } else {
      array.remove(position);
    }
  }

  /**
   * Clear check state.
   */
  public void clear() {
    array.clear();
  }

  /**
   * Return the count of checked item.
   */
  public int getCheckedItemCount() {
    return array.size;
  }

  /**
   * Return all position of checked item in array format.
   */
  public int[] getCheckedItemPositions() {
    return array.toArray();
  }

  /**
   * Calls it when {@link RecyclerView.Adapter#notifyDataSetChanged()} called.
   * Returns {@code true} if check state changes.
   */
  public boolean onChanged() {
    if (array.size != 0) {
      array.clear();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Calls it when {@link RecyclerView.Adapter#notifyItemChanged(int)} called.
   * Returns {@code true} if check state changes.
   */
  public boolean onItemRangeChanged(int positionStart, int itemCount) {
    // Get affected position range
    int boundLeft = array.indexOf(positionStart);
    if (boundLeft < 0) {
      boundLeft = ~boundLeft;
    }
    int boundRight = array.indexOf(positionStart + itemCount - 1);
    if (boundRight < 0) {
      boundRight = (~boundRight) - 1;
    }
    if (boundLeft > boundRight || boundLeft >= array.size) {
      // No affected position range
      return false;
    }

    // Remove changed range
    array.removeRange(boundLeft, boundRight - boundLeft + 1);
    return true;
  }

  /**
   * Calls it when {@link RecyclerView.Adapter#notifyItemInserted(int)} called.
   * Returns {@code true} if check state changes.
   */
  public boolean onItemRangeInserted(int positionStart, int itemCount) {
    int index = array.indexOf(positionStart);
    if (index < 0) {
      index = ~index;
    }
    if (index >= array.size) {
      return false;
    }

    array.increaseRange(index, array.size - index, itemCount);
    return true;
  }

  /**
   * Calls it when {@link RecyclerView.Adapter#notifyItemRemoved(int)} called.
   * Returns {@code true} if check state changes.
   */
  public boolean onItemRangeRemoved(int positionStart, int itemCount) {
    boolean result = false;

    // Get affected position range
    int boundLeft = array.indexOf(positionStart);
    if (boundLeft < 0) {
      boundLeft = ~boundLeft;
    }
    int boundRight = array.indexOf(positionStart + itemCount - 1);
    if (boundRight < 0) {
      boundRight = (~boundRight) - 1;
    }
    if (boundLeft >= array.size) {
      // No affected position range
      return false;
    }

    // Remove removed range
    if (boundLeft <= boundRight) {
      result = true;
      array.removeRange(boundLeft, boundRight - boundLeft + 1);
    }

    //decrease following position
    if (boundLeft < array.size) {
      result = true;
      array.increaseRange(boundLeft, array.size - boundLeft, -itemCount);
    }

    return result;
  }

  /**
   * Calls it when {@link RecyclerView.Adapter#notifyItemMoved(int, int)} called.
   * Returns {@code true} if check state changes.
   */
  public boolean onItemRangeMoved(int fromPosition, int toPosition) {
    boolean result = false;
    int index;
    int boundLeft;
    int boundRight;
    int diff;
    if (fromPosition < toPosition) {
      diff = -1;
      boundLeft = array.indexOf(fromPosition);
      if (boundLeft < 0) {
        index = -1;
        boundLeft = ~boundLeft;
      } else {
        index = boundLeft;
        boundLeft += 1;
      }
      boundRight = array.indexOf(toPosition);
      if (boundRight < 0) {
        boundRight = (~boundRight) - 1;
      }

      if (index != -1) {
        result = true;
        array.removeAt(index);
        --boundLeft;
        --boundRight;
      }
      if (boundLeft <= boundRight) {
        result = true;
        array.increaseRange(boundLeft, boundRight - boundLeft + 1, diff);
      }
      if (index != -1) {
        result = true;
        array.add(toPosition);
      }
    } else {
      diff = 1;
      boundLeft = array.indexOf(toPosition);
      if (boundLeft < 0) {
        boundLeft = ~boundLeft;
      }
      boundRight = array.indexOf(fromPosition);
      if (boundRight <= 0) {
        index = -1;
        boundRight = (~boundRight) - 1;
      } else {
        index = boundRight;
        boundRight -= 1;
      }

      if (index != -1) {
        result = true;
        array.removeAt(index);
      }
      if (boundLeft <= boundRight) {
        result = true;
        array.increaseRange(boundLeft, boundRight - boundLeft + 1, diff);
      }
      if (index != -1) {
        result = true;
        array.add(toPosition);
      }
    }

    return result;
  }

  private static class OrderedIntArray {
    private int[] array;
    private int size;

    public OrderedIntArray() {
      this(10);
    }

    public OrderedIntArray(int initialCapacity) {
      initialCapacity = ContainerHelpers.idealIntArraySize(initialCapacity);
      array = new int[initialCapacity];
      size = 0;
    }

    public void clear() {
      size = 0;
    }

    public void add(int value) {
      int index = ContainerHelpers.binarySearch(array, size, value);
      if (index < 0) {
        index = ~index;
        array = ContainerHelpers.insert(array, size, index, value);
        size++;
      }
    }

    public boolean remove(int value) {
      int index = ContainerHelpers.binarySearch(array, size, value);
      if (index >= 0) {
        removeAt(index);
        return true;
      } else {
        return false;
      }
    }

    public void removeAt(int index) {
      System.arraycopy(array, index + 1, array, index, size - (index + 1));
      size--;
    }

    public void removeRange(int index, int count) {
      System.arraycopy(array, index + count, array, index, size - (index + count));
      size -= count;
    }

    public int indexOf(int value) {
      return ContainerHelpers.binarySearch(array, size, value);
    }

    public void increaseRange(int index, int count, int diff) {
      for (int i = index, n = index + count; i < n; i++) {
        array[i] += diff;
      }
    }

    public int[] toArray() {
      return Arrays.copyOfRange(array, 0, size);
    }

    public boolean contains(int value) {
      return ContainerHelpers.binarySearch(array, size, value) >= 0;
    }
  }
}
