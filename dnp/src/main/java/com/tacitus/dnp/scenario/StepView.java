package com.tacitus.dnp.scenario;

import android.content.ClipData;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
    private CheckBox mLinkedDown;
    private TextView mTextLinkedDown;

    public StepView(Context context, AttributeSet attrs) {
        super(context, attrs);
		setUseCompatPadding(true);
        mContext = context;
        mStep = new Step();
    }


    public String getText() {
        return mStep.getText();
    }

    public Step getStep() {
        return mStep;
    }

    public void setStep(Step step) {
        this.mStep = step;
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.step_color_indicator);
        drawable.setColorFilter(step.getLine().getColor(), PorterDuff.Mode.MULTIPLY);
        mChooseColor.setBackground(drawable);
        mHollowMode.setChecked(step.getLine().isHollow());
        mUnderlineMode.setChecked(step.getLine().isUnderline());
        mText.setText(step.getText());
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

    public void setupImageView() {
		mHollowMode = (CheckBox) findViewById(R.id.hollow);
		mUnderlineMode = (CheckBox) findViewById(R.id.underline);
		mLinkedDown = (CheckBox) findViewById(R.id.link_down);
		mText = (EditText) findViewById(R.id.soundText);
        mChooseColor = (ImageView) findViewById(R.id.chooseColor);
        mTextLinkedDown = (TextView) findViewById(R.id.text_link_down);
        mTextLinkedDown.setGravity(Gravity.CENTER);

		ImageView deleteButton = (ImageView) findViewById(R.id.delete);
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

        mHollowMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStep.getLine().setHollow(isChecked);
            }
        });


        mUnderlineMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStep.getLine().setUnderline(isChecked);
            }
        });

        mLinkedDown.setOnCheckedChangeListener(new android.widget.CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStep.setLinkedDown(isChecked);
                if (isChecked) {
                    mTextLinkedDown.setVisibility(VISIBLE);
                    mTextLinkedDown.setText(getResources().getText(R.string.step_text_linked_down));
                } else {
                    mTextLinkedDown.setVisibility(INVISIBLE);
                }
            }
        });

        mColorChooserDialog = new SimpleColorChooserDialog(mContext, this);

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
