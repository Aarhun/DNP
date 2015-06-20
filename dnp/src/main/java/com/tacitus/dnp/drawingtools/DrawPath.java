package com.tacitus.dnp.drawingtools;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.tacitus.dnp.scenario.Step;
import com.tacitus.dnp.widget.DrawingView;


public class DrawPath {
    private Paint mDrawPaint;
    private Paint mDrawPaintHollow;

    private float mSize;
    private float mSizeHollow;
    private Path mDrawPath;

    private float mFirstX;
    private float mFirstY;
    private float mLastX;
    private float mLastY;

    public DrawPath(float size, int alpha, Step step) {
        mLastX = 0;
        mLastY = 0;
        mFirstX = 0;
        mFirstY = 0;
        mDrawPaint = DrawingView.createPaint();
        mDrawPaint.setColor(step.getLine().getColor());
        mSize = size;
        // Create Hollow paint only if eraseMode is not enable
        if (step.getLine().isHollow()) {
            mSizeHollow = mSize - (mSize * DrawingView.HOLLOW_LINE_THICKNESS_RATIO / 100);
            mDrawPaintHollow = DrawingView.createPaint();
            mDrawPaintHollow.setStrokeWidth(mSizeHollow);
            mDrawPaintHollow.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        mDrawPaint.setStrokeWidth(mSize);
        if (step.getLine().isUnderline()) {
            mDrawPaint.setShadowLayer(1f, mSize / 2, mSize / 2, Color.BLACK);
        }
        // WARNING: When changing color needs to define alpha AFTER
        // Because color contains some alpha setting.
        mDrawPaint.setAlpha(alpha);
        mDrawPath = new Path();
    }

    public float getSize() {
        return mSize;
    }

    public float getLastX() {
        return mLastX;
    }

    public float getLastY() {
        return mLastY;
    }

    public float getFirstX() {
        return mFirstX;
    }

    public float getFirstY() {
        return mFirstY;
    }


    public void moveTo(float x, float y) {
        mDrawPath.moveTo(x, y);
        mLastX = x;
        mLastY = y;
        mFirstX = x;
        mFirstY = y;
    }

    public void lineTo(float x, float y) {
        mDrawPath.lineTo(x, y);
        mLastX = x;
        mLastY = y;
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(mDrawPath, mDrawPaint);
        if (mDrawPaintHollow != null) {
            canvas.drawPath(mDrawPath, mDrawPaintHollow);
        }
    }

    public void resetPath() {
        mDrawPath.reset();
    }

}
