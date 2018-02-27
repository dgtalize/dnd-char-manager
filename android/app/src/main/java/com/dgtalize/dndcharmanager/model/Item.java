package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Item that can be possessed
 */

public class Item implements IdentifiableItem, Parcelable {

    private String uid;
    private String name;
    private String description;
    private double weight;
    private boolean isMagical = false;

    public Item() {

    }

    //region Parcelable
    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.weight = in.readDouble();
        this.isMagical = in.readByte() == 1 ? true : false;
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
        out.writeDouble(this.weight);
        out.writeByte(this.isMagical ? (byte) 1 : (byte) 0);
    }
    //endregion

    //region Getters/Setters

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMagical() {
        return isMagical;
    }

    public void setMagical(boolean magical) {
        isMagical = magical;
    }
    //endregion
}
