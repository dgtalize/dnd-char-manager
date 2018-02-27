package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Character Alignment
 */

public class Alignment implements IdentifiableItem, Parcelable {
    private String uid;
    private String name;
    private String law;
    private String good;

    public Alignment() {

    }

    //region Parcelable
    public static final Parcelable.Creator<Alignment> CREATOR
            = new Parcelable.Creator<Alignment>() {
        public Alignment createFromParcel(Parcel in) {
            return new Alignment(in);
        }

        public Alignment[] newArray(int size) {
            return new Alignment[size];
        }
    };

    protected Alignment(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.law = in.readString();
        this.good = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.uid);
        out.writeString(this.name);
        out.writeString(this.law);
        out.writeString(this.good);
    }

    //endregion

    //region Getters/Setters

    @Override
    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }
//endregion Getters/Setters

    @Override
    public String toString() {
        return this.name;
    }
}
