package ru.eternalkaif.soundsofnature.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

public class PlayerActivity extends BaseActivity {

    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            url = b.getString("songurl");
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PlaceholderFragment.newInstance(url))
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
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        private static final String SONGURL = "songurl";
        public static final String TAG = "mediaplayer";
        private Button buttonPlayPause;
        private SeekBar seekBarProgress;
        private MediaPlayer mp;

        private int mediaFileLengthInMilliseconds;

        private final Handler handler = new Handler();
        private String url;

        public static PlaceholderFragment newInstance(String url) {
            PlaceholderFragment pf = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(SONGURL, url);
            pf.setArguments(args);
            return pf;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                url = getArguments().getString(SONGURL);
            }

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
//            primarySeekBarProgressUpdater();

            return rootView;
        }

        private void primarySeekBarProgressUpdater() {
            seekBarProgress.setProgress((int) (((float) mp.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
            if (mp.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                handler.postDelayed(notification, 1000);
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_playpause) {
                if (!mp.isPlaying()) {
                    mp.start();
                } else {
                    mp.pause();
                }

            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
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
            //Just for debugging
            mp.stop();
        }

        @Override
        public void onStop() {
            super.onStop();
            mp.release();

        }

        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            seekBarProgress.setSecondaryProgress(i);
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            Toast.makeText(getActivity(), "MediaPlayer Error (" + i + "," + i2 + ")", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
