package org.example;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class Main {
    public static Boolean inDataArray(JSONArray d, String key, String Value){
        for (int i = 0; i < d.length(); i++){
            if (Objects.equals(d.getJSONObject(i).optString("key"), Value)) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) throws IOException {
        // Jackson Library for JSON reading
        ObjectMapper mapper = new ObjectMapper();
        // Prevent error upon skipping several variables in Song.class compared to Spotify JSON
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // For later: read in all files in folder that contain Streaming_History_Audio
        File main = new File("E:\\Coding\\Projects-Java\\Spotify-Analyzer\\src\\main\\resources\\Streaming_History_Audio_2014-2015_0.json");

//        File test = new File("E:\\Coding\\Projects-Java\\Spotify-Analyzer\\src\\main\\resources\\test.json");
//        JsonNode testNode = mapper.readTree(test);
//        JsonNode jsonNode = mapper.readTree(new File("E:\\Coding\\Projects-Java\\Spotify-Analyzer\\src\\main\\resources\\Streaming_History_Audio_2014-2015_0.json"));

        // Array of all Song class objects found in JSON
        Song[] myObjects = mapper.readValue(main, Song[].class);

        // Processed data
        JSONArray data = new JSONArray();

//        JSONObject song1 = new JSONObject();
//        song1.put("2014", new String[]{"hi", "yellow"});
//        System.out.println(song1);
//        System.exit(0);

//        ObjectMapper mapper2 = new ObjectMapper();
//        InputStream is = Test.class.getResourceAsStream("/test.json");
//        testObj = mapper.readValue(is, Test.class);

        for (Song song: myObjects){
            boolean addYear = true;
            String songYear = song.getYear();
            String songName = song.getMaster_metadata_track_name();
            String albumName = song.getMaster_metadata_album_album_name();
            String artistName = song.getMaster_metadata_album_artist_name();
            float msPlayed = song.getMs_played();
            String spotifyUrl = song.getSpotify_track_uri();
            boolean skipped = song.isSkipped();
            for (int i = 0; i < data.length(); i++){
                // If year does not exist in data, create a new entry and append it to data
                // if year is present, do the following: add or update song data to Year JSONObject
                JSONObject yearData = data.getJSONObject(i);
                if (Objects.equals(yearData.optString("Year"), songYear)){
                    addYear = false;
                    JSONArray songList = (JSONArray)data.getJSONObject(i).get("Song Data");
                    boolean addSong = true;
                    for (int j = 0; j < songList.length(); j++){
                        JSONObject songData = songList.getJSONObject(j);
                        if (Objects.equals(songData.optString("Song"), songName)){
                            addSong = false;
                            // System.out.println("Song is present");
                            // update
                            songData.put("Times Played", songData.getInt("Times Played") + 1);
                            if (skipped){
                                songData.put("Times Skipped", songData.getInt("Times Skipped") + 1);
                            }
                            songData.put("Total Minutes Listened", songData.getFloat("Total Minutes Listened") + msPlayed/60000);
                            yearData.put("Songs Played", yearData.getInt("Songs Played") + 1);
                            break;
                        }
                    }
                    if (addSong){
                        JSONObject s = new JSONObject();
                        s.put("Song", songName);
                        s.put("Album", albumName);
                        s.put("Artist", artistName);
                        s.put("URL", spotifyUrl);
                        s.put("Total Minutes Listened", msPlayed/60000);
                        s.put("Times Played", 1);
                        s.put("Times Skipped", (skipped) ?1:0);
//                        System.out.println(s);
                        yearData.optJSONArray("Song Data").put(s);
                        yearData.put("Songs Played", yearData.getInt("Songs Played") + 1);

                    }
                    break;
                }
            }

            if (addYear){
                // Create a Year Object that contains a SongData Array whose first entry is a Song Object
                JSONObject y = new JSONObject();
                JSONArray songData = new JSONArray();
                JSONObject s = new JSONObject();
                s.put("Song", songName);
                s.put("Album", albumName);
                s.put("Artist", artistName);
                s.put("URL", spotifyUrl);
                s.put("Total Minutes Listened", msPlayed/60000);
                s.put("Times Played", 1);
                s.put("Times Skipped", (skipped) ? 1:0);
                songData.put(s);
                y.put("Year", songYear);
                y.put("Songs Played", 1);
                y.put("Song Data", songData);
                data.put(y);
            }
        }
//        System.out.println(data.toString(4));
        for (int i = 0; i < data.length(); i++){
            System.out.println(data.getJSONObject(i).optString("Year") + ", Songs Played: "+ data.getJSONObject(i).optString("Songs Played"));
        }
    }


}

