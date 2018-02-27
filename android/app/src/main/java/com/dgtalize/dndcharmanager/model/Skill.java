package com.dgtalize.dndcharmanager.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skill implements IdentifiableItem, Parcelable {
    private String uid;
    private String name;
    private String key;
    private Boolean magic;
    private Boolean armorCheck;
    private Boolean psi;
    private Boolean trained;
    private Map<String, Boolean> synergy;


    public Skill() {

    }

    public Skill(String uid) {
        this();
        this.uid = uid;
    }

    //region Parcelable
    public static final Creator<Skill> CREATOR
            = new Creator<Skill>() {
        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };

    private Skill(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.key = in.readString();
        this.magic = in.readByte() == 1;
        this.armorCheck = in.readByte() == 1;
        this.psi = in.readByte() == 1;
        this.trained = in.readByte() == 1;
        this.synergy = in.readHashMap(Boolean.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.uid);
        out.writeString(this.name);
        out.writeString(this.key);
        out.writeByte(this.magic ? (byte) 1 : (byte) 0);
        out.writeByte(this.armorCheck ? (byte) 1 : (byte) 0);
        out.writeByte(this.psi ? (byte) 1 : (byte) 0);
        out.writeByte(this.trained ? (byte) 1 : (byte) 0);
        out.writeMap(this.synergy);
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getMagic() {
        return magic;
    }

    public void setMagic(Boolean magic) {
        this.magic = magic;
    }

    public Boolean getArmorCheck() {
        return armorCheck;
    }

    public void setArmorCheck(Boolean armorCheck) {
        this.armorCheck = armorCheck;
    }

    public Boolean getPsi() {
        return psi;
    }

    public void setPsi(Boolean psi) {
        this.psi = psi;
    }

    public Boolean getTrained() {
        return trained;
    }

    public void setTrained(Boolean trained) {
        this.trained = trained;
    }

    public Map<String, Boolean> getSynergy() {
        return synergy;
    }

    public void setSynergy(Map<String, Boolean> synergy) {
        this.synergy = synergy;
    }

    //endregion

    @Override
    public String toString() {
        return this.name;
    }
}
