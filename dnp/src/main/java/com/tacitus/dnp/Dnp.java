package com.tacitus.dnp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tacitus.dnp.widget.DrawingView;

import junit.framework.Assert;

import java.util.UUID;

public class Dnp extends Activity implements View.OnClickListener {

    private DrawingView mDrawView;
    private SeekBar mBrushSizeChooser;
    private TextView mBrushSizeChooserText;
    private TextView mBrushSizeChooserTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnp);

        // Used to set height of color chooser dynamically
        RadioGroup radioGroupColor = (RadioGroup) findViewById(R.id.toggleGroup);
        RelativeLayout colorChooser = (RelativeLayout) findViewById(R.id.color_chooser);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        colorChooser.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.AT_MOST));
        int realWidth = colorChooser.getMeasuredWidth();

        ViewGroup.LayoutParams layoutParams = colorChooser.getLayoutParams();
        Assert.assertNotNull(layoutParams);
        layoutParams.height = realWidth / radioGroupColor.getChildCount();
        colorChooser.setLayoutParams(layoutParams);
        colorChooser.invalidate();
        //----------------------------------------

        // Initialize the drawer menu
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set the drawer toggle as the DrawerListener:
        drawerLayout.setDrawerListener(mDrawerToggle);

        // Disable swipe gesture to open and close the drawer:
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ActionBar actionBar = getActionBar();
        Assert.assertNotNull(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //----------------------------------------

        mDrawView = (DrawingView)findViewById(R.id.drawing);

        ImageButton newBtn = (ImageButton) findViewById(R.id.new_btn);
        ImageButton saveBtn = (ImageButton) findViewById(R.id.save_btn);

        newBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        mBrushSizeChooserText = (TextView)findViewById(R.id.brush_size_chooser_text);
        mBrushSizeChooserTitle = (TextView)findViewById(R.id.brush_size_chooser_title);
        mBrushSizeChooserText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        mBrushSizeChooserTitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
        mBrushSizeChooser = (SeekBar)findViewById(R.id.brush_size_chooser);
        mBrushSizeChooser.setEnabled(false);
        mBrushSizeChooser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDrawView.setBrushSize(progress * 2);
                mBrushSizeChooserText.setText(String.valueOf(progress));
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

     public void eraseClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mDrawView.setEraseMode(true);
        } else {
            mDrawView.setEraseMode(false);
        }
    }

    public void touchSizeModeClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mDrawView.setTouchSizeMode(true);
            mBrushSizeChooser.setEnabled(false);
            mBrushSizeChooserText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mBrushSizeChooserTitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            mDrawView.setTouchSizeMode(false);
            mBrushSizeChooser.setEnabled(true);
            mBrushSizeChooserText.setTextColor(getResources().getColor(android.R.color.black));
            mBrushSizeChooserTitle.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.new_btn){
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle(R.string.new_dialog_title);
            newDialog.setMessage(R.string.new_dialog_message);
            newDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mDrawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle(R.string.save_dialog_title);
            saveDialog.setMessage(R.string.save_dialog_message);
            saveDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    mDrawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), mDrawView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "drawing");
                    Context applicationContext = getApplicationContext();
                    Assert.assertNotNull(applicationContext);
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(applicationContext,
                                R.string.save_dialog_ok, Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(applicationContext,
                                R.string.save_dialog_ko, Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    mDrawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}