package com.dgtalize.dndcharmanager.model;

/**
 * Base abilities
 */

public class Ability {
    public static final String KEY_STR = "str";
    public static final String KEY_DEX = "dex";
    public static final String KEY_CON = "con";
    public static final String KEY_INT = "int";
    public static final String KEY_WIS = "wis";
    public static final String KEY_CHA = "cha";

    public static class Comparator implements java.util.Comparator<String> {

        @Override
        public int compare(String s, String t1) {
            return Integer.compare(getOrder(s), getOrder(t1));
        }

        public int getOrder(String abilityKey) {
            switch (abilityKey) {
                case Ability.KEY_STR:
                    return 1;
                case Ability.KEY_DEX:
                    return 2;
                case Ability.KEY_CON:
                    return 3;
                case Ability.KEY_INT:
                    return 4;
                case Ability.KEY_WIS:
                    return 5;
                case Ability.KEY_CHA:
                    return 6;
            }
            return 0;
        }
    }
}
