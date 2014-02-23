package ru.eternalkaif.soundsofnature.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

public class DownloadSongCommand extends BaseCommand {

    private static final String TAG = "TestActionCommand";

    public DownloadSongCommand(String login, String password) {
        this.login = login;
        this.password = password;
    }

    private DownloadSongCommand(Parcel in) {
        login = in.readString();
        password = in.readString();
        Log.d("Command", "is runing");
    }

    private String login;
    private String password;

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(login);
        parcel.writeString(password);
    }

    public static final Parcelable.Creator<DownloadSongCommand> CREATOR = new Parcelable.Creator<DownloadSongCommand>() {
        public DownloadSongCommand createFromParcel(Parcel in) {
            return new DownloadSongCommand(in);
        }

        @Override
        public DownloadSongCommand[] newArray(int i) {
            return new DownloadSongCommand[i];
        }
    };


}
