package it.ambient.sentryturret.action;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

import it.ambient.sentryturret.R;
import it.ambient.sentryturret.event.SpeakStartedEvent;
import it.ambient.sentryturret.event.SpeakStoppedEvent;

public class Speak implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "Speak";
    Random rand = new Random();
    private Context applicationContext;
    private MediaPlayer mp;
    //    AudioAttributes audioAttributes;
    private ArrayList soundDeploying;
    private ArrayList soundGreeting;
    private ArrayList soundPickUp;

    public Speak(Context context) {
        Log.d(TAG, "Speak.construct");
        applicationContext = context;
        // Load the sounds
        soundGreeting = new ArrayList();
        soundGreeting.add(R.raw.turret_greeting_1);
        soundGreeting.add(R.raw.turret_greeting_2);
        soundGreeting.add(R.raw.turret_greeting_3);
        soundDeploying = new ArrayList();
        soundDeploying.add(R.raw.turret_deploy_1);
        soundDeploying.add(R.raw.turret_deploy_2);
        soundDeploying.add(R.raw.turret_deploy_4);
        soundPickUp = new ArrayList();
        soundPickUp.add(R.raw.turret_pickup_3);
        soundPickUp.add(R.raw.turret_pickup_8);
    }

    public void sayGreeting() {
        Log.d(TAG, "sayGreeting()");
        int n = rand.nextInt(soundGreeting.size());
        playSound(applicationContext, (int) soundGreeting.get(n));
    }

    public void sayDeploying() {
        Log.d(TAG, "sayDeploying()");
        int n = rand.nextInt(soundDeploying.size());
        playSound(applicationContext, (int) soundDeploying.get(n));
    }

    public void sayPutMeDown() {
        Log.d(TAG, "sayPutMeDown()");
        int n = rand.nextInt(soundPickUp.size());
        playSound(applicationContext, (int) soundPickUp.get(n));
    }

    private void playSound(Context context, int soundId) {
        Log.d(TAG, "playSound() " + soundId);
        if(mp != null)
        {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
        }
        mp = MediaPlayer.create(context, soundId);
        mp.setOnCompletionListener(this);
        if(mp != null) {
            mp.start();
            EventBus.getDefault().post(new SpeakStartedEvent(mp.getAudioSessionId()));
        }
    }

    /**
     * Callback invoked when playback ends. Publishes SpeakStoppedEvent
     */
    public void onCompletion(MediaPlayer mediaPlayer) {
        EventBus.getDefault().post(new SpeakStoppedEvent());
    }
}
