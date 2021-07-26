package com.example.binuspostscheduler.models;

import java.util.ArrayList;

public class PostedSchedule {
    private String time, id;
    private String description, video, user_id;
    private ArrayList<String> hashtags, selected_id,image;

    public PostedSchedule(){

    }

    public PostedSchedule(String time, String id, String description, String video, String user_id, ArrayList<String> hashtags, ArrayList<String> selected_id, ArrayList<String> image) {
        this.time = time;
        this.id = id;
        this.description = description;
        this.video = video;
        this.user_id = user_id;
        this.hashtags = hashtags;
        this.selected_id = selected_id;
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

    public ArrayList<String> getSelected_id() {
        return selected_id;
    }

    public void setSelected_id(ArrayList<String> selected_id) {
        this.selected_id = selected_id;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }
}
