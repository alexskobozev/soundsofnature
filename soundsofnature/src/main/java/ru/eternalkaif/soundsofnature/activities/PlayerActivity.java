package ru.eternalkaif.soundsofnature.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.eternalkaif.soundsofnature.BaseActivity;
import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.service.MusicService;

public class PlayerActivity extends BaseActivity {


    private String soundUrl;
    private String soundName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Bundle b = getIntent().getExtras();
        soundUrl = b.getString(MusicService.SONG_URL);
        soundName = b.getString(MusicService.SONG_NAME);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PlaceholderFragment.newInstance(soundUrl, soundName))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener,
            View.OnTouchListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener {

        public static final String TAG = "mediaplayer";
        public static final String SONGURL = "songurl";
        private static final long EVERY_SECOND = 1000;
        private final Handler handler = new Handler();
        private Button buttonPlayPause;
        private SeekBar seekBarProgress;
        private MediaPlayer mp;
        private int mediaFileLengthInMilliseconds;
        private boolean mBound;
        private MusicService mService;

        private boolean isPaused;
        private LocalBroadcastManager mLocalBroadcastManager;
        private String songUrl;
        private String songName;

        @NotNull
        public static PlaceholderFragment newInstance(String soundUrl, String soundName) {

            PlaceholderFragment pf = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(MusicService.SONG_URL, soundUrl);
            args.putString(MusicService.SONG_NAME, soundName);
            pf.setArguments(args);
            return pf;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

            if (getArguments() != null) {
                Intent broadcastIntent = new Intent(MusicService.ACTION_PLAY);
                mLocalBroadcastManager.sendBroadcast(broadcastIntent);
                songUrl = getArguments().getString(MusicService.SONG_URL);
                songName = getArguments().getString(MusicService.SONG_NAME);

            }


        }

        @Override
        public void onStart() {
            super.onStart();


        }

        @Nullable
        @Override
        public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_player, container, false);
            buttonPlayPause = (Button) rootView.findViewById(R.id.btn_playpause);
            buttonPlayPause.setOnClickListener(this);

            seekBarProgress = (SeekBar) rootView.findViewById(R.id.seekBar);
            seekBarProgress.setOnTouchListener(this);
            Log.d(TAG, "callings service getArgs " + getArguments() + " mBound " + mBound);

//            if (getArguments() != null) {


//                if (mBound) {
//                    mService.play(url);
//                }
//                Intent intent = new Intent(getActivity(), MusicService.class);
//                intent.setAction(MusicService.ACTION_PLAY);
//                intent.putExtra(MusicService.SONG_URL, url);
//                getActivity().startService(intent);
//            }
            //  mp = MediaPlayer.create(getActivity(), Uri.parse(url));
//            mp = new MediaPlayer();
//
//
//            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mp.setOnBufferingUpdateListener(this);
//            mp.setOnCompletionListener(this);
//            mp.setOnErrorListener(this);
//            mediaFileLengthInMilliseconds = mp.getDuration();
//            mp.start();
//            Log.d(TAG, "Media player started on link " + url);

            return rootView;
        }

        @Override
        public void onClick(@NotNull View view) {
            if (view.getId() == R.id.btn_playpause) {

                Intent pauseIntent;
                if (!MusicService.getInstance().isPrepared()) {
                    Intent serviceIntent = new Intent(getActivity(), MusicService.class);
                    serviceIntent.setAction(MusicService.ACTION_PLAY);
                    serviceIntent.putExtra(MusicService.SONG_URL, songUrl);
                    getActivity().startService(serviceIntent);

                } else if (MusicService.getInstance().isPlaying()) {
                    pauseIntent = new Intent(MusicService.ACTION_PAUSE);
                    mLocalBroadcastManager.sendBroadcast(pauseIntent);
                    buttonPlayPause.setBackgroundResource(R.drawable.ic_play);
                } else {
                    pauseIntent = new Intent(MusicService.ACTION_RESUME);
                    mLocalBroadcastManager.sendBroadcast(pauseIntent);
                    buttonPlayPause.setBackgroundResource(R.drawable.ic_pause);
                }
            }
        }

        @Override
        public boolean onTouch(@NotNull View view, MotionEvent motionEvent) {
            if (view.getId() == R.id.seekBar) {
                if (mp.isPlaying()) {
                    SeekBar seekBar = (SeekBar) view;
                    int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * seekBar.getProgress();
                    mp.seekTo(playPositionInMillisecconds);
                }
            }
            return false;
        }

        @Override
        public void onPause() {
            super.onPause();
            isPaused = true;
            handler.removeCallbacks(onEverySecond);

        }

        @Override
        public void onResume() {
            super.onResume();
            isPaused = false;
            if (MusicService.getInstance() != null)
                if (MusicService.getInstance().isPlaying()) {
                    buttonPlayPause.setBackgroundResource(R.drawable.ic_pause);
                } else {
                    buttonPlayPause.setBackgroundResource(R.drawable.ic_play);
                }
            handler.postDelayed(onEverySecond, EVERY_SECOND);
        }

        @Override
        public void onStop() {
            super.onStop();
//            if (mBound) {
//                getActivity().unbindService(mConnecion);
//                mBound = false;
//            }
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }

        Runnable onEverySecond = new Runnable() {
            @Override
            public void run() {
                if (mService != null) {
                    seekBarProgress.setProgress(mService.getProgress());
                    seekBarProgress.setSecondaryProgress(mService.getSecondaryProgress());
                    handler.postDelayed(onEverySecond, 1000);
                } else {
                    seekBarProgress.setProgress(0);
                }
                Log.d(TAG, "Progress checking");
            }
        };

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            Toast.makeText(getActivity(), "MediaPlayer Error (" + i + "," + i2 + ")", Toast.LENGTH_SHORT).show();
            return false;
        }


    }


}
