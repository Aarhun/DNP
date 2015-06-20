package com.tacitus.dnp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tacitus.dnp.scenario.Step;
import com.tacitus.dnp.scenario.StepAdapter;
import com.tacitus.dnp.scenario.StepView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class ScenarioChooser extends Activity {
    private static final int FILE_SELECT_CODE = 0;

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


        Intent data = getIntent();
        if (data.getExtras() != null) {
            Parcelable[] parcelables = data.getExtras().getParcelableArray("steps");
            for (int i = 0; i < parcelables.length; i++) {
                mDataSet.add(((Step) parcelables[i]));
            }
            mAdapter.notifyDataSetChanged();
        }
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
    public void onBackPressed(){
        saveAndSwitch();
    }

    private void saveAndSwitch() {
        Intent intent = getIntent();
        Parcelable[] parcelables = new Parcelable[mContentView.getChildCount()];
        for (int i=0; i<mContentView.getChildCount(); i++) {
            parcelables[i] = ((StepView)mContentView.getChildAt(i)).getStep();
        }
        intent.putExtra("steps", parcelables);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.ok_btn:
                saveAndSwitch();
                return true;

            case R.id.load_btn:
                loadScenario();
                return true;

            case R.id.save_btn:
                saveScenario();
                return true;

            case R.id.new_btn:
                resetScenario();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String syllablesToString(){
        StringBuilder syllablesString = new StringBuilder();
        for (int i=0; i<mContentView.getChildCount(); i++) {
            syllablesString.append(((StepView) mContentView.getChildAt(i)).getStep().getSound().getText());
            if (i != mContentView.getChildCount() - 1) {
                syllablesString.append("-");
            }
        }
        return syllablesString.toString();
    }

    private void saveScenario() {
        final EditText input = new EditText(this);
        input.setText(syllablesToString());
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle(getResources().getString(R.string.scenario_save));
        saveDialog.setMessage(getResources().getString(R.string.scenario_save_dialog_instruction));
        saveDialog.setView(input);
        saveDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String fileTitle = input.getText().toString();
                        String fileName = fileTitle + ".snr";
                        String path = Path.getPublicAppPath();
                        File scenario = new File(path, fileName);
                        try {
                            FileWriter fileWriter = new FileWriter(scenario, false);
                            Gson gson = new Gson();
                            for (int i = 0; i < mContentView.getChildCount(); i++) {
                                Step step = ((StepView) mContentView.getChildAt(i)).getStep();
                                String json = gson.toJson(step);
                                fileWriter.write(json + "\n");
                            }
                            fileWriter.flush();
                            fileWriter.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

        );
        saveDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        });
        saveDialog.show();
    }

    private void loadScenario() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Path.getPublicAppPath());
        intent.setDataAndType(uri, "text/snr");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    mDataSet.clear();
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    File path = new File(uri.getPath());
                    // Parse content from JSon
                    Gson gson = new Gson();
                    try {
                        FileReader fileReader = new FileReader(path);
                        BufferedReader br = new BufferedReader(fileReader);
                        String line;
                        while((line = br.readLine()) != null) {
                            Step step = gson.fromJson(line, Step.class);
                            mDataSet.add(step);
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void resetScenario() {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle(R.string.scenario_new);
        newDialog.setMessage(R.string.scenario_new_dialog_message);
        newDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDataSet.clear();
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();

    }

    public void addNextItem() {
        Step step = new Step();
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
