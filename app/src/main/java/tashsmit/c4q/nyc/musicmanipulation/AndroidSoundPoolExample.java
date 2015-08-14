package tashsmit.c4q.nyc.musicmanipulation;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//------------------------------------------------------------------------------------------------------
//this class uses both the accelerometer & the sound pool class
//i am trying to use the accelerometer data to manipulate the music (volume change/ pitch change/ layering sounds)


public class AndroidSoundPoolExample extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

    private SoundPool soundPool;
    private int soundID;
    boolean plays = false, loaded = false, yValue = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    int counter;
    TextView xCoor; // declare X axis object
    TextView yCoor; // declare Y axis object
    TextView zCoor; // declare Z axis object

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set the layout of the Activity
        setContentView(R.layout.activity_main);

        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // the counter will help us recognize the stream id of the sound played  now
        counter = 0;

        // Load the sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(this, R.raw.zanzibar, 1);

        xCoor=(TextView)findViewById(R.id.xcoor); // create X axis object
        yCoor=(TextView)findViewById(R.id.ycoor); // create Y axis object
        zCoor=(TextView)findViewById(R.id.zcoor); // create Z axis object

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be HelloAndroid (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

		/*	More sensor speeds (taken from api docs)
		    SENSOR_DELAY_FASTEST get sensor data as fast as possible
		    SENSOR_DELAY_GAME	rate suitable for games
		 	SENSOR_DELAY_NORMAL	rate (default) suitable for screen orientation changes
		*/

    }

    public void onSensorChanged(SensorEvent event) {

        // check sensor type
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // assign directions
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText("X: " + x);
            yCoor.setText("Y: " + y);
            zCoor.setText("Z: " + z);

            //experimenting to see if this boolean value can
            //come in handy with manipulating soundPOol

            if (y < 0) {
                yValue = false;
            }
            else {
                yValue = true;
            }

        }
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    public void playSound(View v) {
        // Is the sound loaded does it already play?
        if (loaded && !plays) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            counter = counter++;
            Toast.makeText(this, "Played sound", Toast.LENGTH_SHORT).show();
            plays = true;
        }
    }

    public void playLoop(View v) {
        // Is the sound loaded does it already play?
        if (loaded && !plays) {

            // the sound will play for ever if we put the loop parameter -1
            soundPool.play(soundID, volume, volume, 1, -1, 1f);
            counter = counter++;
            Toast.makeText(this, "Plays loop", Toast.LENGTH_SHORT).show();
            plays = true;
        }
    }

    public void pauseSound(View v) {
        if (plays) {
            soundPool.pause(soundID);
            soundID = soundPool.load(this, R.raw.zanzibar, counter);
            Toast.makeText(this, "Pause sound", Toast.LENGTH_SHORT).show();
            plays = false;
        }
    }

    public void stopSound(View v) {
        if (plays) {
            soundPool.stop(soundID);
            soundID = soundPool.load(this, R.raw.zanzibar, counter);
            Toast.makeText(this, "Stop sound", Toast.LENGTH_SHORT).show();
            plays = false;
        }
    }

    public void changePitch (View v) {
            soundPool.setRate(soundID, .5f);
            counter = counter++;
            Toast.makeText(this, "pitch changed", Toast.LENGTH_SHORT).show();
            plays = true;
    }
}