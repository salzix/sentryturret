package it.ambient.sentryturret.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import it.ambient.sentryturret.event.MovedUpEvent;

/**
 * @TODO Detect movement
 */
public class Moved implements SensorEventListener {
    private static final String TAG = "Moved";
    private Context applicationContext;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int PICKUP_THRESHOLD = 1;

    public Moved(Context context) {
        applicationContext = context;
        sensorManager = (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.w(TAG, "Sensor not available!");
            // fai! we dont have an accelerometer!
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged()");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        Log.d(TAG, "onSensorChanged()");
        if (accelerometer.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
//                float x = Math.round(sensorEvent.values[0] * 1000) / 1000;
                float y = Math.round(sensorEvent.values[1] * 1000) / 1000;
//                float z = Math.round(sensorEvent.values[2] * 1000) / 1000;
//                Log.d(TAG, "acc x:" + x + "  y:" + y + "  z:"+z);
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
//                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
//                Log.d(TAG, "speed:" + speed);
                if (y >= PICKUP_THRESHOLD) {
                    EventBus.getDefault().post(new MovedUpEvent());
                }
//                last_x = x;
                last_y = y;
//                last_z = z;
            }
        }
    }
}