package ru.eternalkaif.soundsofnature;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import ru.eternalkaif.soundsofnature.handler.BaseCommand;
import ru.eternalkaif.soundsofnature.handler.DownloadSongCommand;
import ru.eternalkaif.soundsofnature.handler.GetSongListCommand;
import ru.eternalkaif.soundsofnature.service.CommandExecutorService;

public class ServiceHelper {

    private Application application;

    @NotNull
    private ArrayList<ServiceCallbackListener> currentListeners = new ArrayList<ServiceCallbackListener>();

    @NotNull
    private SparseArray<Intent> pendingActivities = new SparseArray<Intent>();
    @NotNull
    private AtomicInteger idCounter = new AtomicInteger();

    public ServiceHelper(Application application) {
        this.application = application;
    }


    public void addListener(ServiceCallbackListener callbackListener) {
        currentListeners.add(callbackListener);
    }


    public void removeListener(ServiceCallbackListener callbackListener) {
        currentListeners.remove(callbackListener);
    }

    public int downloadSongAction(String url) {
        final int requestId = createId();
        Intent intent = createIntent(application, new DownloadSongCommand(url), requestId);
        return runRequest(requestId, intent);
    }

    public int getSongListAction() {
        Log.d("TestActionCommand","getsonglistaction in helper");
        final int requestId = createId();
        Intent intent = createIntent(application, new GetSongListCommand(), requestId);
        return runRequest(requestId, intent);
    }

    private int runRequest(int requestId, Intent intent) {
        pendingActivities.append(requestId, intent);
        application.startService(intent);
        return requestId;
    }

    public boolean isPending(int requestId) {
        return pendingActivities.get(requestId) != null;
    }

    @NotNull
    private Intent createIntent(final Context context, BaseCommand command, final int requestId) {

        Intent intent = new Intent(context, CommandExecutorService.class);
        intent.setAction(CommandExecutorService.ACTION_EXECUTE_COMMAND);

        intent.putExtra(CommandExecutorService.EXTRA_COMMAND, command);
        intent.putExtra(CommandExecutorService.EXTRA_REQUEST_ID, requestId);
        intent.putExtra(CommandExecutorService.EXTRA_STATUS_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                Intent originalIntent = pendingActivities.get(requestId);
                if (isPending(requestId)) {
                    if (resultCode != BaseCommand.RESPONSE_PROGRESS) {
                        pendingActivities.remove(requestId);
                    }

                    for (ServiceCallbackListener currentListener : currentListeners) {
                        if (currentListener != null) {
                            currentListener.onServiceCallBack(requestId, originalIntent, resultCode, resultData);
                        }
                    }
                }
            }
        });

        return intent;
    }

    private int createId() {

        return idCounter.getAndIncrement();
    }

    public void cancelCommand(int requestId) {
        Intent intent = new Intent(application, CommandExecutorService.class);
        intent.setAction(CommandExecutorService.ACTION_CANCEL_COMMAND);
        intent.putExtra(CommandExecutorService.EXTRA_REQUEST_ID, requestId);

        application.startService(intent);
        pendingActivities.remove(requestId);
    }

    public boolean check(@NotNull Intent requestIntent, Class<? extends BaseCommand> myclass) {

        Parcelable commandExtra = requestIntent.getParcelableExtra(CommandExecutorService.EXTRA_COMMAND);
        return commandExtra != null && commandExtra.getClass().equals(myclass);

    }
}
