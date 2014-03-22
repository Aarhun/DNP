package com.tacitus.dnp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.tacitus.dnp.widget.DrawingView;

public class Dnp extends Activity implements View.OnClickListener {

    private DrawingView mDrawView;
    private ImageButton mCurrPaint;
    private ImageButton mDrawBtn;
    private float mSmallBrush;
    private float mMediumBrush;
    private float mLargeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnp);
        mDrawView = (DrawingView)findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        mCurrPaint = (ImageButton)paintLayout.getChildAt(0);
        mCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        mSmallBrush = getResources().getInteger(R.integer.small_size);
        mMediumBrush = getResources().getInteger(R.integer.medium_size);
        mLargeBrush = getResources().getInteger(R.integer.large_size);

        mDrawBtn = (ImageButton)findViewById(R.id.draw_btn);

        mDrawBtn.setOnClickListener(this);

        mDrawView.setBrushSize(mLargeBrush);
    }

    public void paintClicked(View view){
        //use chosen color
        if (view != mCurrPaint) {
            //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            mDrawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            mCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            mCurrPaint =(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view){
    //respond to clicks
        if(view.getId()==R.id.draw_btn){
            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.large_brush) * 2);
            layoutParams.weight = 1;
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setLayoutParams(layoutParams);

            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawView.setBrushSize(mSmallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setLayoutParams(layoutParams);
            mediumBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mDrawView.setBrushSize(mMediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setLayoutParams(layoutParams);
            largeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mDrawView.setBrushSize(mLargeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();


        }
    }

    @Override
    protected void onSaveInstanceState(android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("PAINT_BUTTON", mCurrPaint.getId());


    }

    @Override
    protected void onRestoreInstanceState(final android.os.Bundle savedInstanceState) {
        paintClicked(findViewById(savedInstanceState.getInt("PAINT_BUTTON")));
        super.onRestoreInstanceState(savedInstanceState);
    }

}