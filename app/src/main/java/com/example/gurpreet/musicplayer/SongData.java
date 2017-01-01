package com.example.gurpreet.musicplayer;

/**
 * Created by Gurpreet on 27/12/2016.
 */

public class SongData {

    public  String artists;
    public String title;
    public String duration;

    public SongData(String artists, String title, String duration) {
        this.setArtists(artists);
        this.setTitle(title);
        this.setDuration(duration);
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
