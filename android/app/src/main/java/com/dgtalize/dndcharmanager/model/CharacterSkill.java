package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dgtalize.dndcharmanager.data.DataUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

/**
 * Ability rank per character
 */

public class CharacterSkill implements Parcelable {
    private static final String LOG_TAG = "CharacterSkill";

    private String skill;
    private int rank;
    private int miscModifier;
    private int abilityModifier;
    private Skill skillObj;

    public CharacterSkill() {
        this.rank = 0;
        this.miscModifier = 0;
    }

    public CharacterSkill(String skill) {
        this();
        this.setSkill(skill);
    }

    //region Parcelable
    public static final Creator<CharacterSkill> CREATOR
            = new Creator<CharacterSkill>() {
        public CharacterSkill createFromParcel(Parcel in) {
            return new CharacterSkill(in);
        }

        public CharacterSkill[] newArray(int size) {
            return new CharacterSkill[size];
        }
    };

    private CharacterSkill(Parcel in) {
        this.skill = in.readString();
        this.rank = in.readInt();
        this.miscModifier = in.readInt();
        this.abilityModifier = in.readInt();

        this.skillObj = in.readParcelable(Skill.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.skill);
        out.writeInt(this.rank);
        out.writeInt(this.miscModifier);
        out.writeInt(this.abilityModifier);

        out.writeParcelable(this.skillObj, flags);
    }
    //endregion

    //region Getters/Setters
    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
        //load the full Skill
        loadFullSkill(new OnSkillLoadListener() {
            @Override
            public void onSkillLoaded(Skill skill) {
                //do nothing
            }
        });
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMiscModifier() {
        return miscModifier;
    }

    public void setMiscModifier(int miscModifier) {
        this.miscModifier = miscModifier;
    }

    public int getAbilityModifier() {
        return abilityModifier;
    }

    public void setAbilityModifier(int abilityModifier) {
        this.abilityModifier = abilityModifier;
    }

    //endregion

    /**
     * Gets the modifier for this skill, based on the ability, rank and misc
     *
     * @return
     */
    @Exclude
    public int getModifier() {
        return abilityModifier + rank + miscModifier;
    }

    private void loadFullSkill(final OnSkillLoadListener listener) {
        //if not loaded, load it first
        if (this.skillObj == null) {
            //Get the Character Class
            DatabaseReference skillsReference = DataUtils.getDatabase().child("skills").child(this.getSkill());
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    skillObj = dataSnapshot.getValue(Skill.class);
                    listener.onSkillLoaded(skillObj);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(LOG_TAG, "loadSkill:onCancelled", databaseError.toException());
                }
            };
            skillsReference.addListenerForSingleValueEvent(valueEventListener);
        } else {
            //if loaded then just execute
            listener.onSkillLoaded(skillObj);
        }
    }

    @Exclude
    public String getSkillName() {
        if (this.skillObj != null) {
            return String.format("%s (%s)", this.skillObj.getName(), this.skillObj.getKey());
        } else {
            return this.skill;
        }
    }

    private interface OnSkillLoadListener {
        public void onSkillLoaded(Skill skill);
    }
}
