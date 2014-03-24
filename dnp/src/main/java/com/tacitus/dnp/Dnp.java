package com.tacitus.dnp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.tacitus.dnp.widget.DrawingView;

public class Dnp extends Activity implements View.OnClickListener {

    private DrawingView mDrawView;
    private ImageButton mDrawBtn;
    private ImageButton mNewBtn;
    private float mSmallBrush;
    private float mMediumBrush;
    private float mLargeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnp);
        mDrawView = (DrawingView)findViewById(R.id.drawing);

        mSmallBrush = getResources().getInteger(R.integer.small_size);
        mMediumBrush = getResources().getInteger(R.integer.medium_size);
        mLargeBrush = getResources().getInteger(R.integer.large_size);

        mDrawBtn = (ImageButton)findViewById(R.id.draw_btn);
        mNewBtn = (ImageButton)findViewById(R.id.new_btn);

        mDrawBtn.setOnClickListener(this);
        mNewBtn.setOnClickListener(this);

        mDrawView.setBrushSize(mLargeBrush);
    }

    public void paintClickedToggle(View view) {
        String color = view.getTag().toString();
        mDrawView.setColor(color);
    }


    public void hollowClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mDrawView.setHollowMode(true);
        } else {
            mDrawView.setHollowMode(false);
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


        } else if(view.getId()==R.id.new_btn){
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    mDrawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
    }

//    @Override
//    protected void onSaveInstanceState(android.os.Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("PAINT_BUTTON", mCurrPaint.getId());
//
//
//    }
//
//    @Override
//    protected void onRestoreInstanceState(final android.os.Bundle savedInstanceState) {
//        paintClicked(findViewById(savedInstanceState.getInt("PAINT_BUTTON")));
//        super.onRestoreInstanceState(savedInstanceState);
//    }

}