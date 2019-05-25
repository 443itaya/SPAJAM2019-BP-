package jp.ac.doshisha.mikilab.spajam2019_bp_;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LocationListener{
    String url = "https://asia-northeast1-spajam-pipeline.cloudfunctions.net/GetResult?message=ほげほげ";
    private String res = "{“name”: “六本木 樓外樓“, “tel”: “075-365-3320\", “address”: “日本、〒600-8216 京都府京都市下京区東塩小路烏丸通塩小路下ル ＪＲ京都駅中央口 ホテルグランヴィア京都 １５Ｆ“, “opening_now”: “null”, “price_level”:“null”, “website”: “http://www.granvia-kyoto.co.jp/rest/roppongi.php“, “lat”: 34.985722, “lng”: 135.7585679}";
    private static final int REQUESTCODE_TEST = 1;

    private double lat = 34.702485;
    private double lng = 135.49595;
    private int type = -1;
    private int dis = -1;

    LocationManager locationManager;

    int id;

    private String fileName = "res.txt";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView6);
        textView.setText("現在値；"+lat+", "+lng);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
        }
        else{
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 50, this);

        }

        RadioGroup group = (RadioGroup)findViewById(R.id.radio_places);
        id = IndexToId1(type);
        group.check(id);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                type = IdToIndex1(checkedId);
                RadioButton radio = (RadioButton)findViewById(checkedId);
            }
        });
        group = (RadioGroup)findViewById(R.id.radio_distance);
        id = IndexToId2(dis);
        group.check(id);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                dis = IdToIndex2(checkedId);
                RadioButton radio = (RadioButton)findViewById(checkedId);
            }
        });

        saveFile(fileName, res);
    }

    int IndexToId1(int index){
        int tmp = -1;
        if (index == 0) tmp = R.id.radioButton;
        else if (index == 1) tmp = R.id.radioButton2;
        else if (index == 2) tmp = R.id.radioButton3;
        return tmp;
    }
    int IdToIndex1(int id){
        int index = -1;
        if (id == R.id.radioButton) index = 0;
        else if (id == R.id.radioButton2) index = 1;
        else if (id == R.id.radioButton3) index = 2;
        return index;
    }
    int IndexToId2(int index){
        int tmp = -1;
        if (index == 0) tmp = R.id.radioButton4;
        else if (index == 1) tmp = R.id.radioButton5;
        else if (index == 2) tmp = R.id.radioButton6;
        return tmp;
    }
    int IdToIndex2(int id){
        int index = -1;
        if (id == R.id.radioButton4) index = 0;
        else if (id == R.id.radioButton5) index = 1;
        else if (id == R.id.radioButton6) index = 2;
        return index;
    }


    private void locationStart(){
        Log.d("debug","locationStart()");

        // LocationManager インスタンス生成
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, this);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // 緯度の表示6
        lat = location.getLatitude();
        TextView textView6 = (TextView) findViewById(R.id.textView6);
        String str1 = "Latitude:"+location.getLatitude();
        textView6.setText(str1);

        // 経度の表示
        lng = location.getLongitude();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    protected void onResume() {
        super.onResume();

        // Nav barの非表示化
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String json = "{\"lat\":" + lat + ",\"lng\":" + lng + ",\"type\":" + type + ",\"dis\": "+ dis +"}";
                Log.w("aaaaaaaaaaaaaaa",json);
                post(json);
                Intent intent = new Intent(this, FortuneActivity.class);
                startActivityForResult(intent, REQUESTCODE_TEST);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void post(String json) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failMessage();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                res = response.body().string();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.w("onResponse", res);
                        saveFile(fileName, res);
                    }
                });
            }
        });
    }
    private void failMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.w("failMessage", "fail");
            }
        });
    }

    // ファイルを保存
    public void saveFile(String file, String str) {

        // try-with-resources
        try (FileOutputStream fileOutputstream = openFileOutput(file,
                Context.MODE_PRIVATE);){

            fileOutputstream.write(str.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
