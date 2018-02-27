package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Weapon
 */

public class Weapon implements IdentifiableItem, Parcelable {
    protected String uid;
    protected String name;
    protected String cost;
    protected String damageSmall;
    protected String damageMedium;
    protected String critical;
    protected String rangeIncrement;
    protected double weight;
    protected String type;
    protected boolean isRanged;

    public Weapon() {

    }

    //region Parcelable
    public static final Parcelable.Creator<Weapon> CREATOR
            = new Parcelable.Creator<Weapon>() {
        public Weapon createFromParcel(Parcel in) {
            return new Weapon(in);
        }

        public Weapon[] newArray(int size) {
            return new Weapon[size];
        }
    };

    protected Weapon(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.cost = in.readString();
        this.damageSmall = in.readString();
        this.damageMedium = in.readString();
        this.critical = in.readString();
        this.rangeIncrement = in.readString();
        this.weight = in.readDouble();
        this.type = in.readString();
        this.isRanged = in.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.uid);
        out.writeString(this.name);
        out.writeString(this.cost);
        out.writeString(this.damageSmall);
        out.writeString(this.damageMedium);
        out.writeString(this.critical);
        out.writeString(this.rangeIncrement);
        out.writeDouble(this.weight);
        out.writeString(this.type);
        out.writeByte(this.isRanged ? (byte) 1 : (byte) 0);
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDamageSmall() {
        return damageSmall;
    }

    public void setDamageSmall(String damageSmall) {
        this.damageSmall = damageSmall;
    }

    public String getDamageMedium() {
        return damageMedium;
    }

    public void setDamageMedium(String damageMedium) {
        this.damageMedium = damageMedium;
    }

    public String getCritical() {
        return critical;
    }

    public void setCritical(String critical) {
        this.critical = critical;
    }

    public String getRangeIncrement() {
        return rangeIncrement;
    }

    public void setRangeIncrement(String rangeIncrement) {
        this.rangeIncrement = rangeIncrement;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRanged() {
        return isRanged;
    }

    public void setRanged(boolean ranged) {
        isRanged = ranged;
    }
    //endregion


    @Override
    public String toString() {
        return this.name;
    }
}
