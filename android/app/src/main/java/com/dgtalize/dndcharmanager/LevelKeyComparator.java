package com.dgtalize.dndcharmanager;

import java.util.Comparator;

/**
 * Comparator for keys for Level
 */

public class LevelKeyComparator implements Comparator<String> {
    @Override
    public int compare(String s, String t1) {
        Integer level1 = Integer.parseInt(s.substring(1));
        Integer level2 = Integer.parseInt(t1.substring(1));
        return level1.compareTo(level2);
    }
}
