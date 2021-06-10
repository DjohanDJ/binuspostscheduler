package com.example.binuspostscheduler.models;

import java.util.ArrayList;

public class PostedSchedule {
    private String time, id;
    private String description, image, video;
    private ArrayList<String> hashtags, selected_id;

    public PostedSchedule(){

    }

    public PostedSchedule(String id, String time, String description, String imageLink, String videoLink, ArrayList<String> hashtags, ArrayList<String> selectedId) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.image = imageLink;
        this.video = videoLink;
        this.hashtags = hashtags;
        this.selected_id = selectedId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
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
}
