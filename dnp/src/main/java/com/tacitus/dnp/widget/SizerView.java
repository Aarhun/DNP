package com.tacitus.dnp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tacitus.dnp.R;

public class SizerView extends View {

    private Paint mCanvasPaint;
    private Paint mDrawPaint;
    //canvas
    private Canvas mDrawCanvas;

    private Bitmap mCanvasBitmap;

    private int mMultiplier;
    private float mSize;

    public SizerView(Context context, AttributeSet attrs){
        super(context, attrs);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mDrawPaint = createPaint();
        mDrawPaint.setStyle(Paint.Style.FILL);
    }

    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(getResources().getColor(android.R.color.black));
        paint.setAlpha(getResources().getInteger(R.integer.initial_alpha));

        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        //view given size
        super.onSizeChanged(w, h, oldW, oldH);
        if (mCanvasBitmap != null) {
            mCanvasBitmap.recycle();
        }
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the bitmap on hardware canvas
        drawRefRect();
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Index of multiple touch event:
        int index = MotionEventCompat.getActionIndex(event);

        mSize = event.getPressure(index);

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                drawCircle();
                break;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                break;

            default:
                // Do not consume other events.
                return false;
        }
        // Consume handled event.
        return true;
    }

    public void startNew()
    {
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    private void drawRefRect() {
        float x = mDrawCanvas.getHeight() / 2;
        float y = mDrawCanvas.getWidth() / 2;
        Paint paint = createPaint();
        paint.setStyle(Paint.Style.STROKE);
        int ref = 70;
        mDrawCanvas.drawRect(x - ref, y + ref, x + ref, y - ref, paint);
    }


    public void drawCircle(){
        startNew();
        float x = mDrawCanvas.getHeight() / 2;
        float y = mDrawCanvas.getWidth() / 2;
        mDrawCanvas.drawCircle(x, y, (mSize * mMultiplier) / 2, mDrawPaint);
        // This will call the onDraw callback to draw the path temporarily in hardware canvas:
        invalidate();
    }

    public void setMultiplier(int multiplier) {
        mMultiplier = multiplier;
    }

    public int getMultiplier() {
        return mMultiplier;
    }

}
