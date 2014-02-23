package ru.eternalkaif.soundsofnature.handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import ru.eternalkaif.soundsofnature.BaseApplication;


@SuppressLint("ParcelCreator")
public abstract class BaseCommand implements Parcelable {

    public static String EXTRA_PROGRESS = BaseApplication.PACKAGE.concat(".EXTRA_PROGRESS");

    public static final int RESPONSE_SUCCESS = 0;

    public static final int RESPONSE_FAILURE = 1;

    public static final int RESPONSE_PROGRESS = 2;

    private ResultReceiver myCallback;

    protected volatile boolean cancelled = false;

    public final void execute(Intent intent, Context context, ResultReceiver callback) {
        this.myCallback = callback;
        doExecute(intent, context, callback);
        Log.d("BaseCommand", "execute");
    }

    protected abstract void doExecute(Intent intent, Context context, ResultReceiver callback);

    protected void notifySuccess(Bundle data) {
        sendUpdate(RESPONSE_SUCCESS, data);
        Log.d("BaseCommand", "notifySuccess");

    }

    private void sendUpdate(int resultCode, Bundle data) {
        if (myCallback != null) {
            myCallback.send(resultCode, data);
            Log.d("BaseCommand", "sendUpdate");

        }
    }

    protected void notifyFailure(Bundle data) {
        sendUpdate(RESPONSE_FAILURE, data);
        Log.d("BaseCommand", "notifyFailure");

    }

    protected void sendProgress(int progress) {
        Bundle b = new Bundle();
        b.putInt(EXTRA_PROGRESS, progress);
        sendUpdate(RESPONSE_PROGRESS, b);
        Log.d("BaseCommand", "sendProgress");

    }

    public synchronized void cancel() {
        cancelled = true;
    }
}
