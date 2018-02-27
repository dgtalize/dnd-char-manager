package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A Game which has a Master and playes (Characters)
 */

public class Game implements IdentifiableItem, Parcelable {

    private String uid;
    private String name;
    private String description;
    private String masterUID;
    private Map<String, Boolean> characters;

    public Game() {

    }

    public Game(String uid) {
        super();
        this.uid = uid;
    }

    //region Parcelable
    public static final Parcelable.Creator<Game> CREATOR
            = new Parcelable.Creator<Game>() {
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    protected Game(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.masterUID = in.readString();
        this.characters = in.readHashMap(Boolean.class.getClassLoader());
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
        out.writeString(this.masterUID);
        out.writeMap(this.characters);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("master")
    public String getMasterUID() {
        return masterUID;
    }

    @PropertyName("master")
    public void setMasterUID(String masterUID) {
        this.masterUID = masterUID;
    }

    public Map<String, Boolean> getCharacters() {
        return characters;
    }

    public void setCharacters(Map<String, Boolean> characters) {
        this.characters = characters;
    }

    //endregion

    @Override
    public String toString() {
        return this.name;
    }

    @Exclude
    public int getCharactersCount() {
        if (this.characters == null) {
            return 0;
        } else {
            return this.characters.size();
        }
    }
}
