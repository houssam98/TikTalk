package com.example.scaledrone.Packages.Objects;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Message extends RealmObject {

    @PrimaryKey
    private String messageID;

    private String text;

    @Ignore
    private MemberData data;

    @Ignore
    private boolean belongsToCurrentUser;

    private String roomName;

    private Date timestamp;

    private String owner;

    public Message(){
        this.messageID = UUID.randomUUID().toString();
        this.text = "";
        this.data = null;
        this.belongsToCurrentUser = false;
        this.roomName = "";
        this.owner = "";
        this.timestamp = new Date();
    }

    public Message(Message message){
        this.messageID = message.messageID;
        this.text = message.text;
        this.data = message.data;
        this.belongsToCurrentUser = message.belongsToCurrentUser;
        this.roomName = message.roomName;
        this.timestamp = message.timestamp;
    }

    public Message(String text, MemberData data, String roomName, boolean belongsToCurrentUser) {
        this.messageID = UUID.randomUUID().toString();
        this.text = text;
        this.data = data;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.roomName = roomName;
        this.timestamp = new Date();
    }


    public void setBelongsToCurrentUser(boolean belongsToCurrentUser) {
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }


    public String getText() {
        return text;
    }

    public MemberData getData() {
        return data;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString(){
        String time = getTimestamp().toString();
        String[] array = time.split(" ");
        String hour_min = array[3];
        return hour_min;
    }
}