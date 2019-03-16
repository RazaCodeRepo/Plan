package com.example.kontrol.plan.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kontrol on 1/19/2018.
 */

public class Message {

    private String text;
    private String name;
    private String photoUrl;
    private String msgTime;

    public Message() {
    }

    public Message(String text, String name, String photoUrl, String time) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        msgTime = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setTime(String str) {
        msgTime = str;
    }

    public String getMsgTime(){
        return msgTime;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("name", name);
        result.put("photoUrl", photoUrl);
        result.put("msgTime", msgTime);

        return result;
    }

}
