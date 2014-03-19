package ru.eternalkaif.soundsofnature.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import ru.eternalkaif.soundsofnature.objects.Soundlist;

public class GetSongListCommand extends BaseCommand {

    public static final Parcelable.Creator<DownloadSongCommand> CREATOR = new Parcelable.Creator<DownloadSongCommand>() {
        public DownloadSongCommand createFromParcel(Parcel in) {
            return new DownloadSongCommand(in);
        }

        @Override
        public DownloadSongCommand[] newArray(int i) {
            return new DownloadSongCommand[i];
        }
    };
    private static final String TAG = "TestActionCommand";

    public GetSongListCommand() {
    }


    private GetSongListCommand(Parcel in) {
        Log.d("Command", "is runing");
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        String json = null;

        InputStream is = null;
        ObjectMapper objectMapper = new ObjectMapper();
        Soundlist soundlist = null;

        try {
            is = context.getAssets().open("songlist");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            soundlist = objectMapper.readValue(json,Soundlist.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, soundlist != null ? soundlist.toString() : null);


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
