package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Armor owned by a Character
 */

public class CharacterArmor extends Armor {
    private boolean isEquipped = true;

    public CharacterArmor() {
    }

    /**
     * Creates a new Armor for the Character, based on a standard Armor
     *
     * @param armorBase
     */
    public CharacterArmor(Armor armorBase) {
//        this.uid = null;
        this.name = armorBase.getName();
        this.armorBonus = armorBase.getArmorBonus();
        this.maxDexBonus = armorBase.getMaxDexBonus();
        this.checkPenalty = armorBase.getCheckPenalty();
        this.arcaneSpellFailure = armorBase.getArcaneSpellFailure();
        this.weight = armorBase.getWeight();
    }

    //region Parcelable
    public static final Parcelable.Creator<CharacterArmor> CREATOR = new Parcelable.Creator<CharacterArmor>() {
        public CharacterArmor createFromParcel(Parcel in) {
            return new CharacterArmor(in);
        }

        public CharacterArmor[] newArray(int size) {
            return new CharacterArmor[size];
        }
    };

    private CharacterArmor(Parcel in) {
        super(in);
        this.isEquipped = (in.readByte() == 1);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeByte(this.isEquipped ? (byte) 1 : (byte) 0);
    }

    //endregion

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }
}
