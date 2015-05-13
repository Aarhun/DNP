package com.tacitus.dnp.scenario;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.tacitus.dnp.R;

public class SoundImageView extends ImageView {

    private SoundDrawable mSoundDrawable;
    private Context mContext;
    private Sound mSound;

    public SoundImageView(Context context) {
        super(context);
        mContext = context;
        mSound = new Sound();
        mSoundDrawable = new SoundDrawable(mSound.getLine().getColor(), mSound.getText());
        super.setBackground(mSoundDrawable);
        setupImageView();
        invalidate();
    }

    public SoundImageView(Context context, int color, String text) {
        super(context);
        mContext = context;
        mSound = new Sound(color, text);
        mSoundDrawable = new SoundDrawable(mSound.getLine().getColor(), mSound.getText());
        super.setBackground(mSoundDrawable);
        setupImageView();
        invalidate();
    }

    public SoundImageView(Context context, String text) {
        super(context);
        mContext = context;
        mSound = new Sound(text);
        mSoundDrawable = new SoundDrawable(mSound.getLine().getColor(), mSound.getText());
        super.setBackground(mSoundDrawable);
        setupImageView();
        invalidate();
    }

    public String getText() {
        return mSound.getText();
    }

    public Line getLine() {
        return mSound.getLine();
    }

    public void setLine(Line line) {
        mSound.setLine(line);
        mSoundDrawable.changeColor(mSound.getLine().getColor());
    }

    public SoundDrawable getSoundDrawable() {
        return mSoundDrawable;
    }

    public void setSoundDrawable(SoundDrawable soundDrawable) {
        this.mSoundDrawable = soundDrawable;
        invalidate();
    }

    public Sound getSound() {
        return mSound;
    }

    public void setSound(Sound sound) {
        this.mSound = sound;
    }

    public void setColor(int color) {
        mSound.getLine().setColor(color);
        mSoundDrawable.changeColor(color);
    }

    public void setText(String text) {
        mSound.setText(text);
        mSoundDrawable.changeText(text);
    }

    private void setupImageView() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setMinimumWidth((int) (metrics.widthPixels / 5.25));
        setMinimumHeight(metrics.heightPixels / 10);

        setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED: {
                        return true;
                    }

                    case DragEvent.ACTION_DRAG_LOCATION: {
                        return true;
                    }

                    case DragEvent.ACTION_DROP: {
                        // Gets the item containing the dragged data
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        // insanity check
                        if (item == null)
                            return false;
                        // Gets the text data from the item.
                        CharSequence dragData = item.getText();
                        GridLayout parent = ((GridLayout) v.getParent());
                        int currentIndex = parent.indexOfChild(v);
                        SoundImageView soundImageView = (SoundImageView) parent.getChildAt(Integer.valueOf((String) dragData));
                        parent.removeView(soundImageView);
                        parent.addView(soundImageView, currentIndex);
                        parent.invalidate();
                        return true;
                    }
                    default: {
                        return false;
                    }

                }
            }
        });

        setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                int index = ((GridLayout)v.getParent()).indexOfChild(v);
                ClipData data = ClipData.newPlainText("index", String.valueOf(index));
                v.startDrag(data, new View.DragShadowBuilder(v), null, 0);
                return false;
            }
        });
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndShowPopMenu();
            }
        });

    }

    private void createAndShowPopMenu() {
        PopupMenu popupMenu = new PopupMenu(mContext, this);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int color = Integer.parseInt(item.getTitleCondensed().toString());
                setColor(color);
                return true;
            }
        });
        popupMenu.show();
    }
}
