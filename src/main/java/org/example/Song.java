package org.example;

public class Song {
    public String getTs() {
        return ts;
    }

    public String getYear(){
        return ts.substring(0, 4);
    }

    public int getMs_played() {
        return ms_played;
    }

    public String getMaster_metadata_track_name() {
        return master_metadata_track_name;
    }

    public String getMaster_metadata_album_artist_name() {
        return master_metadata_album_artist_name;
    }

    public String getMaster_metadata_album_album_name() {
        return master_metadata_album_album_name;
    }

    public String getSpotify_track_uri() {
        return spotify_track_uri;
    }

    public boolean isSkipped() {
        return skipped;
    }

    /*
        timestamp, song name, artist name, album name, spotify track url
     */
    private String ts;
    private int ms_played;
    private String master_metadata_track_name;
    private String master_metadata_album_artist_name;
    private String master_metadata_album_album_name;
    private String spotify_track_uri;
    private boolean skipped;
}
