package com.tacitus.dnp.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.tacitus.dnp.R;

import junit.framework.Assert;

public class ColorChooserDialog extends Dialog implements View.OnClickListener {

    public interface ColorChooser {

        void onChangeColor(String stringColor, int id);
        void onClearColor(int id);
    }

    private Integer mCurrentColorNumber = 0;
    private ColorChooser mListener;
    private Button mPositiveButton;
    private Button mClearButton;
    private ToggleButton mTypeButton;
    private ImageView mColorChooserBackground;
    private ImageView mColorChooser;
    private boolean mIsDnp = false;

    public ColorChooserDialog(Context context, ColorChooser listener) {
        super(context);
        mListener = listener;
        final View dialogView = getLayoutInflater().inflate(R.layout.color_chooser, null);
        final View chooserLayout = dialogView.findViewById(R.id.color_chooser_layout);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.color_selector);

        mColorChooserBackground = (ImageView) dialogView.findViewById(R.id.color_chooser_background);
        mColorChooser = (ImageView) dialogView.findViewById(R.id.color_chooser);

        mColorChooserBackground.setImageResource(R.drawable.standard_color_chooser);
        mColorChooser.setImageResource(R.drawable.standard_color_chooser);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                Assert.assertNotNull(radioButton);
                mCurrentColorNumber = Integer.valueOf(radioButton.getTag().toString());
            }
        });
        Assert.assertNotNull(dialogView);
        setContentView(dialogView);
        setTitle(R.string.color_chooser_dialog_title);
        //        setMessage(R.string.color_chooser_dialog_message);
        final ImageView imageView = (ImageView) dialogView.findViewById(R.id.color_chooser);
        Assert.assertNotNull(imageView);
        mPositiveButton = (Button) dialogView.findViewById(R.id.positive_button);
        mPositiveButton.setText(context.getText(android.R.string.ok));
        mPositiveButton.setOnClickListener(this);

        mClearButton = (Button) dialogView.findViewById(R.id.clear_button);
        mClearButton.setText(context.getText(R.string.clear_color));
        mClearButton.setOnClickListener(this);

        mTypeButton = (ToggleButton) dialogView.findViewById(R.id.type_button);
        mTypeButton.setOnClickListener(this);


        chooserLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchX = MotionEventCompat.getX(event, MotionEventCompat.getActionIndex(event));
                float touchY = MotionEventCompat.getY(event, MotionEventCompat.getActionIndex(event));

                // Index of multiple touch event:
                int index = MotionEventCompat.getActionIndex(event);
                // Id of multiple touch event
                int id = MotionEventCompat.getPointerId(event, index);

                switch (MotionEventCompat.getActionMasked(event)) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN:
                        imageView.setDrawingCacheEnabled(true);
                        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
                        imageView.setDrawingCacheEnabled(false);
                        try {
                            int pixel = bitmap.getPixel((int) touchX, (int) touchY);
                            bitmap.recycle();
                            int color = Color.argb(255, Color.red(pixel), Color.green(pixel), Color.blue(pixel));
                            String stringColor = String.format("#%06X", 0xFFFFFF & color);
                            // Limit possible color for dnp sun:
                            if (mIsDnp) {
                                switch (color) {
                                    case -14391260:
                                        // Dark green
                                    case -13463502:
                                        // Green
                                    case -12137658:
                                        // Light green
                                    case -2675411:
                                        // Red
                                    case -3837106:
                                        // Light brown
                                    case -7515592:
                                        // Dark brown
                                    case -13156710:
                                        // Blue
                                    case -465067:
                                        // Yellow
                                    case -4079167:
                                        // Grey
                                        setIndicatorBackgroundColor(color, mCurrentColorNumber);
                                        mListener.onChangeColor(stringColor, mCurrentColorNumber);
                                        break;
                                }
                            } else {
                                setIndicatorBackgroundColor(color, mCurrentColorNumber);
                                mListener.onChangeColor(stringColor, mCurrentColorNumber);
                            }
                        } catch (IllegalArgumentException e) {
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP:
                        break;

                    default:
                        // Do not consume other events.
                        return false;
                }
                // Consume handled event.
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == mPositiveButton) {
            dismiss();
        } else if (v == mClearButton) {
            setIndicatorBackgroundColor(getContext().getResources().getColor(android.R.color.background_light), mCurrentColorNumber);
            mListener.onClearColor(mCurrentColorNumber);
        } else if (v == mTypeButton) {
            boolean on = ((ToggleButton) v).isChecked();
            if (on) {
                mColorChooserBackground.setImageResource(R.drawable.dnp_sun_color_chooser_background);
                mColorChooser.setImageResource(R.drawable.dnp_sun_color_chooser);
                mIsDnp = true;
            } else {
                mColorChooserBackground.setImageResource(R.drawable.standard_color_chooser);
                mColorChooser.setImageResource(R.drawable.standard_color_chooser);
                mIsDnp = false;
            }
        }

    }


    @Override
    public void show() {
        super.show();
        // Used to set height of color indicator dynamically
        RelativeLayout indicatorLayout = (RelativeLayout) findViewById(R.id.color_indicator_layout);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        indicatorLayout.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.AT_MOST));
        int realWidth = indicatorLayout.getMeasuredWidth();

        ViewGroup.LayoutParams layoutParams = indicatorLayout.getLayoutParams();
        Assert.assertNotNull(layoutParams);
        layoutParams.height = realWidth / 10;
        indicatorLayout.setLayoutParams(layoutParams);
        indicatorLayout.invalidate();
        //----------------------------------------
    }

    private void setIndicatorBackgroundColor(int color, int id) {
        ImageView imageView = null;
        switch (id) {
            case 0:
                imageView = (ImageView) findViewById(R.id.color_indicator_0);
                break;
            case 1:
                imageView = (ImageView) findViewById(R.id.color_indicator_1);
                break;
            case 2:
                imageView = (ImageView) findViewById(R.id.color_indicator_2);
                break;
            case 3:
                imageView = (ImageView) findViewById(R.id.color_indicator_3);
                break;
            case 4:
                imageView = (ImageView) findViewById(R.id.color_indicator_4);
                break;
        }
        if (imageView != null) {
            imageView.setBackgroundColor(color);
            imageView.invalidate();
        }
    }
}
