package com.tacitus.dnp.scenario;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;


public class Line implements Parcelable {

    private boolean mHollow;
    private boolean mUnderline;
    private int mColor;

    public Line(Parcel in) {
        mHollow = (Boolean) in.readValue(Boolean.class.getClassLoader());
        mUnderline = (Boolean) in.readValue(Boolean.class.getClassLoader());
        mColor = in.readInt();
    }

    public Line() {
        mHollow = false;
        mUnderline = false;
        mColor = Color.argb(255, 255, 255, 255);
    }

    public Line(int color) {
        mHollow = false;
        mUnderline = false;
        mColor = color;
    }

    public Line(int a, int r, int g, int b) {
        mHollow = false;
        mUnderline = false;
        mColor = Color.argb(a, r, g, b);
    }

    public boolean isHollow() {
        return mHollow;
    }

    public void setHollow(boolean mHollow) {
        this.mHollow = mHollow;
    }

    public boolean isUnderline() {
        return mUnderline;
    }

    public void setUnderline(boolean mUnderline) {
        this.mUnderline = mUnderline;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mHollow);
        dest.writeValue(mUnderline);
        dest.writeInt(mColor);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Line createFromParcel(Parcel in) {
            return new Line(in);
        }

        public Line[] newArray(int size) {
            return new Line[size];
        }
    };

    public String toString() {
        return "Hollow: " + mHollow + ", Underline: " + mUnderline + ", Color: " + mColor;
    }
}


