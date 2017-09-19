package rouein.gher;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    AnimationDrawable gherAnimation;
    AnimationDrawable werAnimation;

    MediaPlayer mp1, mp2;
    ImageView gherImage;
    ImageView werImage;
    private SensorManager jSensorManager;
    private Sensor jAccelerometer;
    private ShakeListener jShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp1 = MediaPlayer.create(MainActivity.this, R.raw.dgher);
        mp2 = MediaPlayer.create(MainActivity.this, R.raw.wer);

        loadwer();
        loadGher();

        Intent intent = new Intent(MainActivity.this, ShakeListener.class);
        startService(intent);


        jSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        jAccelerometer = jSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        jShakeDetector = new ShakeListener();

        jShakeDetector.setOnShakeListener(new ShakeListener.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (werAnimation.isRunning()) {
                    werAnimation.stop();
                    mp2.stop();
                    mp2.prepareAsync();
                }
                runGher();
                mp1.start();

            }
        });

    }

    public void loadGher() {

        gherImage = (ImageView) findViewById(R.id.gher);
        gherImage.setBackgroundResource(R.drawable.gher_animation);
        gherAnimation = (AnimationDrawable) gherImage.getBackground();

    }

    public void runGher() {

        gherImage.setVisibility(View.VISIBLE);
        gherAnimation.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gherAnimation.stop();
                gherImage.setVisibility(View.INVISIBLE);
            }
        }, mp1.getDuration());

    }

    public void stopGher() {

        gherAnimation.stop();
        gherImage.setVisibility(View.INVISIBLE);

    }

    public void loadwer() {

        werImage = (ImageView) findViewById(R.id.wer);
        werImage.setBackgroundResource(R.drawable.wer_animation);
        werAnimation = (AnimationDrawable) werImage.getBackground();

    }

    public void runwer() {

        werImage.setVisibility(View.VISIBLE);
        werAnimation.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                werAnimation.stop();
                werImage.setVisibility(View.INVISIBLE);
            }
        }, mp2.getDuration());

    }

    public void stopwer() {

        werAnimation.stop();
        werImage.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (gherAnimation.isRunning()) {
                gherAnimation.stop();
                mp1.stop();
                mp1.prepareAsync();
            }
            runwer();
            mp2.start();
            return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void onResume() {
        super.onResume();
        jSensorManager.registerListener(jShakeDetector, jAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {

        mp1.stop();
        mp1.prepareAsync();
        mp2.stop();
        mp2.prepareAsync();
        stopGher();
        stopwer();

        jSensorManager.unregisterListener(jShakeDetector);
        super.onPause();
    }
}

