package com.tacitus.dnp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.tacitus.dnp.scenario.Step;
import com.tacitus.dnp.scenario.StepAdapter;
import com.tacitus.dnp.scenario.StepView;

import java.util.ArrayList;


public class ScenarioChooser extends Activity {
    private RecyclerView mContentView;
    private StepAdapter mAdapter;
    private ArrayList<Step> mDataSet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scenario_chooser);

        mContentView = (RecyclerView) findViewById(R.id.recycler_view);
        mContentView.setHasFixedSize(true);

        com.gc.materialdesign.views.ButtonFloat buttonFloat = (com.gc.materialdesign.views.ButtonFloat) findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNextItem();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mContentView.setLayoutManager(layoutManager);

        mDataSet = new ArrayList<Step>();

        mAdapter = new StepAdapter(mDataSet);
        mContentView.setAdapter(mAdapter);
        mContentView.setItemAnimator(new DefaultItemAnimator());

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
            case R.id.ok_btn:
                Intent intent = new Intent();
                Parcelable[] parcelables = new Parcelable[mContentView.getChildCount()];
                for (int i=0; i<mContentView.getChildCount(); i++) {
                    parcelables[i] = ((StepView)mContentView.getChildAt(i)).getStep();
                }
                intent.putExtra("Steps", parcelables);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addNextItem() {
        Step step = new Step(String.valueOf(mContentView.getChildCount()));
        mDataSet.add(step);
        mAdapter.notifyItemInserted(mAdapter.getItemCount());
    }

    public void addItemAt(Step step, int index){
        mDataSet.add(index, step);
        mAdapter.notifyItemInserted(index);
    }

    public void deleteItem(Step step) {
        int position = mDataSet.indexOf(step);
        mDataSet.remove(step);
        mAdapter.notifyItemRemoved(position);
    }

}
