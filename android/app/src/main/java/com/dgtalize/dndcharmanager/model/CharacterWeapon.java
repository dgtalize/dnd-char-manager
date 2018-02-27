package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Weapon owned by a Character
 */

public class CharacterWeapon extends Weapon {
    private boolean isEquipped = true;

    public CharacterWeapon() {}

    /**
     * Creates a new Weapon for the Character, based on a standard Weapon
     * @param weaponBase
     */
    public CharacterWeapon(Weapon weaponBase) {
//        this.uid = null;
        this.name = weaponBase.getName();
        this.cost = weaponBase.getCost();
        this.damageSmall = weaponBase.getDamageSmall();
        this.damageMedium = weaponBase.getDamageMedium();
        this.critical = weaponBase.getCritical();
        this.rangeIncrement = weaponBase.getRangeIncrement();
        this.weight = weaponBase.getWeight();
        this.type = weaponBase.getType();
        this.isRanged = weaponBase.isRanged();
    }

    //region Parcelable
    public static final Parcelable.Creator<CharacterWeapon> CREATOR
            = new Parcelable.Creator<CharacterWeapon>() {
        public CharacterWeapon createFromParcel(Parcel in) {
            return new CharacterWeapon(in);
        }

        public CharacterWeapon[] newArray(int size) {
            return new CharacterWeapon[size];
        }
    };

    private CharacterWeapon(Parcel in) {
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
