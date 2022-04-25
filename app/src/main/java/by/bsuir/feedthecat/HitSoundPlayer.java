package by.bsuir.feedthecat;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class HitSoundPlayer {
    private static SoundPool soundPool;
    private static int hitSound1, hitSound2;

    HitSoundPlayer(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(2)
                    .build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        hitSound1 = soundPool.load(context, R.raw.hit1, 1);
        hitSound2 = soundPool.load(context, R.raw.hit2, 1);
    }

    void playHitSound1() {
        soundPool.play(hitSound1, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    void playHitSound2() {
        soundPool.play(hitSound2, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
