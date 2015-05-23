package com.tacitus.dnp.widget;

import android.content.Context;
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
import android.widget.Toast;

import com.tacitus.dnp.R;
import com.tacitus.dnp.Util;
import com.tacitus.dnp.scenario.Step;

import junit.framework.Assert;

import java.util.ArrayList;

public class DrawingView extends View {

    private class DrawingStep {
        private ArrayList<DrawPath> mStepDrawPathsHistory = new ArrayList<DrawPath>();
        private Step mStep;


        public Step getStep() {
            return mStep;
        }

        public void setStep(Step step) {
            this.mStep = step;
        }

        public ArrayList<DrawPath> getDrawPathsHistory() {
            return mStepDrawPathsHistory;
        }

        public void setDrawPathsHistory(ArrayList<DrawPath> drawPathsHistory) {
            this.mStepDrawPathsHistory = drawPathsHistory;
        }


    }

    private class DrawPath {
        private Path mDrawPath;
        private Paint mDrawPaint;
        private Paint mDrawPaintHollow;
        private float mSize;
        private float mSizeHollow;

        private DrawPath(float size, float pressure, Step step) {
            mDrawPath = new Path();
            mDrawPaint = createPaint();
            mDrawPaint.setColor(step.getLine().getColor());
            mSize = size;
            // Create Hollow paint only if eraseMode is not enable
            if (step.getLine().isHollow()) {
                mSizeHollow = mSize - (mSize * HOLLOW_LINE_THICKNESS_RATIO / 100);
                mDrawPaintHollow = createPaint();
                mDrawPaintHollow.setStrokeWidth(mSizeHollow);
                mDrawPaintHollow.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            mDrawPaint.setStrokeWidth(mSize);
            if (step.getLine().isUnderline()) {
                mDrawPaint.setShadowLayer(1f, mSize / 2, mSize / 2, Color.BLACK);
            }
            // WARNING: When changing color needs to define alpha AFTER
            // Because color contains some alpha setting.
            mDrawPaint.setAlpha(getResources().getInteger(R.integer.initial_alpha));
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

    //drawing path & paint
    private SparseArray<DrawPath> mDrawPaths = new SparseArray<DrawPath>();
    private ArrayList<DrawPath> mDrawPathsHistory = new ArrayList<DrawPath>();
    private SparseArray<DrawingStep> mStepsHistory = new SparseArray<DrawingStep>();
    private ArrayList<DrawPath> mDrawPathsRedoable = new ArrayList<DrawPath>();

    private int mCurrentStepCursor = 0;

    private final int HOLLOW_LINE_THICKNESS_RATIO = 20;

    private Paint mCanvasPaint;
    //canvas
    private Canvas mDrawCanvas;
    private boolean mOldTabletMode = false;

    private Bitmap mCanvasBitmap;
    private Bitmap mLoadedBitmap;

    private float mBrushSizeOldTablet;


    private Toast mNothingToUndo;
    private Toast mNothingToRedo;
    private Toast mNoColorSelected;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setScenario(ArrayList<Step> steps) {
        for (int i=0;i<steps.size();i++){
            DrawingStep drawingStep = new DrawingStep();
            drawingStep.setStep(steps.get(i));
            mStepsHistory.put(i, drawingStep);
        }
    }


    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

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
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        // Temporary drawing current drawn paths on hardware canvas
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
                DrawingStep drawingStep = mStepsHistory.get(mCurrentStepCursor);
                if (drawingStep != null) {
                    drawPath = new DrawPath(size, pressure, mStepsHistory.get(mCurrentStepCursor).getStep());
                    drawPath.moveTo(touchX, touchY);
                    drawPath.lineTo(touchX - 1, touchY - 1);
                    mDrawPaths.put(id, drawPath);
                    // This will call the onDraw callback to draw the path temporarily in hardware canvas:
                    invalidate();
                } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN){
                    // Toast only for ACTION_DOWN to avoid spamming.
                    mNoColorSelected = Util.showToast(getContext(), mNoColorSelected, R.string.no_color_chosen);
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
                    }
                }
                // This will call the onDraw callback to draw the path temporarily in hardware canvas:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                // Delete path:
                drawPath = mDrawPaths.get(id);
                if (drawPath != null) {
                    drawPath.lineTo(touchX, touchY);
                    // Draw path definitely on bitmap:
                    drawPath.drawPath();
                    mDrawPathsHistory.add(drawPath);
                    mDrawPathsRedoable.clear();
                    mDrawPaths.remove(id);
                    // This will call the onDraw callback to draw the bitmap on hardware canvas:
                    invalidate();
                }
                break;

            default:
                // Do not consume other events.
                return false;
        }
        // Consume handled event.
        return true;
    }

    public int getCurrentStepCursor() {
        return mCurrentStepCursor;
    }

    public void startNew(){
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mDrawPathsHistory.clear();
        invalidate();
    }

    public void nextStep(){
        // Should not happened but safe check:
        if(mCurrentStepCursor < mStepsHistory.size() - 1) {
            // Save DrawPathsHistory if not empty:
            if(!mDrawPathsHistory.isEmpty()) {
                DrawingStep drawingStep = mStepsHistory.get(mCurrentStepCursor);
                drawingStep.setDrawPathsHistory(mDrawPathsHistory);
                mStepsHistory.put(mCurrentStepCursor, drawingStep);
            }
            mCurrentStepCursor++;
            startStep();
        }
    }

    public void previousStep() {
        // Should not happened but safe check:
        if(mCurrentStepCursor > 0) {
            // Save DrawPathsHistory if not empty:
            if(!mDrawPathsHistory.isEmpty()) {
                DrawingStep drawingStep = mStepsHistory.get(mCurrentStepCursor);
                drawingStep.setDrawPathsHistory(mDrawPathsHistory);
                mStepsHistory.put(mCurrentStepCursor, drawingStep);
            }
            mCurrentStepCursor--;
            startStep();
        }
    }

    private void drawPreviousSteps() {
        //Draw all previous steps
        for(int i=mCurrentStepCursor-1; i>0; --i){
            DrawingStep drawingStep = mStepsHistory.valueAt(i);
            if(drawingStep.getStep().getLinkedDown()) {
                redrawAllPaths(drawingStep.getDrawPathsHistory());
            } else {
                // If one of them is not LinkedDown, stop drawing.
                break;
            }
        }
    }

    private void startStep() {
        // Reset canvas:
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        // Draw paths of linked previous steps:
        drawPreviousSteps();
        // Get current saved paths history:
        mDrawPathsHistory = mStepsHistory.get(mCurrentStepCursor).getDrawPathsHistory();
        // Draw them:
        redrawAllPaths();
        mDrawPathsRedoable.clear();
        invalidate();
    }

    public void loadImage(Bitmap bitmap) {
        if (mLoadedBitmap != null) {
            mLoadedBitmap.recycle();
        }
        mLoadedBitmap = Bitmap.createBitmap(bitmap);
        mDrawCanvas.drawBitmap(bitmap, 0, 0, null);
        mDrawPathsHistory.clear();
        invalidate();
    }

    public void setBrushSizeOldTablet(float brushSizeOldTablet){
        //update size for old tablet
        mBrushSizeOldTablet = brushSizeOldTablet;
    }

    public void setOldTabletMode(boolean oldTabletMode) {
        mOldTabletMode = oldTabletMode;
    }

    public void undo() {
        if (mDrawPathsHistory.size() > 0) {
            DrawPath lastPath = mDrawPathsHistory.get(mDrawPathsHistory.size() - 1);
            mDrawPathsHistory.remove(lastPath);
            mDrawPathsRedoable.add(lastPath);
            resetCanvas();
            drawPreviousSteps();
            redrawAllPaths();
            invalidate();
        } else {
            mNothingToUndo = Util.showToast(getContext(), mNothingToUndo, R.string.nothing_to_undo);
        }
    }

    public void redo() {
        if (mDrawPathsRedoable.size() > 0) {
            DrawPath lastPath = mDrawPathsRedoable.get(mDrawPathsRedoable.size() - 1);
            mDrawPathsRedoable.remove(lastPath);
            mDrawPathsHistory.add(lastPath);
            lastPath.drawPath();
            invalidate();
        } else {
            mNothingToRedo = Util.showToast(getContext(), mNothingToRedo, R.string.nothing_to_redo);
        }

    }

    private void redrawAllPaths() {
        for (int i=0; i<mDrawPathsHistory.size(); ++i) {
            mDrawPathsHistory.get(i).drawPath();
        }
    }

    private void redrawAllPaths(ArrayList<DrawPath> paths) {
        for (int i=0; i<paths.size(); ++i) {
            paths.get(i).drawPath();
        }
    }

    private void resetCanvas() {
        if (mLoadedBitmap != null) {
            mDrawCanvas.drawBitmap(mLoadedBitmap, 0, 0, null);
        } else {
            mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
    }

}
