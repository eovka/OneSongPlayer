package pl.pisze_czytam.mediaplayertest;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MediaPlayer song;
    SeekBar seekBar;
    TextView songInfo;
    TextView actualTimeView;
    TextView songTimeView;
    ImageView play;
    ImageView back;
    ImageView forward;
    ImageView restart;
    private int actualTime;
    private int finalTime;
    boolean isPlaying;
    int rewindTime;
    private Handler myHandler = new Handler();
    public static int oneTimeOnly = 0;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        song = MediaPlayer.create(this, R.raw.kochankowie_gwiezdnych_przestrzeni_kino_w_elblagu);
        isPlaying = false;
        rewindTime = 15000;

        seekBar = findViewById(R.id.seek_bar);
        songInfo = findViewById(R.id.song_info);
        actualTimeView = findViewById(R.id.actual_time);
        songTimeView = findViewById(R.id.song_time);
        play = findViewById(R.id.play_button);
        back = findViewById(R.id.back_button);
        forward = findViewById(R.id.for_button);
        restart = findViewById(R.id.restart_button);

        actualTime = song.getCurrentPosition();
        finalTime = song.getDuration();

        play.setOnClickListener(this);
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
        restart.setOnClickListener(this);

        songInfo.setSelected(true);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
                if (oneTimeOnly == 0) {
                    seekBar.setMax(finalTime);
                    oneTimeOnly = 1;
                }
                if (!isPlaying) {
                    play.setImageResource(R.drawable.button_play_purple);
                    song.start();
                    isPlaying = true;
                    updatingActualTime();
                    showSongTime();
                } else {
                    play.setImageResource(R.drawable.button_pause_grey);
                    song.pause();
                    isPlaying = false;
                }
                break;
            case R.id.back_button:
                actualTime -= rewindTime;
                song.seekTo(actualTime);
                seekBar.setProgress(actualTime);
                back.setImageResource(R.drawable.backward_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        back.setImageResource(R.drawable.backward_grey);
                    }
                }, 200);
                break;
            case R.id.for_button:
                actualTime += rewindTime;
                song.seekTo(actualTime);
                seekBar.setProgress(actualTime);
                forward.setImageResource(R.drawable.forward_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        forward.setImageResource(R.drawable.forward_grey);
                    }
                }, 200);
                break;
            case R.id.restart_button:
                actualTime = 0;
                seekBar.setProgress(actualTime);
                song.seekTo(actualTime);
                if (isPlaying) {
                    song.start();
                }
                restart.setImageResource(R.drawable.repeat_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        restart.setImageResource(R.drawable.repeat_grey);
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
            actualTimeView.setText(String.format("%d:0%d", minutes, seconds));
        } else {
            actualTimeView.setText(String.format("%d:%d", minutes, seconds));
        }
        seekBar.setProgress(actualTime);
        myHandler.postDelayed(UpdateSongTime,50);
    }

    @SuppressLint("DefaultLocale")
    public void showSongTime() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(finalTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(finalTime) - TimeUnit.MINUTES.toSeconds(minutes);
        if (seconds % 60 < 10) {
            songTimeView.setText(String.format("%d:0%d", minutes, seconds));
        } else {
            songTimeView.setText(String.format("%d:%d", minutes, seconds));
        }
    }
}
