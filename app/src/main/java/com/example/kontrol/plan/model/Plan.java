package com.example.kontrol.plan.model;

import java.util.ArrayList;

/**
 * Created by kontrol on 1/19/2018.
 */

public class Plan {

    private static int counter;

    private String name;
    private String plan_admin;
    private String exp_duration;
    private Message message;
    private int planID;
    private ArrayList<String> members;
    private String timeCreated;


    public Plan(){}

    public Plan(String admin, String name, String duration, ArrayList<String> mem, String time){
        counter++;
        this.name = name;
        exp_duration = duration;
        planID = counter;
        plan_admin = admin;
        members = mem;
        timeCreated = time;
    }

    public void setName(String n){
        name = n;
    }

    public String getName(){
        return name;
    }

    public void setExp_duration(String d) {
        exp_duration = d;
    }

    public void setPlan_admin(String admin){
        plan_admin = admin;
    }

    public String getExp_duration(){
        return exp_duration;
    }
    public void setMessage(String text, String name, String photoUrl, String time){
        message = new Message(text, name, photoUrl, time);
    }

    public Message getMessage(){
        return message;
    }

    public String getPlan_admin(){
        return plan_admin;
    }

    public ArrayList<String> getMembers(){
        return members;
    }

    public void setMembers(ArrayList<String> mem) {
        members = mem;
    }

    public void setTimeCreated(String time) {
        timeCreated = time;
    }

    public String getTimeCreated(){
        return timeCreated;
    }
}