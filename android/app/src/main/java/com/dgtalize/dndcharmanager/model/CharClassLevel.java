package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

/**
 * Class attributes per level
 */

public class CharClassLevel implements Parcelable {
    private int BAB;
    private int fort;
    private int ref;
    private int will;
    private List<String> abilities;
    private Map<String, Integer> spellCasting;

    public CharClassLevel() {

    }

    //region Parcelable
    public static final Parcelable.Creator<CharClassLevel> CREATOR
            = new Parcelable.Creator<CharClassLevel>() {
        public CharClassLevel createFromParcel(Parcel in) {
            return new CharClassLevel(in);
        }

        public CharClassLevel[] newArray(int size) {
            return new CharClassLevel[size];
        }
    };

    private CharClassLevel(Parcel in) {
        this.BAB = in.readInt();
        this.fort = in.readInt();
        this.ref = in.readInt();
        this.will = in.readInt();
        this.abilities = in.readArrayList(String.class.getClassLoader());
        this.spellCasting = in.readHashMap(Integer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.BAB);
        out.writeInt(this.fort);
        out.writeInt(this.ref);
        out.writeInt(this.will);
        out.writeList(this.abilities);
        out.writeMap(this.spellCasting);
    }
    //endregion

    //region Getters/Setters

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public int getBAB() {
        return BAB;
    }

    public void setBAB(int BAB) {
        this.BAB = BAB;
    }

    public int getFort() {
        return fort;
    }

    public void setFort(int fort) {
        this.fort = fort;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    public int getWill() {
        return will;
    }

    public void setWill(int will) {
        this.will = will;
    }

    public Map<String, Integer> getSpellCasting() {
        return spellCasting;
    }

    public void setSpellCasting(Map<String, Integer> spellCasting) {
        this.spellCasting = spellCasting;
    }

    //endregion
}
