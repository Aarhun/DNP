package com.tacitus.dnp.widget;

import android.content.Context;

import com.tacitus.dnp.R;

/**
 * Created by Aarhun on 27/08/2014.
 */
public class DnpColor {

    private static Context mContext;

    public DnpColor(Context context) {
        mContext = context;
    }
    static public final int DARK_GREEN = -14391260;
    static public final int GREEN = -13463502;
    static public final int LIGHT_GREEN = -12137658;
    static public final int RED = -2675411;
    static public final int LIGHT_BROWN = -3837106;
    static public final int DARK_BROWN = -7515592;
    static public final int BLUE = -13156710;
    static public final int YELLOW = -465067;
    static public final int GREY = -4079167;

    static public String toHexa(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }

    static public String toString(int color) {
        switch (color) {
            case DARK_GREEN:
                return mContext.getResources().getString(R.string.dark_green);
            case GREEN:
                return mContext.getResources().getString(R.string.green);
            case LIGHT_GREEN:
                return mContext.getResources().getString(R.string.light_green);
            case RED:
                return mContext.getResources().getString(R.string.red);
            case LIGHT_BROWN:
                return mContext.getResources().getString(R.string.light_brown);
            case DARK_BROWN:
                return mContext.getResources().getString(R.string.dark_brown);
            case BLUE:
                return mContext.getResources().getString(R.string.blue);
            case YELLOW:
                return mContext.getResources().getString(R.string.yellow);
            case GREY:
                return mContext.getResources().getString(R.string.grey);
        }
        return "Unknown";
    }

}
