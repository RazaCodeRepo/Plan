package com.example.kontrol.plan.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kontrol on 4/13/2018.
 */

public class User {

    private String name;
    private String phoneNumber;
    private String uID;

    public User (String number, String id, String name) {
        phoneNumber = number;
        uID = id;
        this.name = name;
    }

    public User (String number, String id) {
        phoneNumber = number;
        uID = id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }


    public void setPhoneNumber(String number){
        phoneNumber = number;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setuID(String id){
        uID = id;
    }

    public String getuID(){
        return uID;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("number", phoneNumber);
        result.put("uid", uID);
        result.put("name", name);
        return result;
    }
}
