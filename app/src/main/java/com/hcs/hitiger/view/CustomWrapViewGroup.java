package com.hcs.hitiger.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hot cocoa on 28/04/16.
 */
public class CustomWrapViewGroup extends ViewGroup {
    int deviceWidth;

    public CustomWrapViewGroup(Context context) {
        this(context, null, 0);
    }

    public CustomWrapViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomWrapViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        deviceWidth = metrics.widthPixels;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;

        List<View> childViewList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                return;


            LayoutParams childLayoutParams = (LayoutParams) child.getLayoutParams();

            curWidth = child.getMeasuredWidth() + childLayoutParams.leftMargin + childLayoutParams.rightMargin;
            curHeight = child.getMeasuredHeight() + childLayoutParams.topMargin + childLayoutParams.bottomMargin;

            //wrap is reach to the end and center allign
            if (curLeft + curWidth >= childRight) {
                int moveForAligningCenter = (childRight - curLeft) / 2;
                int left = childLeft + moveForAligningCenter;
                for (View view : childViewList) {
                    LayoutParams childLayoutParams1 = (LayoutParams) child.getLayoutParams();
                    left += childLayoutParams1.leftMargin;
                    view.layout(left, curTop + childLayoutParams1.topMargin, left + view.getMeasuredWidth(), curTop + childLayoutParams1.topMargin + view.getMeasuredHeight());
                    left += view.getMeasuredWidth() + childLayoutParams1.rightMargin;
                }
                childViewList.clear();

                curLeft = childLeft;
                curTop += maxHeight;
                maxHeight = 0;
            }
            childViewList.add(child);

            if (maxHeight < curHeight) {
                maxHeight = curHeight;
            }
            curLeft += curWidth;
        }

        int moveForAligningCenter = (childRight - curLeft) / 2;
        int left = childLeft + moveForAligningCenter;
        for (View view : childViewList) {
            LayoutParams childLayoutParams1 = (LayoutParams) view.getLayoutParams();
            left += childLayoutParams1.leftMargin;
            view.layout(left, curTop + childLayoutParams1.topMargin, left + view.getMeasuredWidth(), curTop + childLayoutParams1.topMargin + view.getMeasuredHeight());
            left += view.getMeasuredWidth() + childLayoutParams1.rightMargin;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        // Measurement will ultimately be computing these values.
        int rowMaxHeight = 0;
        int currentRowWidth = 0;
        int childState = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            // Measure the child.
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            LayoutParams childLayoutParams = (LayoutParams) child.getLayoutParams();
            int childEffectiveWidth = child.getMeasuredWidth() + childLayoutParams.leftMargin + childLayoutParams.rightMargin;
            int childEffectiveHeight = child.getMeasuredHeight() + childLayoutParams.topMargin + childLayoutParams.bottomMargin;

            if (currentRowWidth + childEffectiveWidth > deviceWidth) {
                currentRowWidth = childEffectiveWidth;
                rowMaxHeight += childEffectiveHeight;
            } else {
                currentRowWidth += childEffectiveWidth;
                rowMaxHeight = Math.max(rowMaxHeight, childEffectiveHeight);
            }

            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(deviceWidth, widthMeasureSpec, childState),
                resolveSizeAndState(rowMaxHeight + this.getPaddingBottom() + this.getPaddingTop(), heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }


    /**
     * Per-child layout information for layouts that support margins.
     * See {@link android.R.styleable#FrameLayout_Layout FrameLayout Layout Attributes}
     * for a list of all child view attributes that this class supports.
     *
     * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
     */
    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         *
         * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
         * @see android.view.Gravity
         */
        public int gravity = -1;

        /**
         * {@inheritDoc}
         */

        public LayoutParams() {
            this(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

//            TypedArray a = c.obtainStyledAttributes(attrs, com.android.internal.R.styleable.FrameLayout_Layout);
//            gravity = a.getInt(com.android.internal.R.styleable.FrameLayout_Layout_layout_gravity, -1);
//            a.recycle();
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int width, int height) {
            super(width, height);
        }

        /**
         * Creates a new set of layout parameters with the specified width, height
         * and weight.
         *
         * @param width   the width, either {@link #MATCH_PARENT},
         *                {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height  the height, either {@link #MATCH_PARENT},
         *                {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param gravity the gravity
         * @see android.view.Gravity
         */
        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * Copy constructor. Clones the width, height, margin values, and
         * gravity of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(LayoutParams source) {
            super(source);

            this.gravity = source.gravity;
        }
    }


}