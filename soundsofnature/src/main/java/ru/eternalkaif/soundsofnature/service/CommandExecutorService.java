package ru.eternalkaif.soundsofnature.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.ResultReceiver;
import android.util.SparseArray;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.eternalkaif.soundsofnature.BaseApplication;
import ru.eternalkaif.soundsofnature.handler.BaseCommand;

public class CommandExecutorService extends Service {

    public static final String ACTION_EXECUTE_COMMAND = BaseApplication.PACKAGE.concat(".ACTION_EXECUTE_COMMAND");
    public static final String ACTION_CANCEL_COMMAND = BaseApplication.PACKAGE.concat(".ACTION_CANCEL_COMMAND");
    public static final String EXTRA_REQUEST_ID = BaseApplication.PACKAGE.concat(".EXTRA_REQUEST_ID");
    public static final String EXTRA_STATUS_RECEIVER = BaseApplication.PACKAGE.concat(".STATUS_RECEIVER");
    public static final String EXTRA_COMMAND = BaseApplication.PACKAGE.concat(".EXTRA_COMMAND");
    private static final int NUM_TREADS = 4;
    private ExecutorService executor = Executors.newFixedThreadPool(NUM_TREADS);

    private SparseArray<RunningCommand> runningCommands = new SparseArray<RunningCommand>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_EXECUTE_COMMAND.equals(intent.getAction())) {
            RunningCommand runningCommand = new RunningCommand(intent);

            synchronized (runningCommands) {
                runningCommands.append(getCommandId(intent), runningCommand);
            }
            executor.submit(runningCommand);
        }
        if (ACTION_CANCEL_COMMAND.equals(intent.getAction())) {
            RunningCommand runningCommand = runningCommands.get(getCommandId(intent));
            if (runningCommand != null) {
                runningCommand.cancel();
            }
        }
        return START_NOT_STICKY;
    }

    private int getCommandId(Intent intent) {
        return intent.getIntExtra(EXTRA_REQUEST_ID, -1);
    }

    private ResultReceiver getReceiver(Intent intent) {
        return intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
    }

    private BaseCommand getCommand(Intent intent) {
        return intent.getParcelableExtra(EXTRA_COMMAND);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    private class RunningCommand implements Runnable {

        private Intent intent;
        private BaseCommand command;

        public RunningCommand(Intent intent) {
            this.intent = intent;
            command = getCommand(intent);
        }

        public void cancel() {
            command.cancel();
        }


        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            command.execute(intent, getApplicationContext(), getReceiver(intent));
            shutdown();
        }

        private void shutdown() {
            synchronized (runningCommands) {
                runningCommands.remove(getCommandId(intent));
                if (runningCommands.size() == 0) {
                    stopSelf();
                }
            }
        }
    }
}
