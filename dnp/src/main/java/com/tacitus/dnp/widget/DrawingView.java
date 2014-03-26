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
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.tacitus.dnp.R;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;


public class DrawingView extends View {

    //drawing path & paint
    private Map<Integer, Path> mDrawPaths = new HashMap<Integer, Path>();
    private Map<Integer, Paint> mDrawPaints = new HashMap<Integer, Paint>();
    private Map<Integer, Paint> mDrawPaintsHollow = new HashMap<Integer, Paint>();

    private final float MAJOR_TOUCH_RATIO = 1.5f;
    private final int HOLLOW_LINE_THICKNESS_RATIO = 20;

    private Paint mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
//    private ArrayList<EventHolder> mEventList = new ArrayList<EventHolder>();

    

    private boolean mHollowMode = false;
    private boolean mTouchSizeMode = true;
    private boolean mEraseMode = false;


    //canvas bitmap
    private Bitmap mCanvasBitmap;

    private float mBrushSize;

//    private static class EventHolder implements Parcelable {
//        int mEventType;
//        float mX;
//        float mY;
//        int mColor;
//        float mBrushSize;
//
//        public EventHolder(int event, float x, float y, int color, float brushSize) {
//            this.mEventType = event;
//            this.mX = x;
//            this.mY = y;
//            this.mColor = color;
//            this.mBrushSize = brushSize;
////            Log.e("ERROR", "Save event: " + event + " " + x + "/" + y + " " + color + " " + brushSize);
//
//        }
//        public int describeContents() {
//            return 0;
//        }
//
//        public void writeToParcel(Parcel out, int flags) {
//            out.writeInt(mEventType);
//            out.writeFloat(mX);
//            out.writeFloat(mY);
//            out.writeInt(mColor);
//            out.writeFloat(mBrushSize);
//        }
//
//    }

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
                Path path = new Path();
                path.moveTo(touchX, touchY);
                path.lineTo(touchX - 1, touchY - 1);
                Paint paint = createPaint(size / MAJOR_TOUCH_RATIO, false);

                mDrawCanvas.drawPath(path, paint);
                mDrawPaths.put(id, path);
                // Create Hollow paint only if eraseMode is not enable
                if (mHollowMode && !mEraseMode) {
                    Paint paintHollow = createPaint((size / MAJOR_TOUCH_RATIO) - ((size / MAJOR_TOUCH_RATIO) * HOLLOW_LINE_THICKNESS_RATIO / 100), true);
                    mDrawCanvas.drawPath(path, paintHollow);
                    mDrawPaintsHollow.put(id, paintHollow);
                }
                mDrawPaints.put(id, paint);
                invalidate();


                break;

            case MotionEvent.ACTION_MOVE:
                // In case of ACTION_MOVE event we update all paths:
                for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++) {
                    int currentId = MotionEventCompat.getPointerId(event, i);
                    path = mDrawPaths.get(currentId);
                    if (path != null) {
                        touchX = MotionEventCompat.getX(event, i);
                        touchY = MotionEventCompat.getY(event, i);
                        path.lineTo(touchX, touchY);
                        paint = mDrawPaints.get(currentId);
                        mDrawCanvas.drawPath(path, paint);
                        Paint paintHollow = mDrawPaintsHollow.get(currentId);
                        if (paintHollow != null) {
                            mDrawCanvas.drawPath(path, paintHollow);
                        }
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                // Delete path:
                path = mDrawPaths.remove(id);
                path.close();
                // Delete paint:
                mDrawPaints.remove(id);
                // Delete paint hollow:
                mDrawPaintsHollow.remove(id);
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

//    @Override
//    public Parcelable onSaveInstanceState()
//    {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("INSTANCE_STATE", super.onSaveInstanceState());
//        bundle.putParcelableArrayList("EVENT_LIST", mEventList);
//        bundle.putInt("PAINT_COLOR", mPaintColor);
//        bundle.putFloat("BRUSH_SIZE", mBrushSize);
//
//        return bundle;
//    }
//
//    @Override
//    public void onRestoreInstanceState(Parcelable state)
//    {
//        if (state instanceof Bundle)
//        {
//            final Bundle bundle = (Bundle) state;
//            super.onRestoreInstanceState(bundle.getParcelable("INSTANCE_STATE"));
//            mEventList = bundle.getParcelableArrayList("EVENT_LIST");
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    // Replay drawing when changing orientation
//                    for (EventHolder holder : mEventList) {
//                        onTouchEvent(holder);
//                    }
//                    mPaintColor = bundle.getInt("PAINT_COLOR");
//                    mDrawPaint.setColor(mPaintColor);
//                    mBrushSize = bundle.getFloat("BRUSH_SIZE");
//                    mDrawPaint.setStrokeWidth(mBrushSize);
//                    mDrawPaintHollow.setStrokeWidth(mBrushSize-((int)(mBrushSize)*HOLLOW_LINE_THICKNESS_RATIO/100));
//                }
//            });
//
//            return;
//        }
//        super.onRestoreInstanceState(state);
//    }
}
