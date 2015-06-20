package com.tacitus.dnp.scenario;

import android.os.Parcel;
import android.os.Parcelable;

public class Sound implements Parcelable{

    public enum Consonant {
        D, UNDEFINED
    }
    private String mText;
    private Line mLine;

    private Consonant mConsonant;

    public Sound(Parcel in) {
        mText = in.readString();
        mLine = in.readParcelable(Line.class.getClassLoader());
        try {
            mConsonant = Consonant.valueOf(in.readString());
        } catch ( IllegalArgumentException x) {
            mConsonant = Consonant.UNDEFINED;
        }
    }

    public Sound() {
        mText = "";
        mLine = new Line();
        initConsonant();
    }

    public Sound(String text, Line line) {
        mText = text;
        mLine = line;
        initConsonant();
    }

    public Sound(Sound sound) {
        mText = sound.getText();
        mLine = sound.getLine();
        initConsonant();
    }

    public Sound(int color, String text) {
        mText = text;
        mLine = new Line(color);
        initConsonant();
    }

    public Sound(String text) {
        mText = text;
        mLine = new Line();
        initConsonant();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
        initConsonant();
    }

    public Line getLine() {
        return mLine;
    }

    public void setLine(Line line) {
        this.mLine = line;
    }

    public Consonant getConsonant() {
        return mConsonant;
    }

    public void setConsonant(Consonant consonant) {
        this.mConsonant = consonant;
    }

    private void initConsonant() {
        if (mText.isEmpty()){
            setConsonant(Consonant.UNDEFINED);
            return;
        }

        char firstLetter = mText.toLowerCase().charAt(0);
        switch (firstLetter){
            case 'd':
                setConsonant(Consonant.D);
                break;
            default:
                setConsonant(Consonant.UNDEFINED);
                break;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mText);
        dest.writeParcelable(mLine, flags);
        dest.writeString( mConsonant.name() );
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
