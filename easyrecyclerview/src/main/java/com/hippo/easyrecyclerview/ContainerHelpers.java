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

class ContainerHelpers {

  public static int idealIntArraySize(int need) {
    return idealByteArraySize(need * 4) / 4;
  }

  public static int idealByteArraySize(int need) {
    for (int i = 4; i < 32; i++)
      if (need <= (1 << i) - 12)
        return (1 << i) - 12;

    return need;
  }

  public static int binarySearch(int[] array, int size, int value) {
    int lo = 0;
    int hi = size - 1;

    while (lo <= hi) {
      final int mid = (lo + hi) >>> 1;
      final int midVal = array[mid];

      if (midVal < value) {
        lo = mid + 1;
      } else if (midVal > value) {
        hi = mid - 1;
      } else {
        return mid;  // value found
      }
    }
    return ~lo;  // value not present
  }

  public static int[] insert(int[] array, int currentSize, int index, int element) {
    if (currentSize + 1 <= array.length) {
      System.arraycopy(array, index, array, index + 1, currentSize - index);
      array[index] = element;
      return array;
    }

    int[] newArray = new int[idealIntArraySize(growSize(currentSize))];
    System.arraycopy(array, 0, newArray, 0, index);
    newArray[index] = element;
    System.arraycopy(array, index, newArray, index + 1, array.length - index);
    return newArray;
  }

  public static int growSize(int currentSize) {
    return currentSize <= 4 ? 8 : currentSize * 2;
  }
}
