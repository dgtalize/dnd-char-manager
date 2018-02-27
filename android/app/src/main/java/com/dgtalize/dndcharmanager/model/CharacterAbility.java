package com.dgtalize.dndcharmanager.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Ability score per character
 */

public class CharacterAbility implements Parcelable {
    private String ability;
    private int score = 0;
    private int temporaryScore = 0;

    public CharacterAbility() {
    }

    public CharacterAbility(String ability) {
        this();
        this.ability = ability;
    }

    //region Parcelable
    public static final Parcelable.Creator<CharacterAbility> CREATOR
            = new Parcelable.Creator<CharacterAbility>() {
        public CharacterAbility createFromParcel(Parcel in) {
            return new CharacterAbility(in);
        }

        public CharacterAbility[] newArray(int size) {
            return new CharacterAbility[size];
        }
    };

    private CharacterAbility(Parcel in) {
        this.ability = in.readString();
        this.score = in.readInt();
        this.temporaryScore = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.ability);
        out.writeInt(this.score);
        out.writeInt(this.temporaryScore);
    }
    //endregion

    //region Getters/Setters
    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTemporaryScore() {
        return temporaryScore;
    }

    public void setTemporaryScore(int temporaryScore) {
        this.temporaryScore = temporaryScore;
    }

    //endregion

    /**
     * Gets the modifier for this ability, based on the score
     *
     * @return
     */
    @Exclude
    public int getModifier() {
        Double value = Math.floor((double) this.score / 2);
        return value.intValue() - 5;
    }

    /**
     * Gets the active modifier for this ability, based on the temporary score
     *
     * @return
     */
    @Exclude
    public int getActiveModifier() {
        Double value = Math.floor((double) (this.score + this.temporaryScore) / 2);
        return value.intValue() - 5;
    }
}
