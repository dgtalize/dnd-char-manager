package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Feat
 */

public class Feat implements IdentifiableItem, Parcelable {
    private String uid;
    private String name;
    private String description;
    private String shortDescription;
    private List<String> prerequisites;

    public Feat() {

    }

    //region Parcelable
    public static final Parcelable.Creator<Feat> CREATOR
            = new Parcelable.Creator<Feat>() {
        public Feat createFromParcel(Parcel in) {
            return new Feat(in);
        }

        public Feat[] newArray(int size) {
            return new Feat[size];
        }
    };

    protected Feat(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.shortDescription = in.readString();
        this.prerequisites = in.readArrayList(String.class.getClassLoader());
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
        out.writeString(this.shortDescription);
        out.writeList(this.prerequisites);
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

    @PropertyName("longText")
    public String getDescription() {
        return description;
    }

    @PropertyName("longText")
    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("shortText")
    public String getShortDescription() {
        return shortDescription;
    }

    @PropertyName("shortText")
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    //endregion


    @Override
    public String toString() {
        return this.name;
    }
}
