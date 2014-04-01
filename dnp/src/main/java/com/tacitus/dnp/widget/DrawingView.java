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
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import com.tacitus.dnp.Log;
import com.tacitus.dnp.R;

import junit.framework.Assert;

import java.util.ArrayList;

public class DrawingView extends View {

    private class DrawWatcher implements Runnable {
        private ArrayList<DrawPath> mDrawPaths = new ArrayList<DrawPath>();
        private boolean running = false;

        @Override
        public void run() {
            running = true;
            Path path = new Path();
            Paint paint = createPaint();
//            paint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
            paint.setColor(mPaintColor);

            float size = 50;
            for (int j = 0; j < mDrawPaths.size(); j++) {
                if (!mDrawPaths.get(j).isTerminated()) {
                    continue;
                }
                float moveX = 0;
                float moveY = 0;
                float lineToX = 0;
                float lineToY = 0;

                float lastX1 = mDrawPaths.get(j).getLastX();
                float lastY1 = mDrawPaths.get(j).getLastY();
                float firstX1 = mDrawPaths.get(j).getFirstX();
                float firstY1 = mDrawPaths.get(j).getFirstY();
                size = mDrawPaths.get(j).getSize();
                double minimalDistance = 0;
                // For each path extremity search nearest other path extremity:
                for (int i = 0; i < mDrawPaths.size(); i++) {
                    if ( i == j ) {
                        continue;
                    }
                    float lastX2 = mDrawPaths.get(i).getLastX();
                    float lastY2 = mDrawPaths.get(i).getLastY();
                    float firstX2 = mDrawPaths.get(i).getFirstX();
                    float firstY2 = mDrawPaths.get(i).getFirstY();

                    double distanceFirst1First2 = Math.sqrt((firstX1 - firstX2) * (firstX1 - firstX2) + (firstY1 - firstY2) * (firstY1 - firstY2));
                    double distanceFirst1Last2 = Math.sqrt((firstX1 - lastX2) * (firstX1 - lastX2) + (firstY1 - lastY2) * (firstY1 - lastY2));
                    double distanceFirst2Last1 = Math.sqrt((firstX2 - lastX1) * (firstX2 - lastX1) + (firstY2 - lastY1) * (firstY2 - lastY1));
                    double distanceLast1Last2 = Math.sqrt((lastX1 - lastX2) * (lastX1 - lastX2) + (lastY1 - lastY2) * (lastY1 - lastY2));

                    if (distanceFirst1First2 < minimalDistance || minimalDistance == 0) {
                        moveX = firstX1;
                        moveY = firstY1;
                        lineToX = firstX2;
                        lineToY = firstY2;
                        minimalDistance = distanceFirst1First2;
                    }
                    if (distanceFirst1Last2 < minimalDistance) {
                        moveX = firstX1;
                        moveY = firstY1;
                        lineToX = lastX2;
                        lineToY = lastY2;
                        minimalDistance = distanceFirst1Last2;
                    }
                    if (distanceFirst2Last1 < minimalDistance) {
                        moveX = firstX2;
                        moveY = firstY2;
                        lineToX = lastX1;
                        lineToY = lastY1;
                        minimalDistance = distanceFirst2Last1;
                    }
                    if (distanceLast1Last2 < minimalDistance) {
                        moveX = lastX1;
                        moveY = lastY1;
                        lineToX = lastX2;
                        lineToY = lastY2;
                        minimalDistance = distanceLast1Last2;
                    }
                }
                // For each path trace a line between nearest extremities found
                path.moveTo(moveX, moveY);
                path.lineTo(lineToX, lineToY);
                paint.setStrokeWidth(size);
            }
            mDrawCanvas.drawPath(path, paint);
            invalidate();
            path.close();
            mDrawPaths.clear();
            running = false;
        }

        public void addPath(DrawPath drawPath) {
            if (!running) {
                mDrawPaths.add(drawPath);
            }
        }
    }

    private class DrawPath {
        private Path mDrawPath;
        private Paint mDrawPaint;
        private Paint mDrawPaintHollow;
        private float mSize;
        private float mSizeHollow;
        private float mPressure;
        private long mLastEventTime;
        private float mFirstX;
        private float mFirstY;
        private float mLastX;
        private float mLastY;
        private boolean mTerminated;

        private DrawPath(float size, float pressure, long downTime) {
            mDrawPath = new Path();
            mDrawPaint = createPaint();
            mDrawPaint.setColor(mPaintColor);
            mSize = size;
            mLastX = 0;
            mLastY = 0;
            mFirstX = 0;
            mFirstY = 0;
            mPressure = pressure;
            mLastEventTime = downTime;
            mTerminated = false;
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

        public void drawCircle(float x, float y) {
            mDrawCanvas.drawCircle(x, y, mSize / 2, mDrawPaint);
            if (mHollowMode && !mEraseMode) {
                mDrawCanvas.drawCircle(x, y, mSizeHollow / 2, mDrawPaintHollow);
            }
            mLastX = x;
            mLastY = y;
        }

        public void drawPath() {
            mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
            if (mDrawPaintHollow != null) {
                mDrawCanvas.drawPath(mDrawPath, mDrawPaintHollow);
            }
            invalidate();
        }

        public void closePath() {
            mTerminated = true;
            mDrawPath.close();
        }

        public long getLastEventTime() {
            return mLastEventTime;
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


        public float getSize() {
            return mSize;
        }

        public boolean isTerminated() {
            return mTerminated;
        }
    }

    private DrawWatcher mDrawWatcher = new DrawWatcher();
    private Handler mHandler;
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
        mHandler = new Handler();
//        scheduleTimer();
    }

    public void scheduleTimer() {
        mHandler.postDelayed(mDrawWatcher, 1000);
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
        String tag = "";
        int index = MotionEventCompat.getActionIndex(event);
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_UP:
                tag = "ACTION_UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                tag = "ACTION_CANCEL";
                break;
            case MotionEvent.ACTION_MOVE:
                tag = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_DOWN:
                tag = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
                tag = "ACTION_HOVER_ENTER";
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                tag = "ACTION_HOVER_EXIT";
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                tag = "ACTION_HOVER_MOVE";
                break;
            case MotionEvent.ACTION_OUTSIDE:
                tag = "ACTION_OUTSIDE";
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                tag = "ACTION_POINTER_DOWN";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                tag = "ACTION_POINTER_UP";
                break;
            case MotionEvent.ACTION_SCROLL:
                tag = "ACTION_SCROLL";
                break;
        }
        Log.e(tag, "-DOWN TIME: ", event.getDownTime());
        Log.e(tag, "-EVENT TIME: ", event.getEventTime());
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


        InputDevice device = event.getDevice();
        Assert.assertNotNull(device);
        InputDevice.MotionRange motionRangeMajor = device.getMotionRange(MotionEvent.AXIS_TOUCH_MAJOR);
        InputDevice.MotionRange motionRangeMinor = device.getMotionRange(MotionEvent.AXIS_TOUCH_MINOR);
        Assert.assertNotNull(motionRangeMajor);
        Assert.assertNotNull(motionRangeMinor);
        float touchMajorMax = motionRangeMajor.getMax();
        float touchMinorMax = motionRangeMinor.getMax();


        float size = (((event.getTouchMajor(index) / touchMajorMax) + (event.getTouchMinor(index) / touchMinorMax)) / 2) * 2700;
        float pressure = event.getPressure(index);
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
//                logEvent(event);
                // Create path and draw a small line of 1 pixel:
                DrawPath drawPath = new DrawPath(size, pressure, event.getDownTime());
                drawPath.moveTo(touchX, touchY);
                drawPath.lineTo(touchX - 1, touchY - 1);
                drawPath.drawPath();
                mDrawWatcher.addPath(drawPath);
                mDrawPaths.put(id, drawPath);
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    scheduleTimer();
                }
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
