package ru.eternalkaif.soundsofnature.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.activities.PlayerActivity;

public class MusicService extends Service implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener {
    public static final String SONG_URL = "songurl";
    public static final String SONG_NAME = "songname";
    public static final String ACTION_PLAY = "action_play";
    private static final String TAG = "MusicService";
    private static final String PLAYSONG = "playsong";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_RESUME = "action_resume";
    private static final String SHTORKA_ACTION = "notification_action";
    private static final int RESULT_CODE_PAUSE = 1;
    private static final int RESULT_CODE_PLAY = 2;
    private final IBinder mBinder = new LocalBinder();
    int progress;
    int secondaryProgress;
    @Nullable
    private String url;
    private boolean isPlaying;
    @Nullable
    private String songName;
    @Nullable
    private MediaPlayer mediaPlayer;
    private WifiManager.WifiLock wifiLock;
    private boolean mPrepared;
    private int bufferProgress;
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver broadcastReceiver;
    private static MusicService musicService;
    private boolean isPaused = false;

    public MusicService() {
        musicService = this;
    }

    public static MusicService getInstance() {
        return musicService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind " + intent.getPackage());
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        return mBinder;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    public void initMediaPlayer() {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        if (intent != null)
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ACTION_PAUSE)) {
                    Intent pauseIntent;
                    if (MusicService.getInstance().isPlaying()) {
                        pauseIntent = new Intent(MusicService.ACTION_PAUSE);
                        mLocalBroadcastManager.sendBroadcast(pauseIntent);
                        ResultReceiver r = intent.getParcelableExtra(ACTION_PAUSE);
                        r.send(RESULT_CODE_PAUSE, null);
                    } else {
                        pauseIntent = new Intent(MusicService.ACTION_RESUME);
                        mLocalBroadcastManager.sendBroadcast(pauseIntent);
                        ResultReceiver r = intent.getParcelableExtra(ACTION_PAUSE);
                        r.send(RESULT_CODE_PLAY, null);
                    }
                }

                if (intent.getExtras() != null) {
                    url = intent.getStringExtra(SONG_URL);
                    songName = intent.getStringExtra(SONG_NAME);
                    if (intent.getAction().equals(ACTION_PLAY)) {
                        Log.d(TAG, "Started playing " + url);
                        play(url);
                    } else {
                        Log.d(TAG, "Started playing, but action isnt action_play ");
                    }
                } else {
                    Log.d(TAG, "Started playing, but intent.getExtras = null ");
                }
            } else {
                Log.d(TAG, "Started playing, but intent.getAction = null ");
            }

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {


                if (intent.getAction().equals(ACTION_PLAY)) {
                    url = intent.getExtras().getString(PLAYSONG);
                    Log.d(TAG, "received " + " intent action = " + intent.getAction() + " URL = " + url);
                    play(intent.getExtras().getString(SONG_URL));
                    // getMp().pause();
                }

                if (intent.getAction().equals(ACTION_PAUSE)) {
                    Log.d(TAG, "received " + " intent action = " + intent.getAction() + " URL = " + url);
                    if (isPlaying) {
                        pause();
                    }

                }
                if (intent.getAction().equals(ACTION_RESUME)) {
                    Log.d(TAG, "received " + " intent action = " + intent.getAction() + " URL = " + url);
                    resumePlay();

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_RESUME);
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);

        return Service.START_STICKY;
    }


    public boolean isPlaying() {
        return mPrepared && getMp().isPlaying();
    }

    public boolean isPrepared() {
        return mPrepared;
    }

    public void pause() {
        Log.d(TAG, "pause");
        isPaused = true;
        getMp().pause();
    }

    public void resumePlay() {
        Log.d(TAG, "resumePlay");
        isPaused = false;
        if (mPrepared) {
            getMp().start();
        } else play(url);
    }

    /**
     * @return MediaPlayer at least in idle state
     */
    private MediaPlayer getMp() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
        }
        return mediaPlayer;
    }

    public void play(String url) {
        Log.d(TAG, "play");
        if (!isPlaying) {
            isPlaying = true;


            final RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
            contentView.setTextViewText(R.id.tv_action_name, getResources().getString(R.string.music_player));
            contentView.setTextViewText(R.id.songName, songName);
            contentView.setImageViewResource(R.id.btn_pause, R.drawable.ic_pause_dark);


            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(this, Uri.parse(url));
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            wifiLock.acquire();

            startForeground(123, initNotification(contentView).build());


        }
    }

    public NotificationCompat.Builder initNotification(final RemoteViews contentView) {


        Intent intent = new Intent(this, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        pauseIntent.putExtra(ACTION_PAUSE, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                //TODO: change notification icon
                super.onReceiveResult(resultCode, resultData);
                if (resultCode == 1) {
                    contentView.setImageViewResource(R.id.btn_pause, R.drawable.ic_play_dark);
                    initNotification(contentView);
                    startForeground(123, initNotification(contentView).build());
                } else if (resultCode == 2) {
                    contentView.setImageViewResource(R.id.btn_pause, R.drawable.ic_pause_dark);
                    initNotification(contentView);
                    startForeground(123, initNotification(contentView).build());
                }
            }
        });


        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(
                getApplicationContext());
        notificationCompat
                .setContentTitle(getResources().getString(R.string.music_player))
                .setContentText(songName)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContent(contentView);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.btn_pause, ppauseIntent);

        return notificationCompat;
    }

    public int getProgress() {
        int progress = 0;
        if (getMp().isPlaying()) {
            progress = (int) (((float) getMp().getCurrentPosition()
                    / getMp().getDuration()) * 100);
        }
        Log.d(TAG, "progress " + progress);

        return progress;
    }

    public int getSecondaryProgress() {
        Log.d(TAG, "secondary progress " + bufferProgress);
        return bufferProgress;
    }

    public void stop() {

        if (isPlaying) {
            isPlaying = false;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            stopForeground(true);
            mPrepared = false;
        }
    }

    public void kill() {
        if (isPlaying) {
            isPlaying = false;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            wifiLock.release();
            stopForeground(true);
            mPrepared = false;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        kill();
    }

    @Override
    public void onPrepared(@NotNull MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared");
        mPrepared = true;
        mediaPlayer.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferProgress = percent;
    }


    public class LocalBinder extends Binder {
        @NotNull
        public MusicService getService() {
            return MusicService.this;
        }
    }


}
