package com.dgtalize.dndcharmanager.model;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class Race implements IdentifiableItem {
    private String uid;

    private List<Integer> age;
    private String alignment;
    private List<Integer> baseWeight;
    private List<String> bonusLanguages;
    private String favoredClass;
    private List<Integer> height;
    private List<String> languages;
    private String name;
    private List<String> religion;
    private String size;
    private Integer speed;
    private RaceTraits traits;
    private String weightModifier;


    public Race() {

    }

    public Race(List<Integer> age, String alignment, List<Integer> baseWeight, List<String> bonusLanguages, String favoredClass, List<Integer> height, List<String> languages, String name, List<String> religion, String size, Integer speed, RaceTraits traits, String weightModifier) {
        this.age = age;
        this.alignment = alignment;
        this.baseWeight = baseWeight;
        this.bonusLanguages = bonusLanguages;
        this.favoredClass = favoredClass;
        this.height = height;
        this.languages = languages;
        this.name = name;
        this.religion = religion;
        this.size = size;
        this.speed = speed;
        this.traits = traits;
        this.weightModifier = weightModifier;
    }

    //region Getters/Setters

    @Override
    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Integer> getAge() {
        return age;
    }

    public void setAge(List<Integer> age) {
        this.age = age;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public List<Integer> getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(List<Integer> baseWeight) {
        this.baseWeight = baseWeight;
    }

    public List<String> getBonusLanguages() {
        return bonusLanguages;
    }

    public void setBonusLanguages(List<String> bonusLanguages) {
        this.bonusLanguages = bonusLanguages;
    }

    public String getFavoredClass() {
        return favoredClass;
    }

    public void setFavoredClass(String favoredClass) {
        this.favoredClass = favoredClass;
    }

    public List<Integer> getHeight() {
        return height;
    }

    public void setHeight(List<Integer> height) {
        this.height = height;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getReligion() {
        return religion;
    }

    public void setReligion(List<String> religion) {
        this.religion = religion;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public RaceTraits getTraits() {
        return traits;
    }

    public void setTraits(RaceTraits traits) {
        this.traits = traits;
    }

    public String getWeightModifier() {
        return weightModifier;
    }

    public void setWeightModifier(String weightModifier) {
        this.weightModifier = weightModifier;
    }

    //endregion

    @Override
    public String toString() {
        return this.name;
    }
}
