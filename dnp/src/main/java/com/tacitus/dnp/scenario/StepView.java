package com.tacitus.dnp.scenario;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tacitus.dnp.R;
import com.tacitus.dnp.ScenarioChooser;
import com.tacitus.dnp.widget.SimpleColorChooserDialog;

public class StepView extends CardView implements SimpleColorChooserDialog.SimpleColorChooser {

    private Context mContext;
    private Step mStep;
    private CheckBox mHollowMode;
    private CheckBox mUnderlineMode;
    private EditText mText;
    private SimpleColorChooserDialog mColorChooserDialog;
    private ImageView mChooseColor;
    private TextView mTitle;
    private CheckBox mLinkedDown;

    public StepView(Context context, int stepNumber) {
        super(context);
        mContext = context;
        mStep = new Step();
        mTitle = new TextView(mContext);
        mTitle.setText(getResources().getString(R.string.step_title) + ": " + String.valueOf(stepNumber));
        mTitle.setTextSize(20);
        LayoutParams layoutParamsTitle = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mTitle, layoutParamsTitle);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        LinearLayout linearLayout = (LinearLayout) ((Activity) mContext).getLayoutInflater().inflate(R.layout.step, null);
        mHollowMode = (CheckBox) linearLayout.findViewById(R.id.hollow);
        mUnderlineMode = (CheckBox) linearLayout.findViewById(R.id.underline);
        mLinkedDown = (CheckBox) linearLayout.findViewById(R.id.link_down);
        mText = (EditText) linearLayout.findViewById(R.id.soundText);
        mChooseColor = (ImageView) linearLayout.findViewById(R.id.chooseColor);

        Button deleteButton = (Button) linearLayout.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ScenarioChooser) mContext).deleteItem(mStep);
            }
        });

        mChooseColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorChooserDialog.show();
            }
        });



        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mStep.setText(s.toString());
            }
        });

        mHollowMode.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStep.getLine().setHollow(isChecked);
            }
        });


        mUnderlineMode.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStep.getLine().setUnderline(isChecked);
            }
        });

        mLinkedDown.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mStep.setLinkedDown(isChecked);
            }
        });


        mColorChooserDialog = new SimpleColorChooserDialog(mContext, this);


        addView(linearLayout);
        setupImageView();

        // Used to set height of color indicator dynamically

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) mContext).getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        this.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.EXACTLY));
        int realWidth = this.getMeasuredWidth();

        mText.setMinimumWidth(realWidth / 3);
        mChooseColor.setMinimumWidth(realWidth / 3);
        //----------------------------------------
        invalidate();
    }


    public String getText() {
        return mStep.getText();
    }

    public Step getStep() {
        return mStep;
    }

    public void setStep(Step step) {
        this.mStep = step;
        Drawable drawable = mChooseColor.getBackground();
        drawable.setColorFilter(step.getLine().getColor(), PorterDuff.Mode.MULTIPLY);
        mChooseColor.setBackground(drawable);
        mHollowMode.setChecked(step.getLine().isHollow());
        mUnderlineMode.setChecked(step.getLine().isUnderline());
        mText.setText(step.getText());
        mTitle.setText(step.getTitle());
        mLinkedDown.setChecked(step.getLinkedDown());
    }

    public void setColor(int color) {
        mStep.getLine().setColor(color);
        Drawable drawable = mChooseColor.getBackground();
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mChooseColor.setBackground(drawable);
    }

    public void setText(String text) {
        mStep.setText(text);
    }

    public void setHollow(boolean hollow) {
        mStep.getLine().setHollow(hollow);
    }

    public void setUnderline(boolean underline) {
        mStep.getLine().setUnderline(underline);
    }

    private void setupImageView() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setOnDragListener(new OnDragListener() {
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
                        RecyclerView parent = ((RecyclerView) v.getParent());
                        int currentIndex = parent.indexOfChild(v);
                        StepView stepImageView = (StepView) parent.getChildAt(Integer.valueOf((String) dragData));
                        Step step = stepImageView.getStep();
                        ((ScenarioChooser) mContext).deleteItem(step);
                        ((ScenarioChooser) mContext).addItemAt(step, currentIndex);
                        return true;
                    }
                    default: {
                        return false;
                    }

                }
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int index = ((RecyclerView) v.getParent()).indexOfChild(v);
                ClipData data = ClipData.newPlainText("index", String.valueOf(index));
                v.startDrag(data, new DragShadowBuilder(v), null, 0);
                return false;
            }
        });

    }


    @Override
    public void onChangeColor(int color) {
        setColor(color);
    }

}
