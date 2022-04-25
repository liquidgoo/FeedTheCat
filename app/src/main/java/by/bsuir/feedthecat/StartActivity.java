package by.bsuir.feedthecat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class StartActivity extends AppCompatActivity {

    private GifImageView loadGif;

    private Timer timer = new Timer();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        try {
            Database.Instance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadGif = findViewById(R.id.LoadImage);
        ((GifDrawable)loadGif.getDrawable()).setLoopCount(1);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkAnimationEnding();
                    }
                });
            }
        }, 0, 20);
    }

    private void checkAnimationEnding(){
        if ( ((GifDrawable)loadGif.getDrawable()).isAnimationCompleted() ){
            timer.cancel();
            timer = null;

            Intent intent = new Intent(getApplicationContext(), FirstActivity.class/*MainActivity.class*/);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() { }
}