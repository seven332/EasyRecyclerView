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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ChoiceStateTest {

  @Test
  public void testChecked() {
    ChoiceState state = new ChoiceState();
    assertFalse(state.isChecked(31));
    state.setChecked(31, true);
    assertTrue(state.isChecked(31));
    state.setChecked(31, false);
    assertFalse(state.isChecked(31));
  }

  @Test
  public void testGetCheckedPositions() {
    ChoiceState state = new ChoiceState();
    state.setChecked(34, true);
    state.setChecked(43324, true);
    state.setChecked(63, true);
    state.setChecked(436, true);
    state.setChecked(2, true);
    state.setChecked(2323, true);
    state.setChecked(63, false);
    state.setChecked(17, true);
    assertArrayEquals(new int[] {2, 17, 34, 436, 2323, 43324}, state.getCheckedItemPositions());
  }

  @Test
  public void testOnChange() {
    ChoiceState state = new ChoiceState();
    state.setChecked(4, true);
    state.setChecked(1, true);
    state.setChecked(3, true);
    state.setChecked(2, true);
    assertArrayEquals(new int[] {1, 2, 3, 4}, state.getCheckedItemPositions());
    assertTrue(state.onChanged());
    assertArrayEquals(new int[] {}, state.getCheckedItemPositions());
    assertFalse(state.onChanged());
    assertArrayEquals(new int[] {}, state.getCheckedItemPositions());
  }

  @Test
  public void testOnItemRangeChanged() {
    ChoiceState state;

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertFalse(state.onItemRangeChanged(5, 3));
    assertArrayEquals(new int[] {0, 4, 8, 12}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeChanged(4, 4));
    assertArrayEquals(new int[] {0, 8, 12}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeChanged(5, 4));
    assertArrayEquals(new int[] {0, 4, 12}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeChanged(4, 5));
    assertArrayEquals(new int[] {0, 12}, state.getCheckedItemPositions());
  }

  @Test
  public void testOnItemRangeInserted() {
    ChoiceState state;

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeInserted(4, 3));
    assertArrayEquals(new int[] {0, 7, 11, 15}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeInserted(5, 3));
    assertArrayEquals(new int[] {0, 4, 11, 15}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertFalse(state.onItemRangeInserted(13, 3));
    assertArrayEquals(new int[] {0, 4, 8, 12}, state.getCheckedItemPositions());
  }

  @Test
  public void testOnItemRangeRemoved() {
    ChoiceState state;

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeRemoved(4, 4));
    assertArrayEquals(new int[] {0, 4, 8}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeRemoved(5, 4));
    assertArrayEquals(new int[] {0, 4, 8}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertTrue(state.onItemRangeRemoved(4, 5));
    assertArrayEquals(new int[] {0, 7}, state.getCheckedItemPositions());

    state = new ChoiceState();
    state.setChecked(0, true);
    state.setChecked(4, true);
    state.setChecked(8, true);
    state.setChecked(12, true);
    assertFalse(state.onItemRangeRemoved(13, 5));
    assertArrayEquals(new int[] {0, 4, 8, 12}, state.getCheckedItemPositions());
  }

  private void testOnItemRangeMovedInternal(int[] checked, int[] moved, boolean result, int[] expected) {
    ChoiceState state = new ChoiceState();
    for (int c: checked) {
      state.setChecked(c, true);
    }
    assertEquals(result, state.onItemRangeMoved(moved[0], moved[1]));
    assertArrayEquals(expected, state.getCheckedItemPositions());
  }

  @Test
  public void testOnItemRangeMoved() {
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {5, 7},
        false,
        new int[] {0, 4, 8, 12});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {4, 7},
        true,
        new int[] {0, 7, 8, 12});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {8, 5},
        true,
        new int[] {0, 4, 5, 12});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {4, 8},
        true,
        new int[] {0, 7, 8, 12});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {8, 4},
        true,
        new int[] {0, 4, 5, 12});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {12, 0},
        true,
        new int[] {0, 1, 5, 9});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {0, 15},
        true,
        new int[] {3, 7, 11, 15});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {1, 15},
        true,
        new int[] {0, 3, 7, 11});
    testOnItemRangeMovedInternal(
        new int[] {0, 4, 8, 12},
        new int[] {1, 9},
        true,
        new int[] {0, 3, 7, 12});
  }
}
