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
 * Created by Hippo on 2/6/2017.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Show divider between item, just like
 * {@link android.widget.ListView#setDivider(android.graphics.drawable.Drawable)}
 * <p>
 * Only work for {@link android.support.v7.widget.LinearLayoutManager}.
 */
public class LinearDividerItemDecoration extends RecyclerView.ItemDecoration {

  public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
  public static final int VERTICAL = LinearLayoutManager.VERTICAL;

  private boolean showFirstDivider = false;
  private boolean showLastDivider = false;

  private final Rect rect;
  private final Paint paint;

  private int orientation;
  private int thickness;
  private int paddingStart = 0;
  private int paddingEnd = 0;

  private boolean overlap = false;

  private ShowDividerHelper showDividerHelper;

  public LinearDividerItemDecoration(int orientation, int color, int thickness) {
    rect = new Rect();
    paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    setOrientation(orientation);
    setColor(color);
    setThickness(thickness);
  }

  /**
   * Let {@code ShowDividerHelper} decide whether divider.
   */
  public void setShowDividerHelper(ShowDividerHelper helper) {
    showDividerHelper = helper;
  }

  /**
   * Orientation of the {@link android.support.v7.widget.LinearLayoutManager}.
   */
  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("invalid orientation");
    }
    this.orientation = orientation;
  }

  /**
   * Color of dividers.
   */
  public void setColor(int color) {
    paint.setColor(color);
  }

  /**
   * Thickness of dividers.
   */
  public void setThickness(int thickness) {
    this.thickness = thickness;
  }

  /**
   * Whether draw divider before the first item.
   */
  public void setShowFirstDivider(boolean showFirstDivider) {
    this.showFirstDivider = showFirstDivider;
  }

  /**
   * Whether draw divider after the last item.
   */
  public void setShowLastDivider(boolean showLastDivider) {
    this.showLastDivider = showLastDivider;
  }

  /**
   * Padding of divider.
   */
  public void setPadding(int padding) {
    setPaddingStart(padding);
    setPaddingEnd(padding);
  }

  /**
   * Left padding for {@link #VERTICAL}.
   * Top padding for {@link #HORIZONTAL}.
   * Supports RTL.
   */
  public void setPaddingStart(int paddingStart) {
    this.paddingStart = paddingStart;
  }

  /**
   * Right padding for {@link #VERTICAL}.
   * Bottom padding for {@link #HORIZONTAL}.
   * Supports RTL.
   */
  public void setPaddingEnd(int paddingEnd) {
    this.paddingEnd = paddingEnd;
  }

  /**
   * Whether draw divider over views.
   */
  public void setOverlap(boolean overlap) {
    this.overlap = overlap;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view,
      RecyclerView parent, RecyclerView.State state) {
    if (parent.getAdapter() == null) {
      // Can't get view position, return empty rect
      outRect.set(0, 0, 0, 0);
      return;
    }

    if (overlap) {
      // Overlap, return empty rect
      outRect.set(0, 0, 0, 0);
      return;
    }

    final int position = parent.getChildLayoutPosition(view);
    final int itemCount = parent.getAdapter().getItemCount();

    outRect.set(0, 0, 0, 0);
    if (showDividerHelper != null) {
      if (orientation == VERTICAL) {
        if (position == 0 && showDividerHelper.showDivider(0)) {
          outRect.top = thickness;
        }
        if (showDividerHelper.showDivider(position + 1)) {
          outRect.bottom = thickness;
        }
      } else {
        if (position == 0 && showDividerHelper.showDivider(0)) {
          outRect.left = thickness;
        }
        if (showDividerHelper.showDivider(position + 1)) {
          outRect.right = thickness;
        }
      }
    } else {
      if (orientation == VERTICAL) {
        if (position == 0 && showFirstDivider) {
          outRect.top = thickness;
        }
        if ((position != itemCount - 1) || showLastDivider) {
          outRect.bottom = thickness;
        }
      } else {
        if (position == 0 && showFirstDivider) {
          outRect.left = thickness;
        }
        if ((position != itemCount - 1) || showLastDivider) {
          outRect.right = thickness;
        }
      }
    }
  }

  @Override
  public void onDrawOver(Canvas c, RecyclerView parent,
      RecyclerView.State state) {
    RecyclerView.Adapter adapter = parent.getAdapter();
    if (adapter == null) {
      return;
    }

    int itemCount = adapter.getItemCount();

    if (orientation == VERTICAL) {
      final boolean isRtl =  ViewCompat.getLayoutDirection(parent) ==  ViewCompat.LAYOUT_DIRECTION_RTL;
      int paddingLeft;
      int paddingRight;
      if (isRtl) {
        paddingLeft = paddingEnd;
        paddingRight = paddingStart;
      } else {
        paddingLeft = paddingStart;
        paddingRight = paddingEnd;
      }

      final int left = parent.getPaddingLeft() + paddingLeft;
      final int right = parent.getWidth() - parent.getPaddingRight() - paddingRight;
      final int childCount = parent.getChildCount();

      for (int i = 0; i < childCount; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int position = parent.getChildLayoutPosition(child);

        boolean show;
        if (showDividerHelper != null) {
          show = showDividerHelper.showDivider(position + 1);
        } else {
          show = (position != itemCount - 1) || showLastDivider;
        }
        if (show) {
          int top = child.getBottom() + lp.bottomMargin;
          if (overlap) {
            top -= thickness;
          }
          final int bottom = top + thickness;
          rect.set(left, top, right, bottom);
          c.drawRect(rect, paint);
        }

        if (position == 0) {
          if (showDividerHelper != null) {
            show = showDividerHelper.showDivider(0);
          } else {
            show = showFirstDivider;
          }
          if (show) {
            int bottom = child.getTop() + lp.topMargin;
            if (overlap) {
              bottom += thickness;
            }
            final int top = bottom - thickness;
            rect.set(left, top, right, bottom);
            c.drawRect(rect, paint);
          }
        }
      }
    } else {
      final int top = parent.getPaddingTop() + paddingStart;
      final int bottom = parent.getHeight() - parent.getPaddingBottom() - paddingEnd;
      final int childCount = parent.getChildCount();

      for (int i = 0; i < childCount; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int position = parent.getChildLayoutPosition(child);

        boolean show;
        if (showDividerHelper != null) {
          show = showDividerHelper.showDivider(position + 1);
        } else {
          show = (position != itemCount - 1) || showLastDivider;
        }
        if (show) {
          int left = child.getRight() + lp.rightMargin;
          if (overlap) {
            left -= thickness;
          }
          final int right = left + thickness;
          rect.set(left, top, right, bottom);
          c.drawRect(rect, paint);
        }

        if (position == 0) {
          if (showDividerHelper != null) {
            show = showDividerHelper.showDivider(0);
          } else {
            show = showFirstDivider;
          }
          if (show) {
            int right = child.getLeft() + lp.leftMargin;
            if (overlap) {
              right += thickness;
            }
            final int left = right - thickness;
            rect.set(left, top, right, bottom);
            c.drawRect(rect, paint);
          }
        }
      }
    }
  }

  /**
   * Whether draw divider.
   */
  public interface ShowDividerHelper {

    /**
     * Whether draw divider for specialized index.
     * <p>
     * The divider before first item is index 0.
     * The divider before first item is index count.
     */
    boolean showDivider(int index);
  }
}
