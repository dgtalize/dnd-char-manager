package com.dgtalize.dndcharmanager.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserMeta {
    private String name;
    private String email;
    private String imageUrl;

    public UserMeta() {

    }

    //region Getters/Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    //endregion

    /**
     * Initialize the UserMeta properties in case they are empty, taken values from the
     * Firebase UserMeta
     * @param fbUser
     */
    public void initializeValues(FirebaseUser fbUser) {
        if(TextUtils.isEmpty(this.name)){
            this.setName(fbUser.getDisplayName());
        }
        if(TextUtils.isEmpty(this.email)){
            this.setEmail(fbUser.getEmail());
        }
        if(TextUtils.isEmpty(this.imageUrl)){
            this.setImageUrl(fbUser.getPhotoUrl().toString());
        }
    }
}
