package it.ambient.sentryturret.action;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.util.Log;
import android.widget.ImageView;

import org.greenrobot.eventbus.Subscribe;

import it.ambient.sentryturret.R;
import it.ambient.sentryturret.event.SpeakStartedEvent;
import it.ambient.sentryturret.event.SpeakStoppedEvent;

public class Blink
        implements Visualizer.OnDataCaptureListener {
    private static final String TAG = "Blink";
    private ImageView imageViewPupil;
    private Context applicationContext;
    private Visualizer visualizer;

    public Blink(Context context, ImageView imageView) {
        imageViewPupil = imageView;
        applicationContext = context;
    }

    private void close() {
        Log.d(TAG, "close()");
        imageViewPupil.setImageResource(R.drawable.eye_pupil_1);
        visualizer.release();
        visualizer = null;
    }

    private void setPupil(float size) {
//        Log.d(TAG, "setPupil()");
        if (size > 1) {
            Log.w(TAG, "pupil size too large: " +  size);
            size = 1;
        }
        if (size < 0) {
            Log.w(TAG, "pupil size too small: " +  size);
            size = 0;
        }
        int sizeRecalculated = (int) Math.ceil(size * 8);   // 10 is too big
        if (sizeRecalculated == 0 ) sizeRecalculated++;
//        Log.d(TAG, "pupil sizeRecalculated: "  + sizeRecalculated);
        int idResource = applicationContext.getResources().getIdentifier("eye_pupil_" + sizeRecalculated, "drawable", applicationContext.getPackageName());
        imageViewPupil.setImageResource(idResource);
    }

    private void createVisualizer(int audioSessionId){
        Log.d(TAG, "createVisualizer() " + audioSessionId);
        int rate = Visualizer.getMaxCaptureRate() / 2;
        Log.d(TAG, "maxCaptureRate " + rate);
        visualizer = new Visualizer(audioSessionId); // get output audio stream
        visualizer.setEnabled(false);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(this,rate , true, false); // waveform not freq data
        visualizer.setEnabled(true);
    }

    /**
     * Callback for visualizer.setDataCaptureListener
     */
    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        float intensity = 0.0f;
        float sample = 0.0f;
        for (int i = 0; i < waveform.length - 1; i++) {
            sample = (short) ((waveform[i]) | waveform[i + 1] << 8);
            sample += waveform[i];
            intensity = Math.abs(sample) / (intensity / 2);
        }
        intensity = ((float) waveform[0] + 128f) / 256;
//        Log.d(TAG, "waveform intensity: "  + String.valueOf(intensity));
        setPupil(intensity);
    }

    /**
     * Callback required by visualizer.setDataCaptureListener but not used
     */
    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {}

    @Subscribe
    public void onSpeakStarted(SpeakStartedEvent event) {
        Log.d(TAG, "onSpeakStarted()" + event.getAudioSessionId());
        createVisualizer(event.getAudioSessionId());
    }

    @Subscribe
    public void onSpeakStopped(SpeakStoppedEvent event) {
        Log.d(TAG, "onSpeakStopped()");
        close();
    }

}