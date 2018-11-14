package com.example.scaledrone.Packages.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.scaledrone.Packages.Objects.Chat;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String userID;

    private String username;

    private byte[] profile_picture;

    public User(){
        userID = UUID.randomUUID().toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getProfile_picture() {
        Bitmap bmp = BitmapFactory.decodeByteArray(profile_picture, 0, profile_picture.length);

        return bmp;
    }

    public byte[] getProfile_picture_byte(){
        return profile_picture;
    }

    public void setProfile_picture(byte[] profile_picture) {
        this.profile_picture = profile_picture;
    }


}
