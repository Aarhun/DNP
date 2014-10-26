package com.tacitus.dnp.scenario;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class SoundDrawable extends Drawable {

    private Paint mPaint;
    private int mColor;
    private String mText;

    public SoundDrawable() {
        mPaint = new Paint();
        // White
        mColor = 0xffffffff;
        mText = "";
    }

    public SoundDrawable(int color, String text) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mColor = color;
        mText = text;
    }


    @Override
    public void draw(Canvas canvas) {
        // Black
        mPaint.setColor(0xff000000);
        RectF rectF = new RectF(getBounds());
        canvas.drawRoundRect(rectF, 20, 20, mPaint);
        mPaint.setColor(mColor);
        int strokeWidth = (int) rectF.right / 15;
        rectF = new RectF(rectF.left+strokeWidth, rectF.top+strokeWidth, rectF.right-strokeWidth, rectF.bottom-strokeWidth);
        canvas.drawRoundRect(rectF, 20, 20, mPaint);
        mPaint.setColor(0xffffffff);
        mPaint.setTextSize(strokeWidth * 5);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2)) ;

        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mText, xPos, yPos, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void changeColor(int color) {
        mColor = color;
        invalidateSelf();
    }

    public void changeText(String text) {
        mText = text;
        invalidateSelf();
    }

    public String getText() {
        return mText;
    }
}
