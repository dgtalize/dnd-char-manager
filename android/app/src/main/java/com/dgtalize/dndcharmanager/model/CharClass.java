package com.dgtalize.dndcharmanager.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Map;

public class CharClass implements IdentifiableItem, Parcelable {
    @Exclude
    private String uid;

    private String alignment;
    private Boolean base;
    private List<String> bonusLanguages;
    private String name;
    private Map<String, Boolean> classSkills;
    private Map<String, CharClassLevel> levels;
    private String hitDice;
    private String keyStat;
    private Boolean psionicClass;
    private String secondaryStat;
    private Integer skillPoints;
    private Boolean spellCaster;
    private List<String> weaponArmorProficiency;
    private CharClassSpells spells;

    public CharClass() {

    }

    //region Parcelable
    public static final Parcelable.Creator<CharClass> CREATOR
            = new Parcelable.Creator<CharClass>() {
        public CharClass createFromParcel(Parcel in) {
            return new CharClass(in);
        }

        public CharClass[] newArray(int size) {
            return new CharClass[size];
        }
    };

    private CharClass(Parcel in) {
        this.alignment = in.readString();
        this.base = in.readByte() == 1;
        this.bonusLanguages = in.readArrayList(String.class.getClassLoader());
        this.name = in.readString();
        this.classSkills = in.readHashMap(Boolean.class.getClassLoader());
        this.levels = in.readHashMap(CharClassLevel.class.getClassLoader());
        this.hitDice = in.readString();
        this.keyStat = in.readString();
        this.psionicClass = in.readByte() == 1;
        this.secondaryStat = in.readString();
        this.skillPoints = in.readInt();
        this.spellCaster = in.readByte() == 1;
        this.weaponArmorProficiency = in.readArrayList(String.class.getClassLoader());
        this.spells = in.readParcelable(CharClassSpells.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.alignment);
        out.writeByte(this.base ? (byte) 1 : (byte) 0);
        out.writeList(this.bonusLanguages);
        out.writeString(this.name);
        out.writeMap(this.classSkills);
        out.writeMap(this.levels);
        out.writeString(this.hitDice);
        out.writeString(this.keyStat);
        out.writeByte(this.psionicClass ? (byte) 1 : (byte) 0);
        out.writeString(this.secondaryStat);
        out.writeInt(this.skillPoints);
        out.writeByte(this.spellCaster ? (byte) 1 : (byte) 0);
        out.writeList(this.weaponArmorProficiency);
        out.writeParcelable(this.spells, flags);
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

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public Boolean getBase() {
        return base;
    }

    public void setBase(Boolean base) {
        this.base = base;
    }

    public List<String> getBonusLanguages() {
        return bonusLanguages;
    }

    public void setBonusLanguages(List<String> bonusLanguages) {
        this.bonusLanguages = bonusLanguages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getClassSkills() {
        return classSkills;
    }

    public void setClassSkills(Map<String, Boolean> classSkills) {
        this.classSkills = classSkills;
    }

    public Map<String, CharClassLevel> getLevels() {
        return levels;
    }

    public void setLevels(Map<String, CharClassLevel> levels) {
        this.levels = levels;
//        this.levels = new HashMap<>();
//        //add sorted
//        for (int l = 1; l <= levels.size(); l++) {
//            String levelKey = String.format("l%d", l);
//            if (levels.containsKey(levelKey)) {
//                this.levels.put(levelKey, levels.get(levelKey));
//            }
//        }
    }

    public String getHitDice() {
        return hitDice;
    }

    public void setHitDice(String hitDice) {
        this.hitDice = hitDice;
    }

    public String getKeyStat() {
        return keyStat;
    }

    public void setKeyStat(String keyStat) {
        this.keyStat = keyStat;
    }

    public Boolean getPsionicClass() {
        return psionicClass;
    }

    public void setPsionicClass(Boolean psionicClass) {
        this.psionicClass = psionicClass;
    }

    public String getSecondaryStat() {
        return secondaryStat;
    }

    public void setSecondaryStat(String secondaryStat) {
        this.secondaryStat = secondaryStat;
    }

    public Integer getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(Integer skillPoints) {
        this.skillPoints = skillPoints;
    }

    public Boolean getSpellCaster() {
        return spellCaster;
    }

    public void setSpellCaster(Boolean spellCaster) {
        this.spellCaster = spellCaster;
    }

    public List<String> getWeaponArmorProficiency() {
        return weaponArmorProficiency;
    }

    public void setWeaponArmorProficiency(List<String> weaponArmorProficiency) {
        this.weaponArmorProficiency = weaponArmorProficiency;
    }

    public CharClassSpells getSpells() {
        return spells;
    }

    public void setSpells(CharClassSpells spells) {
        this.spells = spells;
    }
//endregion

    @Override
    public String toString() {
        return this.name;
    }

    public boolean hasSpells() {
        return (this.getSpells() != null);
    }

    /**
     * Get the BAB base on the indicated level
     *
     * @param level
     * @return
     */
    public int getBAB(Integer level) {
        if (level > this.levels.size()) {
            level = this.levels.size();
        }
        return this.levels.get(String.format("l%d", level)).getBAB();
    }
}
