package ru.eternalkaif.soundsofnature.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.objects.Soundlist;

public class GetSongListCommand extends BaseCommand {

    public static final Parcelable.Creator<GetSongListCommand> CREATOR = new Parcelable.Creator<GetSongListCommand>() {
        public GetSongListCommand createFromParcel(Parcel in) {
            return new GetSongListCommand(in);
        }

        @Override
        public GetSongListCommand[] newArray(int i) {
            return new GetSongListCommand[i];
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

        Bundle data = new Bundle();

        Random rnd = new Random();

        int progress = 0;
        sendProgress(progress);
        String json = null;

        InputStream is = null;
        ObjectMapper objectMapper = new ObjectMapper();
        Soundlist soundlist = null;



        try {
            is = context.getResources().openRawResource(R.raw.songlist);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            soundlist = objectMapper.readValue(json, Soundlist.class);
            sendProgress(100);
            data.putString("data", soundlist.toString());
            notifySuccess(data);
        } catch (IOException e) {
            data.putString("error", "IOexception!");
            notifyFailure(data);
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