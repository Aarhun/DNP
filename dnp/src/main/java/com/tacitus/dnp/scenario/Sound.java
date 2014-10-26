package com.tacitus.dnp.scenario;

import android.os.Parcel;
import android.os.Parcelable;

public class Sound implements Parcelable{

    private String mText;
    private Line mLine;

    public Sound(Parcel in) {
        mText = in.readString();
        mLine = in.readParcelable(Line.class.getClassLoader());
    }

    public Sound() {
        mText = "";
        mLine = new Line();
    }

    public Sound(int color, String text) {
        mText = text;
        mLine = new Line(color);
    }

    public Sound(String text) {
        mText = text;
        mLine = new Line();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public Line getLine() {
        return mLine;
    }

    public void setLine(Line line) {
        this.mLine = line;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mText);
        dest.writeParcelable(mLine, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Sound createFromParcel(Parcel in) {
            return new Sound(in);
        }

        public Sound[] newArray(int size) {
            return new Sound[size];
        }
    };

    public String toString() {
        return mText + " " + mLine.toString();
    }
}
