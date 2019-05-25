package jp.ac.doshisha.mikilab.spajam2019_bp_;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class FortuneActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;

    private float beforeX;
    private float beforeY;
    private float beforeZ;
    private long beforeTime = -1;

    private float shakeSpeed = 70;
    private float shakeCount = 0;

    private static final int REQUESTCODE_TEST = 1;

    TextView textCount;

    int mp3;
    SoundPool soundPool;
    public void play_mp3(){soundPool.play(mp3,1f , 1f, 0, 0, 1f);};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortune);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textCount  = findViewById(R.id.textView4);
        textCount.setText(String.valueOf(shakeCount));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            //1個目のパラメーターはリソースの数に合わせる
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        } else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    //パラメーターはリソースの数に合わせる
                    .setMaxStreams(2)
                    .build();
        }
        //音楽の読み込み
        mp3 = soundPool.load(this, R.raw.syakasyaka, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if(sensors.size() > 0){
            Sensor s = sensors.get(0);
            mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }

        beforeX = 0;
        beforeY = 0;
        beforeZ = 0;
        beforeTime = -1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        switch(event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                long nowTime = System.currentTimeMillis();

                if (beforeTime == -1) {
                    beforeX = x;
                    beforeY = y;
                    beforeZ = z;

                    beforeTime = nowTime;
                    break;
                }

                long diffTime = nowTime - beforeTime;
                if( diffTime < 300 )
                    break;


                float speed = Math.abs(x + y + z - beforeX - beforeY - beforeZ) / diffTime * 10000;

                if( speed > shakeSpeed && shakeCount <= 10){
                    textCount.setText(String.valueOf(shakeCount));
                    play_mp3();

                    if(++shakeCount > 10){
                        Intent intent = new Intent(this, PlaceActivity.class);
                        startActivityForResult(intent, REQUESTCODE_TEST);
                        finish();
                    }
                }
                else {

                }

                beforeX = x;
                beforeY = y;
                beforeZ = z;

                beforeTime = nowTime;

                break;
        }
    }

}