package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Money container
 */

public class Money implements Parcelable {
    private int cp;
    private int sp;
    private int gp;
    private int pp;

    public Money() {
    }

    //region Parcelable
    public static final Parcelable.Creator<Money> CREATOR
            = new Parcelable.Creator<Money>() {
        public Money createFromParcel(Parcel in) {
            return new Money(in);
        }

        public Money[] newArray(int size) {
            return new Money[size];
        }
    };

    private Money(Parcel in) {
        this.cp = in.readInt();
        this.sp = in.readInt();
        this.gp = in.readInt();
        this.pp = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.cp);
        out.writeInt(this.sp);
        out.writeInt(this.gp);
        out.writeInt(this.pp);
    }
    //endregion

    //region Getters/Setters
    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp = sp;
    }

    public int getGp() {
        return gp;
    }

    public void setGp(int gp) {
        this.gp = gp;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }
    //endregion
}
