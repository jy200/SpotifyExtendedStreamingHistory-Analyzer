package org.example;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Main {
    public static void main(String[] args) throws IOException {
        // Processed output data
        JSONArray data = new JSONArray();

        // Jackson Library for JSON reading
        ObjectMapper mapper = new ObjectMapper();
        // Prevent error upon skipping several variables in Song.class compared to Spotify JSON
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        File folder = new File("E:\\Coding\\Projects-Java\\Spotify-Analyzer\\src\\main\\resources");
        File[] folderList = folder.listFiles();
        List<File> parseFolder = new ArrayList<>();
        assert folderList != null;
        int fileNum = 0;
        for (File f: folderList){
            String fileName = f.getName();
            if (fileName.contains("Streaming_History_Audio")){
                parseFolder.add(f);
            }
            // limit is 4, num + 1
            if (fileNum>3){
                break;
            }else{
                fileNum++;
            }
        }

        List<Song> myObjects = new ArrayList<>();
        for (File f : parseFolder){
            myObjects.addAll(Arrays.asList(mapper.readValue(f, Song[].class)));
        }
//        Arrays.asList(mapper.readValue(main, Song[].class));



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
                            // Song is present: update array
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
                        // Song is not present, create new object and add to array
                        JSONObject s = new JSONObject();
                        s.put("Song", songName);
                        s.put("Album", albumName);
                        s.put("Artist", artistName);
                        s.put("URL", spotifyUrl);
                        s.put("Total Minutes Listened", msPlayed/60000);
                        s.put("Times Played", 1);
                        s.put("Times Skipped", (skipped) ?1:0);
                        yearData.optJSONArray("Song Data").put(s);
                        yearData.put("Songs Played", yearData.getInt("Songs Played") + 1);

                    }
                    break;
                }
            }

            if (addYear){
                // Year not present
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

