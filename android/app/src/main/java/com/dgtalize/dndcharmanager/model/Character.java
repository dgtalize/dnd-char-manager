package com.dgtalize.dndcharmanager.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dgtalize.dndcharmanager.ConvertUtils;
import com.dgtalize.dndcharmanager.data.DataUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;


public class Character implements IdentifiableItem, Parcelable {
    private static final String LOG_TAG = "Character";

    private String uid;
    private String owner;
    private String name;
    private String charClass;
    private String race;
    private String alignmentUid;
    private Integer xp;
    private Integer level;
    private Integer hp;
    private Integer currentHp;
    private Map<String, CharacterAbility> abilities;
    private Map<String, CharacterSkill> skills;
    private Map<String, Item> items = new HashMap<>();
    private Map<String, CharacterWeapon> weapons = new HashMap<>();
    private Map<String, CharacterArmor> armors = new HashMap<>();
    private Map<String, Feat> feats = new HashMap<>();
    private String notes;
    private Money money = new Money();
    private String game;

    private CharClass charClassObj = null;
    private Alignment alignment = null;

    public Character() {
        this.level = 1;
        this.xp = 0;
        this.hp = 0;
        this.currentHp = hp;
        //initialize abilities
        this.abilities = new HashMap<>();
        this.abilities.put(Ability.KEY_STR, new CharacterAbility(Ability.KEY_STR));
        this.abilities.put(Ability.KEY_DEX, new CharacterAbility(Ability.KEY_DEX));
        this.abilities.put(Ability.KEY_CON, new CharacterAbility(Ability.KEY_CON));
        this.abilities.put(Ability.KEY_INT, new CharacterAbility(Ability.KEY_INT));
        this.abilities.put(Ability.KEY_WIS, new CharacterAbility(Ability.KEY_WIS));
        this.abilities.put(Ability.KEY_CHA, new CharacterAbility(Ability.KEY_CHA));
    }

    public Character(String uid) {
        this();
        this.uid = uid;
    }

    //region Parcelable
    public static final Parcelable.Creator<Character> CREATOR
            = new Parcelable.Creator<Character>() {
        public Character createFromParcel(Parcel in) {
            return new Character(in);
        }

        public Character[] newArray(int size) {
            return new Character[size];
        }
    };

    private Character(Parcel in) {
        this.uid = in.readString();
        this.owner = in.readString();
        this.name = in.readString();
        this.race = in.readString();
        this.charClass = in.readString();
        this.alignmentUid = in.readString();
        this.xp = in.readInt();
        this.level = in.readInt();
        this.hp = in.readInt();
        this.currentHp = in.readInt();
        this.abilities = in.readHashMap(CharacterAbility.class.getClassLoader());
        this.skills = in.readHashMap(CharacterSkill.class.getClassLoader());
        this.items = in.readHashMap(Item.class.getClassLoader());
        this.weapons = in.readHashMap(CharacterWeapon.class.getClassLoader());
        this.armors = in.readHashMap(CharacterArmor.class.getClassLoader());
        this.feats = in.readHashMap(Feat.class.getClassLoader());
        this.notes = in.readString();
        this.money = in.readParcelable(Money.class.getClassLoader());
        this.game = in.readString();

        this.charClassObj = in.readParcelable(CharClass.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.uid);
        out.writeString(this.owner);
        out.writeString(this.name);
        out.writeString(this.race);
        out.writeString(this.charClass);
        out.writeString(this.alignmentUid);
        out.writeInt(this.xp);
        out.writeInt(this.level);
        out.writeInt(this.hp);
        out.writeInt(this.currentHp);
        out.writeMap(this.abilities);
        out.writeMap(this.skills);
        out.writeMap(this.items);
        out.writeMap(this.weapons);
        out.writeMap(this.armors);
        out.writeMap(this.feats);
        out.writeString(this.notes);
        out.writeParcelable(this.money, flags);
        out.writeString(this.game);

        out.writeParcelable(this.charClassObj, flags);
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @PropertyName("class")
    public String getCharClass() {
        return charClass;
    }

    @PropertyName("class")
    public void setCharClass(String charClass) {
        this.charClass = charClass;
        //load the full class
        loadFullClass(new OnCharClassLoadListener() {
            @Override
            public void onCharClassLoaded(CharClass charClass) {
                //do nothing
            }
        });
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    @PropertyName("alignment")
    public String getAlignmentUid() {
        return alignmentUid;
    }

    @PropertyName("alignment")
    public void setAlignmentUid(String alignmentUid) {
        this.alignmentUid = alignmentUid;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
    }

    public Map<String, CharacterAbility> getAbilities() {
        return abilities;
    }

    public void setAbilities(Map<String, CharacterAbility> abilities) {
        this.abilities = abilities;
    }

    public Map<String, CharacterSkill> getSkills() {
        return skills;
    }

    public void setSkills(Map<String, CharacterSkill> skills) {
        this.skills = skills;
    }

    public Map<String, Item> getItems() {
        return items;
    }

    public void setItems(Map<String, Item> items) {
        this.items = items;
    }

    public Map<String, CharacterWeapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(Map<String, CharacterWeapon> weapons) {
        this.weapons = weapons;
        //set the keys as internal UID
        for (Map.Entry<String, CharacterWeapon> weaponEntry : this.weapons.entrySet()) {
            weaponEntry.getValue().setUid(weaponEntry.getKey());
        }
    }

    public Map<String, CharacterArmor> getArmors() {
        return armors;
    }

    public void setArmors(Map<String, CharacterArmor> armors) {
        this.armors = armors;
        //set the keys as internal UID
        for (Map.Entry<String, CharacterArmor> armorEntry : this.armors.entrySet()) {
            armorEntry.getValue().setUid(armorEntry.getKey());
        }
    }

    public Map<String, Feat> getFeats() {
        return feats;
    }

    public void setFeats(Map<String, Feat> feats) {
        this.feats = feats;
        //set the keys as internal UID
        for (Map.Entry<String, Feat> featEntry : this.feats.entrySet()) {
            featEntry.getValue().setUid(featEntry.getKey());
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    //endregion

    //region Health

    /**
     * Add Hit Points to the base HP of the character. This is NOT damage
     *
     * @param points
     */
    public void addBaseHp(int points) {
        this.hp += points;
    }

    /**
     * Add damage to the Character
     *
     * @param points Points of damage. Will be substracted from the current HPs
     */
    public void addDamage(int points) {
        this.currentHp -= points;
    }

    /**
     * Add current hit points to the Character
     *
     * @param points Points of healing. Will be added from the current HPs
     */
    public void heal(int points) {
        this.currentHp += points;
        if (this.currentHp > this.hp) {
            this.currentHp = this.hp;
        }
    }

    //endregion

    //region Experience

    /**
     * Add Experience Points to the character
     *
     * @param points Can be positive or negative
     * @return The difference in level from previous one. 0 means no change
     */
    public int addXp(int points) {
        //XP cannot be less than 0
        if ((this.xp + points) < 0) {
            throw new InvalidParameterException("XP cannot be less than 0.");
        }

        this.xp += points;
        int levelDiff = this.updateLevel();
        return levelDiff;
    }

    /**
     * Updates the Character level based on the characters XP
     *
     * @return The difference in level from previous one. 0 means no change
     */
    private int updateLevel() {
        int originalLevel = this.level;
        int calc = ((Double) Math.floor((double) this.xp / 1000.0)).intValue();

        int tempLevel = 0;
        //start cycling and substracting levels to see how far I can go
        //if I reach a negative number, then I went too far
        while (calc >= 0) {
            tempLevel++;
            calc -= tempLevel;
        }
        this.level = tempLevel;
        return (this.level - originalLevel);
    }

    //endregion


    @Override
    public String toString() {
        return this.getName();
    }

    public void recalculateSkillModifiers() {
        final Map<String, Skill> baseSkills = new HashMap<>();

        //Get the base skills to get information for the calculation (like which ability is key to which skill)
        DatabaseReference skillsReference = DataUtils.getDatabase().child("skills");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get all skills
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    baseSkills.put(ds.getKey(), ds.getValue(Skill.class));
                }
                //update the character skills
                for (Map.Entry<String, CharacterSkill> entry : skills.entrySet()) {
                    Skill skill = baseSkills.get(entry.getValue().getSkill());
                    //get the character ability corresponding to the Skill key ability
                    CharacterAbility charAbility = abilities.get(skill.getKey());
                    //update the Ability Skill modifier
                    if (charAbility != null) {
                        entry.getValue().setAbilityModifier(charAbility.getModifier());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadSkills:onCancelled", databaseError.toException());
            }
        };
        skillsReference.addListenerForSingleValueEvent(valueEventListener);

    }

    public boolean isClassSkill(String skill) {
        return this.charClassObj.getClassSkills().containsKey(skill);
    }

    @Exclude
    public int getCasterLevel() {
        return this.getLevel();
    }

    /**
     * Gets the available number of Spells for the @spellsLevel, based on the Character level and Class
     *
     * @param spellsLevel
     * @return Returns NULL if not available
     */
    public Integer spellsAvailableLevel(int spellsLevel) {
        //Get the Class Level attributes for the current character level
        CharClassLevel currentLevel = this.charClassObj.getLevels().get(ConvertUtils.generateLevelKey(this.getLevel()));
        //Get the spells available for casting based for the Spells Level
        if (currentLevel.getSpellCasting() != null
                && currentLevel.getSpellCasting().containsKey(ConvertUtils.generateLevelKey(spellsLevel))) {
            return currentLevel.getSpellCasting().get(ConvertUtils.generateLevelKey(spellsLevel));
        } else {
            return null;
        }
    }

    /**
     * Gets the Base Attack Bonus of the Character based on its level
     *
     * @return
     */
    @Exclude
    public int getBAB() {
        return charClassObj.getBAB(this.getLevel());
    }

    /**
     * Get the attack bonus by using the selected weapon by parameter
     *
     * @param weapon Which weapon is being used
     * @return
     */
    @Exclude
    public int getAttackBonus(CharacterWeapon weapon) {
        //get the Class BAB based on the current level
        int attackBonuns = charClassObj.getBAB(this.getLevel());
        if (weapon.isRanged) {
            attackBonuns += this.abilities.get(Ability.KEY_DEX).getModifier();
        } else {
            attackBonuns += this.abilities.get(Ability.KEY_STR).getModifier();
        }
        //TODO: size modified
        return attackBonuns;
    }

    /**
     * Get the Armor Class of the Character based on armor
     *
     * @return
     */
    @Exclude
    public int getArmorClass() {
        //Base value
        int armorClass = 10;
        //DEX modified
        armorClass += this.getAbilities().get(Ability.KEY_DEX).getModifier();
        //Equipped gear
        for (Map.Entry<String, CharacterArmor> charArmorEntry : this.getArmors().entrySet()) {
            //if is equipped, add the armor
            if (charArmorEntry.getValue().isEquipped()) {
                armorClass += charArmorEntry.getValue().getArmorBonus();
            }
        }

        return armorClass;
    }

    /**
     * Get the grapple modifier
     *
     * @return
     */
    @Exclude
    public int getGrappleModifier() {
        //get the BAB
        int grappleModifier = this.getBAB();
        //get the strenght modifier
        grappleModifier += this.abilities.get(Ability.KEY_STR).getActiveModifier();
        //TODO: size modifier
        return grappleModifier;
    }

    //region Savings

    @Exclude
    public int getSavingFortitude() {
        int saving = 0;
        //Class Base modifier
        String currentLvlKey = ConvertUtils.generateLevelKey(this.getLevel());
        saving += this.charClassObj.getLevels().get(currentLvlKey).getFort();
        //Ability modifier
        saving += this.getAbilities().get(Ability.KEY_CON).getModifier();
        return saving;
    }

    @Exclude
    public int getSavingReflex() {
        int saving = 0;
        //Class Base modifier
        String currentLvlKey = ConvertUtils.generateLevelKey(this.getLevel());
        saving += this.charClassObj.getLevels().get(currentLvlKey).getRef();
        //Ability modifier
        saving += this.getAbilities().get(Ability.KEY_DEX).getModifier();
        return saving;
    }

    @Exclude
    public int getSavingWill() {
        int saving = 0;
        //Class Base modifier
        String currentLvlKey = ConvertUtils.generateLevelKey(this.getLevel());
        saving += this.charClassObj.getLevels().get(currentLvlKey).getWill();
        //Ability modifier
        saving += this.getAbilities().get(Ability.KEY_WIS).getModifier();
        return saving;
    }

    //endregion

    //region Loading the full character class

    public void loadFullClass(final OnCharClassLoadListener listener) {
        //if not loaded, load it first
        if (this.charClassObj == null) {
            //Get the Character Class
            DatabaseReference classReference = DataUtils.getDatabase().child("classes").child(this.charClass);
//            classReference.keepSynced(true);
            classReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot charClassDS) {
                    try {
                        charClassObj = charClassDS.getValue(CharClass.class);
                        if (listener != null) {
                            listener.onCharClassLoaded(charClassObj);
                        }
                    } catch (DatabaseException dbEx) {
                        Log.e(LOG_TAG, String.format("Error loading Class: %s", charClassDS.getKey()));
                        Log.d(LOG_TAG, String.format("Class value: %s", charClassDS.getValue().toString()));
                        throw dbEx;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(LOG_TAG, "loadClass:onCancelled", databaseError.toException());
                }
            });
        } else {
            //if loaded then just execute
            if (listener != null) {
                listener.onCharClassLoaded(charClassObj);
            }
        }
    }

    public interface OnCharClassLoadListener {
        public void onCharClassLoaded(CharClass charClass);
    }
    //endregion

    //region Alignment

    @Exclude
    public Alignment getAlignment() {
        return alignment;
    }

    @Exclude
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
        this.alignmentUid = alignment.getUid();
    }

    //endregion Alignment

    /**
     * Initialize the Skills for the character
     */
    public void initializeSkills(final OnCharacterLoadListener listener) {
        this.skills = new HashMap<>();

        DatabaseReference database = DataUtils.getDatabase();

        Query skillsQuery = database.child("skills").orderByKey();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Skill skill = ds.getValue(Skill.class);
                    CharacterSkill charSkill = new CharacterSkill(ds.getKey());
                    skills.put(ds.getKey(), charSkill);
                }

                if (listener != null) {
                    listener.onSkillsInitialized();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "initializeSkills:onCancelled", databaseError.toException());
            }
        };
        skillsQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    public interface OnCharacterLoadListener {
        public void onSkillsInitialized();
    }
}
