package com.tacitus.dnp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tacitus.dnp.widget.DrawingView;

public class Dnp extends Activity implements View.OnClickListener {

    private DrawingView mDrawView;
    private ImageButton mNewBtn;
    private SeekBar mBrushSizeChooser;
    private TextView mBrushSizeChooserText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnp);
        mDrawView = (DrawingView)findViewById(R.id.drawing);

        mNewBtn = (ImageButton)findViewById(R.id.new_btn);

        mNewBtn.setOnClickListener(this);

        mBrushSizeChooserText = (TextView)findViewById(R.id.brush_size_chooser_text);
        mBrushSizeChooser = (SeekBar)findViewById(R.id.brush_size_chooser);
        mBrushSizeChooser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDrawView.setBrushSize(progress);
                mBrushSizeChooserText.setText(getResources().getText(R.string.brush_size) + " " + progress);
//                ViewGroup.LayoutParams layoutParams = mColorChooser.getLayoutParams();
//                if (layoutParams != null) {
//                    float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                            progress, getResources().getDisplayMetrics());
//                    layoutParams.height = (int) size;
//                    layoutParams.width = (int) size;
//                    mColorChooser.setLayoutParams(layoutParams);
//                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBrushSizeChooser.setProgress((int) getResources().getDimension(R.dimen.initial_size));

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
        if(view.getId()==R.id.new_btn){
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