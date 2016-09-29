package com.hcs.hitiger.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.api.model.user.Sport;

/**
 * Created by hot cocoa on 13/05/16.
 */
public class SportsViewHelper {

    private static int value4Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, HitigerApplication.getInstance().getResources().getDisplayMetrics());
    private static int value6Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, HitigerApplication.getInstance().getResources().getDisplayMetrics());
    private static int value8Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, HitigerApplication.getInstance().getResources().getDisplayMetrics());

    public static StateListDrawable getStateListForSportsView(int color) {
        StateListDrawable states = new StateListDrawable();
        ShapeDrawable defaultShapeDrawable = SportsViewHelper.getRoundedColoredDrawable(color);
        Drawable shapeDrawableForSelectedState = SportsViewHelper.getRoundedStrokeColoredDrawable(color);
        states.addState(new int[]{android.R.attr.state_pressed},
                defaultShapeDrawable);
        states.addState(new int[]{android.R.attr.state_selected}, defaultShapeDrawable);
        states.addState(new int[]{},
                shapeDrawableForSelectedState);
        return states;
    }

    public static ColorStateList getColorStateList(int color, int defaultColor) {
        return new ColorStateList(new int[][]{new int[]{android.R.attr.state_selected}, new int[]{android.R.attr.state_pressed}, new int[]{}}, new int[]{defaultColor, defaultColor, color});
    }


    private static float value24Dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, HitigerApplication.getInstance().getResources().getDisplayMetrics());
    private static int value1Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, HitigerApplication.getInstance().getResources().getDisplayMetrics());

    public static ShapeDrawable getRoundedColoredDrawable(int color) {
        float[] roundedCorner = new float[]{value24Dp, value24Dp, value24Dp, value24Dp, value24Dp, value24Dp, value24Dp, value24Dp};
        float[] innerRoundedCorner = new float[]{value24Dp, value24Dp, value24Dp, value24Dp, value24Dp, value24Dp, value24Dp, value24Dp};
        RoundRectShape roundRectShape = new RoundRectShape(roundedCorner, null, innerRoundedCorner);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    public static GradientDrawable getRoundedStrokeColoredDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(value24Dp);
        drawable.setStroke(value1Dp, color);
        return drawable;
    }

    public static void setStyleToSporstTetView(TextView textView, ViewGroup.MarginLayoutParams params, Sport sport) {
        params.setMargins(value4Dp, value6Dp, value4Dp, value6Dp);
        textView.setPadding(2 * value6Dp, value8Dp, 2 * value6Dp, value8Dp);
        textView.setLayoutParams(params);
        textView.setTextColor(SportsViewHelper.getColorStateList(Color.parseColor(sport.getColor()), Color.WHITE));
        textView.setBackgroundDrawable(SportsViewHelper.getStateListForSportsView(Color.parseColor(sport.getColor())));
        textView.setSelected(sport.isAdded());
        textView.setTypeface(HitigerApplication.SEMI_BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setSingleLine(true);
    }
}
