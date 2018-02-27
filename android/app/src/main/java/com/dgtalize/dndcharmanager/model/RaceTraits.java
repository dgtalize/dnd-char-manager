package com.dgtalize.dndcharmanager.model;


import java.util.HashMap;
import java.util.List;

public class RaceTraits {
    private List<String> abilities;
    private HashMap<String, Integer> misc;
    private HashMap<String, Integer> stats;


    public RaceTraits() {

    }

    public RaceTraits(List<String> abilities, HashMap<String, Integer> misc, HashMap<String, Integer> stats) {
        this.abilities = abilities;
        this.misc = misc;
        this.stats = stats;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public HashMap<String, Integer> getMisc() {
        return misc;
    }

    public void setMisc(HashMap<String, Integer> misc) {
        this.misc = misc;
    }

    public HashMap<String, Integer> getStats() {
        return stats;
    }

    public void setStats(HashMap<String, Integer> stats) {
        this.stats = stats;
    }
}
