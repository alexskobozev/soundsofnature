package ru.eternalkaif.soundsofnature.tools;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import ru.eternalkaif.soundsofnature.db.SongItem;

public class JSONParser {

    static InputStream inputStream;
    static JSONObject jsonObject;
    static String json = "";

    public JSONParser(Context context) {


    }

    public ArrayList<SongItem> getListOfSongs(String url) {
        final ArrayList<SongItem> songList = new ArrayList<SongItem>();
        SongsHttpClient.get("songlist.json.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                //JSONObject songs = response.get("soundlinks");
                //songList.add();
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
                //get json from local
            }
        });
        return songList;
    }

}
