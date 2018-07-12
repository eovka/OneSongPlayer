package pl.pisze_czytam.mediaplayertest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import java.util.concurrent.TimeUnit;

import pl.pisze_czytam.mediaplayertest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMainBinding bind;
    MediaPlayer song;
    AudioManager audioManager;
    private int actualTime;
    private int finalTime;
    boolean isPlaying;
    int rewindTime = 15000;
    private Handler myHandler = new Handler();
    private static final String TIME_KEY = "currentTime";
    private static final String PLAYING_KEY = "isPlaying";

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            bind.playButton.setImageResource(R.drawable.button_play);
            isPlaying = false;
            bind.songInfo.setSelected(false);
            actualTime = 0;
            song.seekTo(actualTime);
        }
    };

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    song.stop();
                    releaseMediaPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    song.pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    song.pause();
                    break;
            }
        }
    };

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        song = MediaPlayer.create(this, R.raw.kochankowie_gwiezdnych_przestrzeni_kino_w_elblagu);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (savedInstanceState != null) {
            bind.actualTime.setText(savedInstanceState.getInt(TIME_KEY));
            isPlaying = savedInstanceState.getBoolean(PLAYING_KEY);
        } else {
            actualTime = song.getCurrentPosition();
            isPlaying = false;
        }

        finalTime = song.getDuration();
        bind.seekBar.setMax(finalTime);

        bind.playButton.setOnClickListener(this);
        bind.backButton.setOnClickListener(this);
        bind.forButton.setOnClickListener(this);
        bind.restartButton.setOnClickListener(this);

        bind.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (song != null && fromUser){
                    song.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
                if (!isPlaying) {
                    int result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        song.start();
                        song.setOnCompletionListener(completionListener);
                        bind.playButton.setImageResource(R.drawable.button_play_purple);
                        updatingActualTime();
                        showSongTime();
                        bind.songInfo.setSingleLine(true);
                        bind.songInfo.setFreezesText(true);
                        bind.songInfo.setText(getString(R.string.kino_w_elblagu_song));
                        bind.songInfo.setSelected(true);
                        bind.songInfo.setFocusable(true);
                        bind.songInfo.setFocusableInTouchMode(true);
                        bind.songInfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        isPlaying = true;
                    }
                } else {
                    bind.playButton.setImageResource(R.drawable.button_pause_grey);
                    song.pause();
                    bind.songInfo.setSelected(false);
                    isPlaying = false;
                }
                break;
            case R.id.back_button:
                actualTime -= rewindTime;
                song.seekTo(actualTime);
                bind.seekBar.setProgress(actualTime);
                bind.backButton.setImageResource(R.drawable.backward_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        bind.backButton.setImageResource(R.drawable.backward_grey);
                    }
                }, 200);
                break;
            case R.id.for_button:
                actualTime += rewindTime;
                song.seekTo(actualTime);
                bind.seekBar.setProgress(actualTime);
                // show actual time and reveal song duration when forwarding before clicking play
                updatingActualTime();
                showSongTime();
                bind.forButton.setImageResource(R.drawable.forward_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        bind.forButton.setImageResource(R.drawable.forward_grey);
                    }
                }, 200);
                break;
            case R.id.restart_button:
                actualTime = 0;
                bind.seekBar.setProgress(actualTime);
                song.seekTo(actualTime);
                if (isPlaying) {
                    song.start();
                }
                bind.restartButton.setImageResource(R.drawable.reload_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        bind.restartButton.setImageResource(R.drawable.reload_grey);
                    }
                }, 200);
        }
    }
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            actualTime = song.getCurrentPosition();
            updatingActualTime();
        }
    };

    @SuppressLint("DefaultLocale")
    public void updatingActualTime() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(actualTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(actualTime) - TimeUnit.MINUTES.toSeconds(minutes);
        if (seconds % 60 < 10) {
            bind.actualTime.setText(String.format("%d:0%d", minutes, seconds));
        } else {
            bind.actualTime.setText(String.format("%d:%d", minutes, seconds));
        }
        bind.seekBar.setProgress(actualTime);
        myHandler.postDelayed(UpdateSongTime,50);
    }

    @SuppressLint("DefaultLocale")
    public void showSongTime() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(finalTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(finalTime) - TimeUnit.MINUTES.toSeconds(minutes);
        if (seconds % 60 < 10) {
            bind.songTime.setText(String.format("%d:0%d", minutes, seconds));
        } else {
            bind.songTime.setText(String.format("%d:%d", minutes, seconds));
        }
    }
    private void releaseMediaPlayer() {
        if (song != null) {
            song.release();
            song = null;
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }
    public void onResume() {
        super.onResume();
        actualTime = song.getCurrentPosition();
        bind.seekBar.setProgress(actualTime);
        showSongTime();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TIME_KEY, song.getCurrentPosition());
        outState.putBoolean(PLAYING_KEY, isPlaying);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bind.actualTime.setText(savedInstanceState.getInt(TIME_KEY));
        isPlaying = savedInstanceState.getBoolean(PLAYING_KEY);
    }
}