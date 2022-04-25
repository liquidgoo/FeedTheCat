package by.bsuir.feedthecat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;


public class MiniGameActivity extends AppCompatActivity {


    private TextView scoreLabel;
    private TextView tapLabel;

    private HitSoundPlayer hitSoundPlayer;

    private RelativeLayout rLayout;
    private RelativeLayout leftLayout;
    private RelativeLayout rightLayout;

    private final Queue<ImageView> lNotes = new LinkedList<>();
    private final Queue<ImageView> rNotes = new LinkedList<>();

    float lSpeed;
    float rSpeed;
    float lDelay;
    float rDelay;
    int updateDelay = 20;

    private ImageView lHitSpot;
    private ImageView rHitSpot;


    private int screenHeight;

    private int rX;
    private int lX;

    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    private boolean action_flg = false;
    private boolean start_flg = false;

    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    private Timer timer3 = new Timer();
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private Handler handler3 = new Handler();

    private long score = 0;

    private void gameOver() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game);

        hitSoundPlayer = new HitSoundPlayer(this);

        rLayout = this.findViewById(R.id.RelativeLayout);
        leftLayout = this.findViewById(R.id.leftLayout);
        rightLayout = this.findViewById(R.id.rightLayout);

        lHitSpot = this.findViewById(R.id.lHitSpot);
        rHitSpot = this.findViewById(R.id.rHitSpot);

        lHitSpot.post(() -> {
            int[] point = new int[2];
            lHitSpot.getLocationInWindow(point);
            lX = point[0];
        });
        rHitSpot.post(() -> {
            int[] point = new int[2];
            rHitSpot.getLocationInWindow(point);
            rX = point[0];
        });


        scoreLabel = this.findViewById(R.id.ScoreLabel);
        tapLabel = this.findViewById(R.id.TapLabel);


        lHitSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start_flg) {
                    hitSoundPlayer.playHitSound1();
                    checkCollision(lHitSpot, lNotes.poll());
                }
            }
        });


        rHitSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start_flg) {
                    hitSoundPlayer.playHitSound2();
                    checkCollision(rHitSpot, rNotes.poll());
                }
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!start_flg) {
            tapLabel.setVisibility(View.INVISIBLE);
            start_flg = true;
            initializeScreenHeight();
            initializeSpeeds();


            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    });
                }
            }, 0, updateDelay);

            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler2.post(new Runnable() {
                        @Override
                        public void run() {
                            spawnNote(lNotes, R.drawable.note, leftLayout);
                        }
                    });
                }
            }, 0, Math.round(1000 * lDelay));

            timer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler3.post(new Runnable() {
                        @Override
                        public void run() {

                            spawnNote(rNotes, R.drawable.note2, rightLayout);
                        }
                    });
                }
            }, 0, Math.round(1000 * rDelay));
        }
        return super.onTouchEvent(event);
    }

    private void spawnNote(Queue<ImageView> notes, int resId, RelativeLayout layout) {
        ImageView note = new ImageView(this);
        note.setImageResource(resId);
        note.setLayoutParams(params);
        note.setY(-200);
        note.setX(0);
        layout.addView(note);
        notes.offer(note);

    }

    public void checkCollision(ImageView hitSpot, ImageView note) {
        if (note == null) {
            gameOver();
            return;
        }
        float delay = Math.abs(hitSpot.getY() - note.getY()) / hitSpot.getHeight();

        if (delay <= 0.5) {
            score += 10 * (1 - delay);
            ((ViewGroup) note.getParent()).removeView(note);
        }
        else {
            gameOver();
        }

    }

    private void checkMiss(ImageView hitSpot, ImageView note) {
        if (note != null && note.getY() > hitSpot.getY() + hitSpot.getHeight() / 2.0f)
            gameOver();
    }

    private void moveNotes(Queue<ImageView> notes, float speed) {
        for (ImageView note : notes) {
            note.setY(note.getY() + speed * (updateDelay / 1000.0f));
        }
    }

    public void update() {
        if (start_flg) {

            moveNotes(lNotes, lSpeed);
            moveNotes(rNotes, rSpeed);
            checkMiss(lHitSpot, lNotes.peek());
            checkMiss(rHitSpot, rNotes.peek());
        }

        scoreLabel.setText("Score: " + String.valueOf(score));
    }

    private void initializeScreenHeight() {
        screenHeight = rLayout.getHeight();
    }

    private void initializeSpeeds() {
        lDelay = 3.0f / 3;
        rDelay = 3.0f / 4;
        lSpeed = screenHeight / 3.0f;
        rSpeed = screenHeight / 3.0f;
    }
}