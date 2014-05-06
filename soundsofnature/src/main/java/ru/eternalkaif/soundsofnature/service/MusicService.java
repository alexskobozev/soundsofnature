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
import android.os.IBinder;
import android.os.PowerManager;
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
    public static final String ACTION_PLAY = "START_PLAY";
    private static final String TAG = "MusicService";
    private static final String PLAYSONG = "playsong";
    private static final String ACTION_PAUSE = "action_pause";
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

    public MusicService() {
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
                if (intent.getExtras() != null) {
                    url = intent.getStringExtra(SONG_URL);
                    songName = intent.getStringExtra(SONG_NAME);
                    if (intent.getAction().equals(ACTION_PLAY)) {
                        play(url);
                    }
                }
            }

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {


                if (intent.getAction().equals(ACTION_PLAY)) {
                    url = intent.getExtras().getString(PLAYSONG);
                    Log.d(TAG, "received " + " intent action = " + intent.getAction() + " URL = " + url);
                    play(intent.getExtras().getString(SONG_URL));
                    getMp().pause();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);

        return Service.START_STICKY;
    }


    public boolean isPlaying() {
        return mPrepared && getMp().isPlaying();
    }

    public void pause() {
        Log.d(TAG, "pause");
        getMp().pause();
    }

    public void resumePlay() {
        Log.d(TAG, "resumePlay");
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
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
            contentView.setTextViewText(R.id.tv_action_name, getResources().getString(R.string.music_player));
            contentView.setTextViewText(R.id.songName, songName);

            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(
                    getApplicationContext());
            notificationCompat
                    .setContentTitle(getResources().getString(R.string.music_player))
                    .setContentText(songName)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setContent(contentView);


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
            startForeground(123, notificationCompat.build());


        }
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
            wifiLock.release();
            stopForeground(true);
            mPrepared = false;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        stop();
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
