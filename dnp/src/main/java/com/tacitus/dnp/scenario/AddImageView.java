package com.tacitus.dnp.scenario;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.tacitus.dnp.ScenarioChooser;

public class AddImageView extends ImageView {

    private Context mContext;

    public AddImageView(Context context) {
        super(context);
        mContext = context;
        super.setBackground(getResources().getDrawable(android.R.drawable.ic_menu_add));
        setupImageView();
        invalidate();
    }


    private void setupImageView() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setMinimumWidth((int) (metrics.widthPixels / 5.25));
        setMinimumHeight(metrics.heightPixels / 10);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ScenarioChooser)mContext).addNextItem();
            }
        });

    }
}
