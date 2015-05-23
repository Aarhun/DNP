package com.tacitus.dnp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tacitus.dnp.scenario.Step;
import com.tacitus.dnp.widget.DrawingView;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class DrawingZone extends Activity implements View.OnClickListener {

    private DrawingView mDrawView;
    private int RESULT_LOAD_IMAGE = 1;
    private int RESULT_SCENARIO_CHOOSER = 2;
    private Button mPrevButton;
    private Button mNextButton;
    private TextView mTitle;
    private ArrayList<Step> mSteps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing_zone);

        ActionBar actionBar = getActionBar();
        Assert.assertNotNull(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //----------------------------------------

        mSteps = new ArrayList<Step>();
        Intent data = getIntent();
        if (data.getExtras() != null) {
            Parcelable[] parcelables = data.getExtras().getParcelableArray("steps");
            for (int i = 0; i < parcelables.length; i++) {
                mSteps.add(((Step) parcelables[i]));
            }
        }

        mDrawView = (DrawingView)findViewById(R.id.drawing);
        mDrawView.setScenario(mSteps);

        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(this);
        mPrevButton.setEnabled(false);
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(this);
        if(mSteps.isEmpty()){
            mNextButton.setEnabled(false);
        }

        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setTextColor(getResources().getColor(android.R.color.white));
        updateTitle();

    }



    @Override
    public void onClick(View view){
        if (view == mPrevButton) {
            mNextButton.setEnabled(true);
            mDrawView.previousStep();
            if (mDrawView.getCurrentStepCursor() == 0) {
                mPrevButton.setEnabled(false);
            }
            updateTitle();
        } else if (view == mNextButton){
            mPrevButton.setEnabled(true);
            mDrawView.nextStep();
            if (mDrawView.getCurrentStepCursor() == mSteps.size() - 1){
                mNextButton.setEnabled(false);
            }
            updateTitle();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    if (bitmap != null) {
                        mDrawView.loadImage(bitmap);
                    }
                }
            }
        }
        if (requestCode == RESULT_SCENARIO_CHOOSER && resultCode == RESULT_OK && data != null) {
            mSteps.clear();
            Parcelable[] parcelables = data.getExtras().getParcelableArray("steps");
            for(int i=0;i<parcelables.length;i++){
                mSteps.add((Step) parcelables[i]);
            }
            mDrawView.setScenario(mSteps);
            if(!mSteps.isEmpty()) {
                mNextButton.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.new_btn:
                newDrawing();
                return true;
            case R.id.save_btn:
                saveDrawing();
                return true;
            case R.id.load_btn:
                loadDrawing();
                return true;
            case R.id.choose_scenario_btn:
                chooseScenario();
                return true;
            case R.id.undo_btn:
                mDrawView.undo();
                return true;
            case R.id.redo_btn:
                mDrawView.redo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseScenario() {
        Intent intent = new Intent(this, ScenarioChooser.class);
        Parcelable[] parcelables = new Parcelable[mSteps.size()];
        for (int i=0; i<mSteps.size(); i++) {
            parcelables[i] = mSteps.get(i);
        }
        intent.putExtra("steps", parcelables);
        startActivityForResult(intent, RESULT_SCENARIO_CHOOSER);
    }

    private void loadDrawing() {
        //load drawing
        AlertDialog.Builder loadDialog = new AlertDialog.Builder(this);
        loadDialog.setTitle(R.string.load_dialog_title);
        loadDialog.setMessage(R.string.load_dialog_message);
        loadDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, RESULT_LOAD_IMAGE);

            }
        });
        loadDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        loadDialog.show();
    }

    private void saveDrawing() {
        //save drawing
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle(R.string.save_dialog_title);
        saveDialog.setMessage(R.string.save_dialog_message);
        saveDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                mDrawView.setDrawingCacheEnabled(true);

                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "Pictures";
                String fileTitle = UUID.randomUUID().toString();
                String fileName = fileTitle + ".png";
                File image = new File(dirPath, fileName);
                Context applicationContext = getApplicationContext();
                Assert.assertNotNull(applicationContext);
                Bitmap drawingCache = mDrawView.getDrawingCache();
                Assert.assertNotNull(drawingCache);
                boolean error = false;
                try {
                    // Ensure the file has an unique name:
                    while (!image.createNewFile()) {
                        fileTitle = UUID.randomUUID().toString();
                        fileName = fileTitle + ".png";
                        image = new File(dirPath, fileName);
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(image);
                    drawingCache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    // Update content database:
                    ContentValues values = new ContentValues(7);

                    values.put(MediaStore.Images.Media.TITLE, "DNP-drawing-" + fileTitle);
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.ORIENTATION, 0);
                    values.put(MediaStore.Images.Media.DATA, image.getAbsolutePath());
                    values.put(MediaStore.Images.Media.SIZE, image.length());

                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }

                if (error) {
                    Toast unsavedToast = Toast.makeText(applicationContext,
                            R.string.save_dialog_ko, Toast.LENGTH_SHORT);
                    unsavedToast.show();
                } else {
                    Toast savedToast = Toast.makeText(applicationContext,
                            R.string.save_dialog_ok, Toast.LENGTH_SHORT);
                    savedToast.show();
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

    private void newDrawing() {
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

    }

    private void updateTitle() {
        int currentStep = mDrawView.getCurrentStepCursor() + 1;
        mTitle.setText(getResources().getString(R.string.step_title) + " " + currentStep);
    }
}