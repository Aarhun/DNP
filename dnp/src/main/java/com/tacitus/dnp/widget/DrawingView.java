package com.tacitus.dnp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.tacitus.dnp.R;

import java.util.ArrayList;

public class DrawingView extends View {



    //drawing path
    private Path mDrawPath;
    //drawing and canvas paint
    private Paint mDrawPaint;
    private Paint mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
    private ArrayList<EventHolder> mEventList = new ArrayList<EventHolder>();


    //canvas bitmap
    private Bitmap mCanvasBitmap;

    private float mBrushSize;

    private static class EventHolder implements Parcelable {
        int mEventType;
        float mX;
        float mY;
        int mColor;
        float mBrushSize;

        public EventHolder(int event, float x, float y, int color, float brushSize) {
            this.mEventType = event;
            this.mX = x;
            this.mY = y;
            this.mColor = color;
            this.mBrushSize = brushSize;
//            Log.e("ERROR", "Save event: " + event + " " + x + "/" + y + " " + color + " " + brushSize);

        }
        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(mEventType);
            out.writeFloat(mX);
            out.writeFloat(mY);
            out.writeInt(mColor);
            out.writeFloat(mBrushSize);
        }

    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
    //    get drawing area setup for interaction

        mDrawPath = new Path();
        mDrawPaint = new Paint();

        mDrawPaint.setColor(mPaintColor);

        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);

        mBrushSize = getResources().getInteger(R.integer.medium_size);

        mDrawPaint.setStrokeWidth(mBrushSize);
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
        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    private void onTouchEvent(EventHolder holder) {
//        Log.e("DEBUG", "Replay event: " + holder.mEventType + " " + holder.mX + "/" + holder.mY + " " + holder.mColor + " " + holder.mBrushSize);
        switch (holder.mEventType) {
            case MotionEvent.ACTION_DOWN:
                invalidate();
                mDrawPaint.setColor(holder.mColor);
                mDrawPaint.setStrokeWidth(holder.mBrushSize);
                mDrawPath.moveTo(holder.mX, holder.mY);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(holder.mX, holder.mY);
                break;
            case MotionEvent.ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                mDrawPath.reset();
                break;
            default:
                break;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mEventList.add(new EventHolder(event.getAction(), touchX, touchY, mPaintColor, mBrushSize));
                mDrawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mEventList.add(new EventHolder(event.getAction(), touchX, touchY, mPaintColor, mBrushSize));
                mDrawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mEventList.add(new EventHolder(event.getAction(), touchX, touchY, mPaintColor, mBrushSize));
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                mDrawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
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
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        mBrushSize =pixelAmount;
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("INSTANCE_STATE", super.onSaveInstanceState());
        bundle.putParcelableArrayList("EVENT_LIST", mEventList);
        bundle.putInt("PAINT_COLOR", mPaintColor);
        bundle.putFloat("BRUSH_SIZE", mBrushSize);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            final Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable("INSTANCE_STATE"));
            mEventList = bundle.getParcelableArrayList("EVENT_LIST");
            post(new Runnable() {
                @Override
                public void run() {
                    // Replay drawing when changing orientation
                    for (EventHolder holder : mEventList) {
                        onTouchEvent(holder);
                    }
                    mDrawPaint.setColor(bundle.getInt("PAINT_COLOR"));
                    mDrawPaint.setStrokeWidth(bundle.getFloat("BRUSH_SIZE"));
                }
            });

            return;
        }
        super.onRestoreInstanceState(state);
    }
}
