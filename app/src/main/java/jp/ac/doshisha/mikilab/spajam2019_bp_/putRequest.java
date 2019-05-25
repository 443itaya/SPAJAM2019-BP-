package jp.ac.doshisha.mikilab.spajam2019;

import android.app.Activity;
import android.util.Log;
import okhttp3.*;

import java.io.IOException;

public class putRequest {
    Activity activity;
    String res = "";
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public putRequest(String url, String json){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
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
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.w("onResponse", res);
                    }
                });
            }
        });
    }
    private void failMessage() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.w("failMessage", "fail");
            }
        });
    }
}
