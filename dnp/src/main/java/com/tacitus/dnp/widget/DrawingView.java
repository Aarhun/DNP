package com.tacitus.dnp.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.tacitus.dnp.R;

import junit.framework.Assert;


public class DrawingView extends View {

    private class DrawPath {
        private Path mDrawPath;
        private Paint mDrawPaint;
        private Paint mDrawPaintHollow ;

        private DrawPath(float size) {
            mDrawPath = new Path();
            mDrawPaint = createPaint(size, false);
            // Create Hollow paint only if eraseMode is not enable
            if (mHollowMode && !mEraseMode) {
                mDrawPaintHollow = createPaint(size - (size * HOLLOW_LINE_THICKNESS_RATIO / 100), true);
            }
        }

        public void moveTo(float x, float y) {
            mDrawPath.moveTo(x, y);
        }

        public void lineTo(float x, float y) {
            mDrawPath.lineTo(x, y);
        }

        public void drawPath() {
            mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
            if (mDrawPaintHollow != null) {
                mDrawCanvas.drawPath(mDrawPath, mDrawPaintHollow);
            }
            invalidate();
        }

        public void closePath() {
            mDrawPath.close();
        }
    }

    //drawing path & paint
    private SparseArray<DrawPath> mDrawPaths = new SparseArray<DrawPath>();

    private final float MAJOR_TOUCH_RATIO = 1.5f;
    private final int HOLLOW_LINE_THICKNESS_RATIO = 20;

    private Paint mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
    private boolean mHollowMode = false;
    private boolean mTouchSizeMode = true;
    private boolean mEraseMode = false;


    //canvas bitmap
    private Bitmap mCanvasBitmap;

    private float mBrushSize;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        setBrushSize(getResources().getDimension(R.dimen.initial_size));
    }

    private Paint createPaint(float size, boolean isHollow) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(mPaintColor);

        //update size
        setBrushSize(paint, size);
        if (mEraseMode || isHollow) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        return paint;
    }

    private void setBrushSize(Paint paint, float size) {
        Resources resources = getResources();
        Assert.assertNotNull(resources);
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                size, resources.getDisplayMetrics()));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
         //draw view
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = MotionEventCompat.getX(event, MotionEventCompat.getActionIndex(event));
        float touchY = MotionEventCompat.getY(event, MotionEventCompat.getActionIndex(event));

        // Index of multiple touch event:
        int index = MotionEventCompat.getActionIndex(event);
        // Id of multiple touch event
        int id = MotionEventCompat.getPointerId(event, index);
        float size = mBrushSize;
        if (mTouchSizeMode) {
            size = event.getTouchMajor(index);
        }

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                // Create path and draw a small line of 1 pixel:
                DrawPath drawPath = new DrawPath(size / MAJOR_TOUCH_RATIO);
                drawPath.moveTo(touchX, touchY);
                drawPath.lineTo(touchX - 1, touchY - 1);
                drawPath.drawPath();
                mDrawPaths.put(id, drawPath);
                break;

            case MotionEvent.ACTION_MOVE:
                // In case of ACTION_MOVE event we update all paths:
                for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++) {
                    int currentId = MotionEventCompat.getPointerId(event, i);
                    drawPath = mDrawPaths.get(currentId);
                    if (drawPath != null) {
                        touchX = MotionEventCompat.getX(event, i);
                        touchY = MotionEventCompat.getY(event, i);
                        drawPath.lineTo(touchX, touchY);
                        drawPath.drawPath();
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                // Delete path:
                drawPath = mDrawPaths.get(id);
                if (drawPath != null) {
                    mDrawPaths.remove(id);
                    drawPath.closePath();
                }
                break;

            default:
                // Do not consume other events.
                return false;
        }
        // Consume handled event.
        return true;
    }

    public void setColor(String newColor){
        //set color
        invalidate();
        mPaintColor = Color.parseColor(newColor);
    }

    public void setBrushSize(float newSize){
        //update size
        Resources resources = getResources();
        Assert.assertNotNull(resources);
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, resources.getDisplayMetrics());
    }

    public void setHollowMode(boolean hollowMode) {
        mHollowMode = hollowMode;
    }

    public void setEraseMode(boolean eraseMode) {
        mEraseMode = eraseMode;
    }

    public void setTouchSizeMode(boolean touchSizeMode) {
        mTouchSizeMode = touchSizeMode;
    }

    public void startNew(){
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
