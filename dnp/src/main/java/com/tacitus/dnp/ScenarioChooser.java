package com.tacitus.dnp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridLayout;

import com.tacitus.dnp.scenario.AddImageView;
import com.tacitus.dnp.scenario.SoundImageView;

public class ScenarioChooser extends Activity {
    private GridLayout mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scenario_chooser);

        mContentView = (GridLayout) findViewById(R.id.vertical_layout);
        mContentView.setUseDefaultMargins(true);
        mContentView.addView(new AddImageView(this));


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scenario_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.new_btn:
                addNextItem();
                return true;
            case R.id.ok_btn:
                Intent intent = new Intent();
                Parcelable[] parcelables = new Parcelable[mContentView.getChildCount()];
                for (int i=0; i<mContentView.getChildCount() - 1; i++) {
                    parcelables[i] = ((SoundImageView)mContentView.getChildAt(i)).getSound();
                }
                intent.putExtra("sounds", parcelables);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addNextItem() {
        SoundImageView soundImageView = new SoundImageView(this);
        mContentView.addView(soundImageView, mContentView.getChildCount() - 1);
        soundImageView.setText(String.valueOf(mContentView.getChildCount()));
        mContentView.invalidate();
    }

}
