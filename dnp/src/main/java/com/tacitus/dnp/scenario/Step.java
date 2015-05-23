package com.tacitus.dnp.scenario;

import android.os.Parcel;
import android.os.Parcelable;


public class Step implements Parcelable {
    private Sound mSound;
    private String mTitle;
    private Boolean mLinkedUp;
    private Boolean mLinkedDown;

    public Step(Parcel in) {
        mTitle = in.readString();
        mSound = in.readParcelable(Sound.class.getClassLoader());
        mLinkedUp = (Boolean) in.readValue(Boolean.class.getClassLoader());
        mLinkedDown = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public Step() {
        mTitle = "";
        mSound = new Sound();
        mLinkedDown = false;
        mLinkedUp = false;
    }

    public Step(int color, String text) {
        mTitle = "";
        mSound = new Sound(color, text);
        mLinkedDown = false;
        mLinkedUp = false;
    }

    public Step(String text) {
        mTitle = "";
        mSound = new Sound();
        mLinkedDown = false;
        mLinkedUp = false;
    }

    public Step(String title, Boolean linkedUp, Boolean linkedDown, int color, String text) {
        mTitle = title;
        mSound = new Sound(color, text);
        mLinkedDown = linkedDown;
        mLinkedUp = linkedUp;
    }

    public Step(Boolean linkedDown, Boolean linkedUp, Sound sound, String title) {
        mLinkedDown = linkedDown;
        mLinkedUp = linkedUp;
        mSound = new Sound(sound);
        mTitle = title;
    }

    public Step(Step step) {
        mTitle = step.getTitle();
        mLinkedDown = step.getLinkedDown();
        mLinkedUp = step.getLinkedUp();
        mSound = step.getSound();
    }

    public Sound getSound() {
        return mSound;
    }

    public void setSound(Sound sound) {
        this.mSound = sound;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Boolean getLinkedUp() {
        return mLinkedUp;
    }

    public void setLinkedUp(Boolean linkedUp) {
        this.mLinkedUp = linkedUp;
    }

    public Boolean getLinkedDown() {
        return mLinkedDown;
    }

    public void setLinkedDown(Boolean linkedDown) {
        this.mLinkedDown = linkedDown;
    }

    public String getText() {
        return mSound.getText();
    }

    public void setText(String text) {
        this.mSound.setText(text);
    }

    public Line getLine() {
        return mSound.getLine();
    }

    public void setLine(Line line) {
        this.mSound.setLine(line);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeParcelable(mSound, flags);
        dest.writeValue(mLinkedUp);
        dest.writeValue(mLinkedDown);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    public String toString() {
        return mTitle + " " + mSound.toString() + ", linked up: " + mLinkedUp + ", linked down: " + mLinkedDown;
    }


}
