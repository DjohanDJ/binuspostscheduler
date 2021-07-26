package com.example.binuspostscheduler.models;

import java.util.ArrayList;

public class NewSchedule {
    private String description,time,user_id,video,type;
    private ArrayList<String> hashtags,image;
    private ArrayList<Account> selected_id;
    private String id;
    private ArrayList<String> selected_ids;
    public NewSchedule(String description, String time, String user_id, String video, String type, ArrayList<String> hashtags, ArrayList<String> image, ArrayList<Account> selected_id, String id) {
        this.description = description;
        this.time = time;
        this.user_id = user_id;
        this.video = video;
        this.type = type;
        this.hashtags = hashtags;
        this.id = id;
        this.image = image;
        this.selected_id = selected_id;
    }

    public NewSchedule(String description, String time, String user_id, String video, String type, ArrayList<String> hashtags, ArrayList<String> image, String id, ArrayList<String> selected_ids) {
        this.description = description;
        this.time = time;
        this.user_id = user_id;
        this.video = video;
        this.type = type;
        this.hashtags = hashtags;
        this.image = image;
        this.id = id;
        this.selected_ids = selected_ids;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public ArrayList<Account> getSelected_id() {
        return selected_id;
    }

    public void setSelected_id(ArrayList<Account> selected_id) {
        this.selected_id = selected_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getSelected_ids() {
        return selected_ids;
    }

    public void setSelected_ids(ArrayList<String> selected_ids) {
        this.selected_ids = selected_ids;
    }
}
