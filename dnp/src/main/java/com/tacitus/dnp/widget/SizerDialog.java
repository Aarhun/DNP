package com.tacitus.dnp.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tacitus.dnp.Dnp;
import com.tacitus.dnp.R;

import junit.framework.Assert;

public class SizerDialog extends Dialog implements View.OnClickListener {


    private Button mPositiveButton;
    private Button mClearButton;
    private Button mNegativeButton;
    private SizerView mSizerView;
    private SeekBar mSizeChooser;
    private Context mContext;

    public SizerDialog(final Context context) {
        super(context);
        mContext = context;
        final View dialogView = getLayoutInflater().inflate(R.layout.sizer_dialog, null);
        Assert.assertNotNull(dialogView);
        setContentView(dialogView);
        mSizeChooser = (SeekBar) findViewById(R.id.size_chooser);
        mSizerView = (SizerView) findViewById(R.id.size_view);

        setTitle(R.string.sizer_dialog_title);
        mPositiveButton = (Button) findViewById(R.id.positive_button);
        mPositiveButton.setText(context.getText(android.R.string.ok));
        mPositiveButton.setOnClickListener(this);

        mClearButton = (Button) findViewById(R.id.clear_button);
        mClearButton.setText(context.getText(R.string.reset));
        mClearButton.setOnClickListener(this);

        mNegativeButton = (Button) findViewById(R.id.cancel_button);
        mNegativeButton.setText(context.getText(android.R.string.cancel));
        mNegativeButton.setOnClickListener(this);
        // !!! Set the initial value BEFORE setting the listener !!!
        // (Avoid attempting to draw the circle)
        SharedPreferences settings = getContext().getSharedPreferences(Dnp.CONFIG_FILE, Context.MODE_PRIVATE);
        mSizeChooser.setProgress(settings.getInt(Dnp.MULTIPLIER_NAME, getContext().getResources().getInteger(R.integer.base_stroke_size_multiplier)) );
        mSizerView.setMultiplier(settings.getInt(Dnp.MULTIPLIER_NAME, getContext().getResources().getInteger(R.integer.base_stroke_size_multiplier)));

        mSizeChooser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSizerView.setMultiplier(progress);
                mSizerView.drawCircle();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView instructions = (TextView) findViewById(R.id.instructions);
        instructions.setText(context.getResources().getText(R.string.sizer_instructions));
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        if (v == mPositiveButton) {
            saveMultiplier();
            dismiss();
        } else if (v == mClearButton) {
            mSizeChooser.setProgress(mContext.getResources().getInteger(R.integer.base_stroke_size_multiplier));
            mSizerView.startNew();
        } else if (v == mNegativeButton) {
            dismiss();
        }

    }

    @Override
    public void show() {
        super.show();
        // Used to set height and width dynamically

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mSizerView.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.AT_MOST));
        int realWidth = mSizerView.getMeasuredWidth();

        ViewGroup.LayoutParams layoutParams = mSizerView.getLayoutParams();
        Assert.assertNotNull(layoutParams);
        layoutParams.height = realWidth / 3;
        layoutParams.width = realWidth / 3;
        mSizerView.setLayoutParams(layoutParams);
        mSizerView.invalidate();
        //----------------------------------------
    }

    private void saveMultiplier()
    {
        SharedPreferences settings = mContext.getSharedPreferences(Dnp.CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Dnp.MULTIPLIER_NAME, mSizerView.getMultiplier());
        editor.commit();
    }

}
