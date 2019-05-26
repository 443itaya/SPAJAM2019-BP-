package jp.ac.doshisha.mikilab.spajam2019_bp_;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int REQUESTCODE_TEST = 1;
    public String res = "";

    TextView textView;

    private String fileName = "res.txt";
    String name, tel, address, open, price, website;
    double lat, lng;

    private String[] word = new String[5];
    private int ram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // MapFragmentの生成
        MapFragment mapFragment = MapFragment.newInstance();

        // MapViewをMapFragmentに変更する
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapView, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        String res = readFile(fileName);

        try {

            JSONObject json = new JSONObject(res);

            // ブロック部：getJSONObjectメソッド
            // キー・バリュー部：getStringメソッド
            // 配列部分：getJSONArrayメソッド＋getJSONObjectメソッドで繰り返し取得
            name = json.getString("name");
            if(name.isEmpty() == true) name = "不明";
            tel = json.getString("tel");
            if(tel.isEmpty() == true) tel = "不明";
            address = json.getString("address");
            if(address.isEmpty() == true) address = "不明";
            open = json.getString("opening_now");
            if(open.isEmpty() == true) open = "不明";
            else if(open.equals("True")) open = "営業中";
            else if(open.equals("False")) open = "準備中";
            price = json.getString("price_level");
            if(price.isEmpty() == true) price = "不明";
            else if(price.equals("0")) price = "無料";
            else if(price.equals("1")) price = "お手頃";
            else if(price.equals("2")) price = "普通";
            else if(price.equals("3")) price = "高め";
            else if(price.equals("4")) price = "豪遊☆";
            website = json.getString("website");
            if(website.isEmpty() == true) website = "不明";
            lat = Double.parseDouble(json.getString("lat"));
            lng = Double.parseDouble(json.getString("lng"));


        } catch (JSONException e) {

        }

        textView = findViewById(R.id.textView2);
        textView.setText(name);
        textView = findViewById(R.id.textView3);
        textView.setText(tel);
        textView = findViewById(R.id.textView7);
        textView.setText(address);
        textView = findViewById(R.id.textView8);
        textView.setText(open);
        textView = findViewById(R.id.textView9);
        textView.setText(price);
        textView = findViewById(R.id.textView10);
//        textView.setText(website);
        textView.setText(Html.fromHtml("<a href="+website+">"+website+"</a>"));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        word[0] = "中吉ニャー";
        word[1] = "時間に注意だニャー";
        word[2] = "ラッキーアニマルはねこだニャー";
        word[3] = "ねこを見つけるといいことがあるニャー";
        word[4] = "良い旅をニャー";
        ram = (int)(Math.random()*5);
        textView = findViewById(R.id.textView11);
        textView.setText(word[ram]);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(html);
        }
        return spanned;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Nav barの非表示化
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng place = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(place).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public String readFile(String file) {
        String text = null;

        // try-with-resources
        try (FileInputStream fileInputStream = openFileInput(file);
             BufferedReader reader= new BufferedReader(
                     new InputStreamReader(fileInputStream, "UTF-8"))) {

            String lineBuffer;
            while( (lineBuffer = reader.readLine()) != null ) {
                text = lineBuffer ;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                Intent intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent, REQUESTCODE_TEST);
                finish();
                break;
        }
    }
}
