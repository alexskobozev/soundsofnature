package ru.eternalkaif.soundsofnature.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class PlaySoundCommand extends BaseCommand {

    public static final Parcelable.Creator<PlaySoundCommand> CREATOR = new Parcelable.Creator<PlaySoundCommand>() {
        public PlaySoundCommand createFromParcel(Parcel in) {
            return new PlaySoundCommand(in);
        }

        @NotNull
        @Override
        public PlaySoundCommand[] newArray(int i) {
            return new PlaySoundCommand[i];
        }
    };


    private static final String TAG = "PlaySoundCommand";
    private String url;

    public PlaySoundCommand(String url) {
        this.url = url;
    }


    private PlaySoundCommand(Parcel in) {

        url = in.readString();
        Log.d("Command", "is runing");
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {

        Bundle data = new Bundle();

        int progress = 0;
        sendProgress(progress);


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
    }
}
