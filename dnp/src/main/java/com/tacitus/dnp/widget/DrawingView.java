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
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import com.tacitus.dnp.Log;
import com.tacitus.dnp.R;

import junit.framework.Assert;

public class DrawingView extends View {

    private class DrawPath {
        private Path mDrawPath;
        private Paint mDrawPaint;
        private Paint mDrawPaintHollow;
        private float mSize;
        private float mSizeHollow;
        private float mPressure;

        private DrawPath(float size, float pressure) {
            mDrawPath = new Path();
            mDrawPaint = createPaint();
            mDrawPaint.setColor(mPaintColor);
            mSize = size;
            mPressure = pressure;
            if (!mTouchSizeMode) {
                mSize = mBrushSize;
            }
            // Create Hollow paint only if eraseMode is not enable
            if (mHollowMode && !mEraseMode) {
                mSizeHollow = mSize - (mSize * HOLLOW_LINE_THICKNESS_RATIO / 100);
                mDrawPaintHollow = createPaint();
                mDrawPaintHollow.setStrokeWidth(mSizeHollow);
                mDrawPaintHollow.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            mDrawPaint.setStrokeWidth(mSize);
            mDrawPaint.setAlpha(mPaintAlpha);
            if (mPressureMode) {
                mDrawPaint.setAlpha((int) (mPressure * 255));
            }
        }

        public void moveTo(float x, float y) {
            mDrawPath.moveTo(x, y);
        }

        public void lineTo(float x, float y) {
            mDrawPath.lineTo(x, y);
        }

        public void drawCircle(float x, float y) {
            mDrawCanvas.drawCircle(x, y, mSize / 2, mDrawPaint);
            if (mHollowMode && !mEraseMode) {
                mDrawCanvas.drawCircle(x, y, mSizeHollow / 2, mDrawPaintHollow);
            }

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

    private final int HOLLOW_LINE_THICKNESS_RATIO = 20;

    private Paint mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
    private boolean mHollowMode = false;
    private boolean mTouchSizeMode = true;
    private boolean mEraseMode = false;
    private boolean mPressureMode = false;


    //canvas bitmap
    private Bitmap mCanvasBitmap;

    private float mBrushSize;

    private int mPaintAlpha;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        Resources resources = getResources();
        Assert.assertNotNull(resources);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        setBrushSize(resources.getInteger(R.integer.initial_size) * 2);
    }

    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        if (mEraseMode) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        //view given size
        super.onSizeChanged(w, h, oldW, oldH);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
         //draw view
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
    }

    private void logEvent(MotionEvent event) {
        int index = MotionEventCompat.getActionIndex(event);
        for (InputDevice.MotionRange motionRange : event.getDevice().getMotionRanges()) {
            switch (motionRange.getAxis()) {
                case MotionEvent.AXIS_X:
                    Log.e("AXIS_X");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getX(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_Y:
                    Log.e("AXIS_Y");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getY(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_PRESSURE:
                    Log.e("AXIS_PRESSURE");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getPressure(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_SIZE:
                    Log.e("AXIS_SIZE");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getSize(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOUCH_MAJOR:
                    Log.e("AXIS_TOUCH_MAJOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getTouchMajor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOUCH_MINOR:
                    Log.e("AXIS_TOUCH_MINOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getTouchMinor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOOL_MAJOR:
                    Log.e("AXIS_TOOL_MAJOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getToolMajor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOOL_MINOR:
                    Log.e("AXIS_TOOL_MINOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getToolMinor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_ORIENTATION:
                    Log.e("AXIS_ORIENTATION");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getOrientation(index));
                    Log.e("***********************");
                    break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = MotionEventCompat.getX(event, MotionEventCompat.getActionIndex(event));
        float touchY = MotionEventCompat.getY(event, MotionEventCompat.getActionIndex(event));

        // Index of multiple touch event:
        int index = MotionEventCompat.getActionIndex(event);
        // Id of multiple touch event
        int id = MotionEventCompat.getPointerId(event, index);

        float size = (event.getTouchMajor(index) + event.getTouchMinor(index)) / 2;
        float pressure = event.getPressure(index);

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
//                logEvent(event);

                // Create path and draw a small line of 1 pixel:
                DrawPath drawPath = new DrawPath(size, pressure);
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

    public void setBrushSize(float brushSize){
        //update size
        mBrushSize = brushSize;
    }

    public void setPaintAlpha(int alphaValue) {
        mPaintAlpha = alphaValue;
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

    public void setPressureMode(boolean pressureMode) {
        mPressureMode = pressureMode;
    }

    public void startNew(){
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void loadImage(Bitmap bitmap) {
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mDrawCanvas.drawBitmap(bitmap, 0, 0, null);
        invalidate();
    }
}
