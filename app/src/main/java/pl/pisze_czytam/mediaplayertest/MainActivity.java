package pl.pisze_czytam.mediaplayertest;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MediaPlayer song;
    SeekBar seekBar;
    TextView actualTimeView;
    TextView songTimeView;
    ImageView play;
    ImageView back;
    ImageView forward;
    private int actualTime;
    private int finalTime;
    boolean isPlaying;
    int rewindTime;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        song = MediaPlayer.create(this, R.raw.kochankowie_gwiezdnych_przestrzeni_kino_w_elblagu);
        isPlaying = false;
        rewindTime = 15000;

        seekBar = findViewById(R.id.seek_bar);
        actualTimeView = findViewById(R.id.actual_time);
        songTimeView = findViewById(R.id.song_time);
        play = findViewById(R.id.play_button);
        back = findViewById(R.id.back_button);
        forward = findViewById(R.id.for_button);

        seekBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.colorAccent));
        actualTime = song.getCurrentPosition();
        finalTime = song.getDuration();
        songTimeView.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime))));
        actualTimeView.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) actualTime),
                TimeUnit.MILLISECONDS.toSeconds((long) actualTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                actualTime))));

        play.setOnClickListener(this);
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
                if (!isPlaying) {
                    play.setImageResource(R.drawable.button_play_purple);
                    song.start();
                    isPlaying = true;
                } else {
                    play.setImageResource(R.drawable.button_pause_grey);
                    song.pause();
                    isPlaying = false;
                }
                break;
            case R.id.back_button:
                rewindTime = song.getCurrentPosition() - 15000;
                song.seekTo(rewindTime);
                back.setImageResource(R.drawable.backward_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        back.setImageResource(R.drawable.backward_grey);
                    }
                }, 250);
                break;
            case R.id.for_button:
                rewindTime = song.getCurrentPosition() + 15000;
                song.seekTo(rewindTime);
                forward.setImageResource(R.drawable.forward_purple);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        forward.setImageResource(R.drawable.forward_grey);
                    }
                }, 250);
                break;
        }
    }
}
