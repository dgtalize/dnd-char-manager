package com.dgtalize.dndcharmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dgtalize.dndcharmanager.LevelKeyComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Spells information of a Character Class
 */

public class CharClassSpells implements Parcelable {

    private String type;
    private Map<String, Map<String, Boolean>> levels;

    public CharClassSpells() {

    }

    //region Parcelable
    public static final Parcelable.Creator<CharClassSpells> CREATOR
            = new Parcelable.Creator<CharClassSpells>() {
        public CharClassSpells createFromParcel(Parcel in) {
            return new CharClassSpells(in);
        }

        public CharClassSpells[] newArray(int size) {
            return new CharClassSpells[size];
        }
    };

    private CharClassSpells(Parcel in) {
        this.type = in.readString();
        this.levels = in.readHashMap(Map.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.type);
        out.writeMap(this.levels);
    }
    //endregion

    //region Getters/Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Map<String, Boolean>> getLevels() {
        return levels;
    }

    public void setLevels(Map<String, Map<String, Boolean>> levels) {
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
    //endregion

    public Map<String, Boolean> getSpellsMap(int level) {
        String levelKey = String.format("l%d", level);
        return this.getLevels().get(levelKey);
    }

    public List<String> getSortedLevelKeys() {
        List<String> levelKeys = new ArrayList<>(this.getLevels().keySet());
        Collections.sort(levelKeys, new LevelKeyComparator());

        return levelKeys;
    }
}
