package com.tacitus.dnp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.tacitus.dnp.scenario.Step;
import com.tacitus.dnp.widget.DnpColor;
import com.tacitus.dnp.widget.SizerDialog;

import junit.framework.Assert;

import java.util.ArrayList;

public class Dnp extends Activity implements View.OnClickListener {

    public static final String MULTIPLIER_NAME = "multiplier";
    public static final String CONFIG_FILE = "config";

    private SeekBar mBrushSizeChooserOldTablet;
    private TextView mBrushSizeChooserTextOldTablet;
    private TextView mBrushSizeChooserTitleOldTablet;
    private SeekBar mBrushSizeChooser;
    private TextView mBrushSizeChooserText;
    private TextView mBrushSizeChooserTitle;
    private SeekBar mAlphaChooser;
    private TextView mAlphaChooserText;
    private TextView mAlphaChooserTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private int RESULT_SCENARIO_CHOOSER = 2;
    private Button mDrawButton;
    private Button mScenarioButton;
    private Button mSizerButton;
    private ArrayList<Step> mSteps;
    private SizerDialog mSizerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DnpColor(this);
        mSteps = new ArrayList<Step>();

        setContentView(R.layout.activity_dnp);

        Path.createPublicAppPath();

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

        mDrawButton = (Button) findViewById(R.id.draw_button);
        mDrawButton.setOnClickListener(this);
        mScenarioButton = (Button) findViewById(R.id.scenario_button);
        mScenarioButton.setOnClickListener(this);
        mSizerButton = (Button) findViewById(R.id.sizer_button);
        mSizerButton.setOnClickListener(this);

        mBrushSizeChooserText = (TextView)findViewById(R.id.brush_size_chooser_text);
        mBrushSizeChooserTitle = (TextView)findViewById(R.id.brush_size_chooser_title);
        mBrushSizeChooserText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        mBrushSizeChooserTitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
        mBrushSizeChooser = (SeekBar)findViewById(R.id.brush_size_chooser);
        mBrushSizeChooser.setEnabled(false);
        mBrushSizeChooser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("setBrushSize", progress * 2);
//                mDrawView.setBrushSize(progress * 2);
                mBrushSizeChooserText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBrushSizeChooser.setProgress(getResources().getInteger(R.integer.initial_size));

        mBrushSizeChooserTextOldTablet = (TextView)findViewById(R.id.brush_size_chooser_text_old_tablet);
        mBrushSizeChooserTitleOldTablet = (TextView)findViewById(R.id.brush_size_chooser_title_old_tablet);
        mBrushSizeChooserTextOldTablet.setTextColor(getResources().getColor(android.R.color.darker_gray));
        mBrushSizeChooserTitleOldTablet.setTextColor(getResources().getColor(android.R.color.darker_gray));
        mBrushSizeChooserOldTablet = (SeekBar)findViewById(R.id.brush_size_chooser_old_tablet);
        mBrushSizeChooserOldTablet.setEnabled(false);
        mBrushSizeChooserOldTablet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("setBrushSizeOldTablet", progress);
//                mDrawView.setBrushSizeOldTablet(progress);
                mBrushSizeChooserTextOldTablet.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBrushSizeChooserOldTablet.setProgress(getResources().getInteger(R.integer.initial_size_old_tablet));


        mAlphaChooserText = (TextView)findViewById(R.id.alpha_chooser_text);
        mAlphaChooserTitle = (TextView)findViewById(R.id.alpha_chooser_title);
        mAlphaChooser = (SeekBar)findViewById(R.id.alpha_chooser);
        mAlphaChooser.setEnabled(true);
        mAlphaChooser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("setPaintAlpha", progress);
//                mDrawView.setPaintAlpha(progress);
                mAlphaChooserText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mAlphaChooser.setProgress(getResources().getInteger(R.integer.initial_alpha));


        mSizerDialog = new SizerDialog(this);

    }

    public void underlineClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d("setUnderlineMode", true);
//            mDrawView.setUnderlineMode(true);
        } else {
            Log.d("setUnderlineMode", false);
//            mDrawView.setUnderlineMode(false);
        }
    }

    public void hollowClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d("setHollowMode", true);
//            mDrawView.setHollowMode(true);
        } else {
            Log.d("setHollowMode", false);
//            mDrawView.setHollowMode(false);
        }
    }

     public void eraseClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d("setEraseMode", true);
//            mDrawView.setEraseMode(true);
        } else {
            Log.d("setEraseMode", false);
//            mDrawView.setEraseMode(false);
        }
    }

    public void touchSizeModeClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d("setTouchSizeMode", true);
//            mDrawView.setTouchSizeMode(true);
            mBrushSizeChooser.setEnabled(false);
            mBrushSizeChooserText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mBrushSizeChooserTitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            Log.d("setTouchSizeMode", false);
//            mDrawView.setTouchSizeMode(false);
            mBrushSizeChooser.setEnabled(true);
            mBrushSizeChooserText.setTextColor(getResources().getColor(android.R.color.black));
            mBrushSizeChooserTitle.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    public void pressureModeClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d("setPressureMode", true);
//            mDrawView.setPressureMode(true);
            mAlphaChooser.setEnabled(false);
            mAlphaChooserText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mAlphaChooserTitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            Log.d("setPressureMode", false);
//            mDrawView.setPressureMode(false);
            mAlphaChooser.setEnabled(true);
            mAlphaChooserText.setTextColor(getResources().getColor(android.R.color.black));
            mAlphaChooserTitle.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    public void oldTabletModeClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d("setOldTabletMode", true);
//            mDrawView.setOldTabletMode(true);
            mBrushSizeChooserOldTablet.setEnabled(true);
            mBrushSizeChooserTextOldTablet.setTextColor(getResources().getColor(android.R.color.black));
            mBrushSizeChooserTitleOldTablet.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            Log.d("setOldTabletMode", false);
//            mDrawView.setOldTabletMode(false);
            mBrushSizeChooserOldTablet.setEnabled(false);
            mBrushSizeChooserTextOldTablet.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mBrushSizeChooserTitleOldTablet.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public void onClick(View view){
        if (view == mDrawButton) {
            startDrawing();
        } else if (view == mScenarioButton){
            chooseScenario();
        } else if (view == mSizerButton){
            mSizerDialog.show();
        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    private void addStepsToIntent(Intent intent) {
        Parcelable[] parcelables = new Parcelable[mSteps.size()];
        for (int i=0; i<mSteps.size(); i++) {
            parcelables[i] = mSteps.get(i);
        }
        intent.putExtra("steps", parcelables);
    }

    private void chooseScenario() {
        Intent intent = new Intent(this, ScenarioChooser.class);
        addStepsToIntent(intent);
        startActivityForResult(intent, RESULT_SCENARIO_CHOOSER);
    }

    private void startDrawing() {
        Intent intent = new Intent(this, DrawingZone.class);
        addStepsToIntent(intent);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SCENARIO_CHOOSER && resultCode == RESULT_OK && data != null) {
            mSteps.clear();
            Parcelable[] parcelables = data.getExtras().getParcelableArray("steps");
            for (int i = 0; i < parcelables.length; i++) {
                mSteps.add((Step) parcelables[i]);
            }
        }
    }

}