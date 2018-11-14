package com.example.scaledrone.Packages.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Room extends RealmObject {

    @PrimaryKey
    @Required
    private String roomName;

    private String roomPassword;

    @Required
    private Date timestamp;

    private byte[] roomImage;

    public Bitmap getRoomImage() {
        Bitmap bmp = null;
        if(roomImage!=null) {
            bmp = BitmapFactory.decodeByteArray(roomImage, 0, roomImage.length);
        }
        return bmp;
    }

    public void setRoomImage(byte[] roomImage) {
        this.roomImage = roomImage;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Room() {
        this.roomName = "";
        this.roomPassword = null;
        this.timestamp = new Date();
      //  this.roomImage = null;
    }
}
