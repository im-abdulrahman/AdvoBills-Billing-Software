package com.example.grownanded.Chat_Section;

import java.util.Date;

public class Chat_List_Model {

    String id,name,image,timeStamp,lastMsg;
    public Date dateObject;

    public Chat_List_Model() {
    }

    public Chat_List_Model(String id, String name, String image, String timeStamp, String lastMsg) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.timeStamp = timeStamp;
        this.lastMsg = lastMsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
