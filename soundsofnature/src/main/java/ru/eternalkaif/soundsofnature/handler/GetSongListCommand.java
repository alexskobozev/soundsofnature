package ru.eternalkaif.soundsofnature.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

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

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
