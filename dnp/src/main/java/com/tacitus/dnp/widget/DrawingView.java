package com.tacitus.dnp.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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

    //drawing path
    private Map<Integer, Path> mDrawPaths = new HashMap<Integer, Path>();

    //drawing and canvas paint
    private Paint mDrawPaint;
    private Paint mDrawPaintHollow;
    private Paint mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
//    private ArrayList<EventHolder> mEventList = new ArrayList<EventHolder>();

    private static int mHollowLineThickness = 20;

    private boolean mHollowMode = false;


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
        setupDrawing();
    }

    private void setupDrawing(){
    //    get drawing area setup for interaction

        mDrawPaint = new Paint();
        mDrawPaintHollow = new Paint();

        mDrawPaint.setColor(mPaintColor);

        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mDrawPaintHollow.setAntiAlias(true);
        mDrawPaintHollow.setStyle(Paint.Style.STROKE);
        mDrawPaintHollow.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaintHollow.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaintHollow.setColor(((ColorDrawable)findViewById(R.id.drawing).getBackground()).getColor());
//        mDrawPaintHollow.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);

        setBrushSize(getResources().getDimension(R.dimen.initial_size));

        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaintHollow.setStrokeWidth(mBrushSize-(int)(mBrushSize*mHollowLineThickness/100));
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



        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                // Create path and draw a small line of 1 pixel:
                Path path = new Path();
                path.moveTo(touchX, touchY);
                path.lineTo(touchX - 1, touchY - 1);
                mDrawCanvas.drawPath(path, mDrawPaint);
                if (mHollowMode) {
                    mDrawCanvas.drawPath(path, mDrawPaintHollow);
                }
                invalidate();
                mDrawPaths.put(id, path);
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
                        mDrawCanvas.drawPath(path, mDrawPaint);
                        if (mHollowMode) {
                            mDrawCanvas.drawPath(path, mDrawPaintHollow);
                        }
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                // Delete path:
                mDrawPaths.remove(id);
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
        mDrawPaint.setColor(mPaintColor);
    }

    public void setBrushSize(float newSize){
        //update size
        Resources resources = getResources();
        Assert.assertNotNull(resources);
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, resources.getDisplayMetrics());
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaintHollow.setStrokeWidth(mBrushSize-(int)(mBrushSize*mHollowLineThickness/100));
    }

    public void setHollowMode(boolean hollowMode) {
        mHollowMode = hollowMode;
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
//                    mDrawPaintHollow.setStrokeWidth(mBrushSize-((int)(mBrushSize)*mHollowLineThickness/100));
//                }
//            });
//
//            return;
//        }
//        super.onRestoreInstanceState(state);
//    }
}
