package ru.eternalkaif.soundsofnature.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;

import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.activities.PlayerActivity;

public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public static final String SONG_URL = "songurl";
    public static final String SONG_NAME = "songname";
    public static final String ACTION_PLAY = "START_PLAY";
    private String url;
    private boolean isPlaying;
    private String songName;
    private MediaPlayer mediaPlayer;
    private WifiManager.WifiLock wifiLock;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    public void initMediaPlayer() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getExtras() != null) {
                url = intent.getStringExtra(SONG_URL);
                songName = intent.getStringExtra(SONG_NAME);
                if (intent.getAction().equals(ACTION_PLAY)) {
                    play();
                }
            }
        }

        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        return Service.START_STICKY;
    }

    private void play() {
        if (!isPlaying) {
            isPlaying = true;
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(
                    getApplicationContext());
            notificationCompat
                    .setContentTitle(getResources().getString(R.string.music_player))
                    .setContentText(songName)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent);

            //  mediaPlayer = MediaPlayer.create(this, Uri.parse(url));
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, Uri.parse(url));
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            wifiLock.acquire();
            startForeground(123, notificationCompat.build());


        }
    }

    private void stop() {
        if (isPlaying) {
            isPlaying = false;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            wifiLock.release();
            stopForeground(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
