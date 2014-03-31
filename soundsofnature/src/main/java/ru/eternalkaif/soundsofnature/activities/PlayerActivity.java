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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

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
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

        private static final String SONGURL = "songurl";
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
            buttonPlayPause = (Button) getActivity().findViewById(R.id.btn_playpause);
            buttonPlayPause.setOnClickListener(this);

            seekBarProgress = (SeekBar) getActivity().findViewById(R.id.seekBar);
            seekBarProgress.setOnTouchListener(this);

            mp = new MediaPlayer();
            mp.setOnBufferingUpdateListener(this);
            mp.setOnCompletionListener(this);

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
                try {
                    mp.setDataSource(url);
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaFileLengthInMilliseconds = mp.getDuration();
                if (!mp.isPlaying()) {
                    mp.start();
                } else {
                    mp.stop();
                }
                primarySeekBarProgressUpdater();
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
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            seekBarProgress.setSecondaryProgress(i);
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }
    }

}
