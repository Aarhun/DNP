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
import android.widget.Toast;

import com.tacitus.dnp.R;

import junit.framework.Assert;

import java.util.ArrayList;

public class DrawingView extends View {

    private class DrawWatcher implements Runnable {

        @Override
        public void run() {
            resetColorOrder();
        }
    }

    private class DrawPath {
        private Path mDrawPath;
        private Paint mDrawPaint;
        private Paint mDrawPaintHollow;
        private float mSize;
        private float mSizeHollow;
        private float mPressure;


        private DrawPath(float size, float pressure, int color) {
            mDrawPath = new Path();
            mDrawPaint = createPaint();
            mDrawPaint.setColor(color);
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
            if (mUnderlineMode && !mEraseMode) {
                mDrawPaint.setShadowLayer(1f, mSize / 2, mSize / 2, Color.BLACK);
            }
            // WARNING: When changing color needs to define alpha AFTER
            // Because color contains some alpha setting.
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
        }

        public void drawPath(Canvas canvas) {
            canvas.drawPath(mDrawPath, mDrawPaint);
            if (mDrawPaintHollow != null) {
                canvas.drawPath(mDrawPath, mDrawPaintHollow);
            }
        }

        public void resetPath() {
            mDrawPath.reset();
        }

    }

    private Handler mHandler;
    private DrawWatcher mDrawWatcher = new DrawWatcher();

    //drawing path & paint
    private SparseArray<DrawPath> mDrawPaths = new SparseArray<DrawPath>();
    private SparseArray<Integer> mDrawPaintColors = new SparseArray<Integer>();
    private ArrayList<DrawPath> mDrawPathsHistory = new ArrayList<DrawPath>();
    private ArrayList<DrawPath> mDrawPathsRedoable = new ArrayList<DrawPath>();
    private int mCurrentColorCursor;

    private final int HOLLOW_LINE_THICKNESS_RATIO = 20;

    private Paint mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
    private boolean mHollowMode = false;
    private boolean mUnderlineMode = false;
    private boolean mTouchSizeMode = true;
    private boolean mEraseMode = false;
    private boolean mPressureMode = false;
    private boolean mOldTabletMode = false;


    private float mBrushSize;
    private float mBrushSizeOldTablet;

    private int mPaintAlpha;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        resetColorOrder();
        Resources resources = getResources();
        Assert.assertNotNull(resources);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        setBrushSize(resources.getInteger(R.integer.initial_size) * 2);
        mHandler = new Handler();
        mDrawCanvas = new Canvas();
    }

    public void initDrawWatcherTimer() {
        mHandler.removeCallbacks(mDrawWatcher);
        mHandler.postDelayed(mDrawWatcher, 10000);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
         //draw view
        for (int i=0; i<mDrawPathsHistory.size(); i++) {
            DrawPath drawPath = mDrawPathsHistory.get(i);
            drawPath.drawPath(canvas);
        }
        for (int i=0; i<mDrawPaths.size(); i++) {
            DrawPath drawPath = mDrawPaths.valueAt(i);
            drawPath.drawPath(canvas);
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

        float multiplier = 2700;
        if (mOldTabletMode) {
            multiplier *= mBrushSizeOldTablet;
        }
        float size = (((event.getTouchMajor(index) / touchMajorMax) + (event.getTouchMinor(index) / touchMinorMax)) / 2) * multiplier;
        float pressure = event.getPressure(index);
        DrawPath drawPath;

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
//                Log.logEvent(event);

                // Create path and draw a small line of 1 pixel:
                Integer color = getColor(mCurrentColorCursor);
                if (color != null) {
                    drawPath = new DrawPath(size, pressure, color);
                    drawPath.moveTo(touchX, touchY);
                    drawPath.lineTo(touchX - 1, touchY - 1);
                    mDrawPaths.put(id, drawPath);
                    invalidate();
                } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN){
                    // Toast only for ACTION_DOWN to avoid spamming.
                    Context context = getContext();
                    Assert.assertNotNull(context);
                    Toast noColorChosen = Toast.makeText(context,
                            R.string.no_color_chosen, Toast.LENGTH_SHORT);
                    noColorChosen.show();
                }
                initDrawWatcherTimer();
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
                    }
                }
                initDrawWatcherTimer();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCurrentColorCursor++;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                // Delete path:
                drawPath = mDrawPaths.get(id);
                if (drawPath != null) {
                    drawPath.lineTo(touchX, touchY);
                    drawPath.drawPath();
                    mDrawPathsHistory.add(drawPath);
                    mDrawPathsRedoable.clear();
                    mDrawPaths.remove(id);
                    invalidate();
                }
                initDrawWatcherTimer();
                break;

            default:
                // Do not consume other events.
                return false;
        }
        // Consume handled event.
        return true;
    }

    private Integer getColor(int id) {
        Integer color;
        for (int i = id; i >= 0; i--) {
            color = mDrawPaintColors.get(i);
            if (color != null) {
                return color;
            }
        }
        return null;
    }


    public void setColor(String newColor){
        //set color
//        invalidate();
        mPaintColor = Color.parseColor(newColor);
    }

    public void setColor(String newColor, int id){
        //set color
//        invalidate();
        mDrawPaintColors.put(id, Color.parseColor(newColor));
    }

    public void clearColor(int id){
        //clear color
        mDrawPaintColors.remove(id);
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

    public void setUnderlineMode(boolean underlineMode) {
        mUnderlineMode = underlineMode;
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
        mDrawPathsHistory.clear();
        resetColorOrder();
        invalidate();
    }

    public void loadImage(Bitmap bitmap) {
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mDrawCanvas.drawBitmap(bitmap, 0, 0, null);
        mDrawPathsHistory.clear();
        resetColorOrder();
        invalidate();
    }

    public void setBrushSizeOldTablet(float brushSizeOldTablet){
        //update size for old tablet
        mBrushSizeOldTablet = brushSizeOldTablet;
    }

    public void setOldTabletMode(boolean oldTabletMode) {
        mOldTabletMode = oldTabletMode;
    }

    public void resetColorOrder() {
        mCurrentColorCursor = 0;
    }

    public void undo() {
        if (mDrawPathsHistory.size() > 0) {
            // Never set the color to 0, it would means that no color are chosen.
            if (mCurrentColorCursor > 1) {
                mCurrentColorCursor--;
            }
            DrawPath lastPath = mDrawPathsHistory.get(mDrawPathsHistory.size() - 1);
            mDrawPathsHistory.remove(lastPath);
            mDrawPathsRedoable.add(lastPath);
            invalidate();
        } else {
            Context context = getContext();
            Assert.assertNotNull(context);
            Toast nothingToUndo = Toast.makeText(context,
                    R.string.nothing_to_undo, Toast.LENGTH_SHORT);
            nothingToUndo.show();
        }
    }

    public void redo() {
        if (mDrawPathsRedoable.size() > 0) {
            DrawPath lastPath = mDrawPathsRedoable.get(mDrawPathsRedoable.size() - 1);
            mDrawPathsRedoable.remove(lastPath);
            mDrawPathsHistory.add(lastPath);
            invalidate();
        } else {
            Context context = getContext();
            Assert.assertNotNull(context);
            Toast nothingToRedo = Toast.makeText(context,
                    R.string.nothing_to_redo, Toast.LENGTH_SHORT);
            nothingToRedo.show();
        }

    }
}
