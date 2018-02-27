package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

/**
 * Spell
 */

public class Spell implements IdentifiableItem, Parcelable {
    private String uid;
    private String name;
    private String description;

    public Spell() {

    }

    //region Parcelable
    public static final Parcelable.Creator<Spell> CREATOR
            = new Parcelable.Creator<Spell>() {
        public Spell createFromParcel(Parcel in) {
            return new Spell(in);
        }

        public Spell[] newArray(int size) {
            return new Spell[size];
        }
    };

    protected Spell(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.uid);
        out.writeString(this.name);
        out.writeString(this.description);
    }

    //endregion

    //region Getters/Setters
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

    @PropertyName("desc")
    public String getDescription() {
        return description;
    }

    @PropertyName("desc")
    public void setDescription(String description) {
        this.description = description;
    }
    //endregion
}
