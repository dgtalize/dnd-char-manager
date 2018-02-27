package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Armor
 */

public class Armor implements IdentifiableItem, Parcelable {
    protected String uid;
    protected String name;
    protected int armorBonus;
    protected int maxDexBonus;
    protected int checkPenalty;
    protected int arcaneSpellFailure;
    protected double weight;

    public Armor() {

    }

    //region Parcelable
    public static final Parcelable.Creator<Armor> CREATOR = new Parcelable.Creator<Armor>() {
        public Armor createFromParcel(Parcel in) {
            return new Armor(in);
        }

        public Armor[] newArray(int size) {
            return new Armor[size];
        }
    };

    protected Armor(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.armorBonus = in.readInt();
        this.maxDexBonus = in.readInt();
        this.checkPenalty = in.readInt();
        this.arcaneSpellFailure = in.readInt();
        this.weight = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.uid);
        out.writeString(this.name);
        out.writeInt(this.armorBonus);
        out.writeInt(this.maxDexBonus);
        out.writeInt(this.checkPenalty);
        out.writeInt(this.arcaneSpellFailure);
        out.writeDouble(this.weight);
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

    public int getArmorBonus() {
        return armorBonus;
    }

    public void setArmorBonus(int armorBonus) {
        this.armorBonus = armorBonus;
    }

    public int getMaxDexBonus() {
        return maxDexBonus;
    }

    public void setMaxDexBonus(int maxDexBonus) {
        this.maxDexBonus = maxDexBonus;
    }

    public int getCheckPenalty() {
        return checkPenalty;
    }

    public void setCheckPenalty(int checkPenalty) {
        this.checkPenalty = checkPenalty;
    }

    public int getArcaneSpellFailure() {
        return arcaneSpellFailure;
    }

    public void setArcaneSpellFailure(int arcaneSpellFailure) {
        this.arcaneSpellFailure = arcaneSpellFailure;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    //endregion


    @Override
    public String toString() {
        return this.name;
    }
}
