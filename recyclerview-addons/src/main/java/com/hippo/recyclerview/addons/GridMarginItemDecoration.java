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

package com.hippo.recyclerview.addons;

/*
 * Created by Hippo on 2/20/2017.
 */

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * {@code GridMarginItemDecoration} show equal margin between items and bounds.
 * <p>
 * Only works for {@link GridLayoutManager}, {@link GridLayoutManager#VERTICAL},
 * not reverseLayout and default {@link android.support.v7.widget.GridLayoutManager.DefaultSpanSizeLookup}.
 */
public class GridMarginItemDecoration extends RecyclerView.ItemDecoration {

  private int margin;

  public GridMarginItemDecoration(int margin) {
    this.margin = margin;
  }

  public void setMargin(int margin) {
    this.margin = margin;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    if (!(layoutManager instanceof GridLayoutManager)) {
      outRect.set(0, 0, 0, 0);
      return;
    }

    RecyclerView.Adapter adapter = parent.getAdapter();
    if (adapter == null) {
      outRect.set(0, 0, 0, 0);
      return;
    }

    final int position = parent.getChildLayoutPosition(view);
    if (position == -1) {
      outRect.set(0, 0, 0, 0);
      return;
    }

    GridLayoutManager glm = (GridLayoutManager) layoutManager;
    int size = adapter.getItemCount();
    int span = glm.getSpanCount();
    int spanIndex = position % span;
    int spanGroup = position / span;

    if (spanIndex == 0) {
      outRect.left = margin;
    } else {
      outRect.left = (margin + 1) / 2;
    }
    if (spanIndex == span - 1) {
      outRect.right = margin;
    } else {
      outRect.right = margin / 2;
    }
    if (spanGroup == 0) {
      outRect.top = margin;
    } else {
      outRect.top = (margin + 1) / 2;
    }
    if (spanGroup == (size - 1) / span) {
      outRect.bottom = margin;
    } else {
      outRect.bottom = margin / 2;
    }
  }
}
