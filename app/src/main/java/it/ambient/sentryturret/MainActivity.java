package it.ambient.sentryturret;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import it.ambient.sentryturret.action.Blink;
import it.ambient.sentryturret.action.Speak;
import it.ambient.sentryturret.event.MovedUpEvent;
import it.ambient.sentryturret.sensor.Moved;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Speak speak;
    private Blink blink;
    private Moved moved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        findViewById(R.id.imageViewEye).setOnClickListener(this);

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {    // @TODO permission revokes
            Log.d(TAG, "permission denied to RECORD_AUDIO - requesting it");
            String[] permissions = {Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "permission RECORD_AUDIO GRANTED");
            initAudio();
        }
        ImageView imageViewPupil = findViewById(R.id.imageViewPupil);
        blink = new Blink(this, imageViewPupil);
        moved = new Moved(this);
    }

    /**
     * Configure speaker
     */
    private void initAudio() {
        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        speak = new Speak(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult() PERMISSION_GRANTED");
                    initAudio();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult() PERMISSION_DENIED");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    } else {
                        finish();
                    }
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewEye:
                speak.sayGreeting();
                break;
        }
    }

    @Subscribe
    public void onMovedUp(MovedUpEvent event) {
        Log.d(TAG, "onMovedUp() event");
        speak.sayPutMeDown();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(blink);
        EventBus.getDefault().register(this);
        speak.sayDeploying();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(blink);
        EventBus.getDefault().unregister(this);
    }
}
