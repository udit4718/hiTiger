package com.hcs.hitiger.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by hot cocoa on 12/05/16.
 */
public class CustomBorderedShapeDrawable extends ShapeDrawable {

    int strokeColor = Color.WHITE;

    public CustomBorderedShapeDrawable(RoundRectShape roundRectShape) {
        super(roundRectShape);
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        super.onDraw(shape, canvas, paint);
        Paint strokepaint = new Paint(paint);
        strokepaint.setStyle(Paint.Style.STROKE);
        strokepaint.setStrokeWidth(12);
        strokepaint.setColor(strokeColor);
        super.onDraw(shape, canvas, strokepaint);
    }
}
