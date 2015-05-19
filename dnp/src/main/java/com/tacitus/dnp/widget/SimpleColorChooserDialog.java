package com.tacitus.dnp.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.tacitus.dnp.R;

import junit.framework.Assert;

public class SimpleColorChooserDialog extends Dialog implements View.OnClickListener {

    public interface SimpleColorChooser {

        void onChangeColor(int color);
    }

    private SimpleColorChooser mListener;

    public SimpleColorChooserDialog(Context context, SimpleColorChooser listener) {
        super(context);
        mListener = listener;
        final View dialogView = getLayoutInflater().inflate(R.layout.simple_color_chooser, null);
        final View chooserLayout = dialogView.findViewById(R.id.color_chooser_layout);

        Assert.assertNotNull(dialogView);
        setContentView(dialogView);
        setTitle(R.string.color_chooser_dialog_title);
        //        setMessage(R.string.color_chooser_dialog_message);
        final ImageView imageView = (ImageView) dialogView.findViewById(R.id.color_chooser);
        Assert.assertNotNull(imageView);

        chooserLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchX = MotionEventCompat.getX(event, MotionEventCompat.getActionIndex(event));
                float touchY = MotionEventCompat.getY(event, MotionEventCompat.getActionIndex(event));

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
                            // Limit possible color for dnp sun:
                            switch (color) {
                                case DnpColor.DARK_GREEN:
                                case DnpColor.GREEN:
                                case DnpColor.LIGHT_GREEN:
                                case DnpColor.RED:
                                case DnpColor.LIGHT_BROWN:
                                case DnpColor.DARK_BROWN:
                                case DnpColor.BLUE:
                                case DnpColor.YELLOW:
                                case DnpColor.GREY:
                                    mListener.onChangeColor(color);
                                    dismiss();
                                    break;
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
    }


    @Override
    public void show() {
        super.show();
    }


}
