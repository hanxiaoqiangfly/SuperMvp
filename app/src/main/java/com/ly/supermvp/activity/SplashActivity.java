package com.ly.supermvp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ly.supermvp.R;
import com.ly.supermvp.utils.HttpUtils;
import com.ly.supermvp.utils.LogUtils;
import com.ly.supermvp.view.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!isNetworkAvailable(getBaseContext())) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SplashActivity.this, "网络连接断开", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    HttpUtils.getInstance().get("http://115.126.65.150/Lottery_server/check_and_get_url.php?type=android&show_url=1&appid=no30413", new HttpUtils.HttpCallback() {
                        @Override
                        public void onSuccess(String data) {
                            LogUtils.i("data" + data);
                            String string = getResources().getString(R.string.intent);

                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                JSONObject data3 = jsonObject.optJSONObject("data");
                                if (data3 != null) {
                                    if (data3.getString("show_url") != null) {

                                        if (data3.getString("show_url").equals("1")) {
                                            Intent intent = new Intent();
                                            intent.putExtra("url", "http://" + data3.getString("url"));
                                            intent.setClass(SplashActivity.this, OfficalNetActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        } else {
                                            Intent intent = new Intent();
                                            intent.setClass(SplashActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }
                                    }
                                } else {
                                    Intent intent = new Intent();
                                    intent.setClass(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            super.onError(msg);
                            LogUtils.i("msg" + msg);
                        }
                    });

                }
            }, 1000);
        }


    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //新版本调用方法获取网络状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            //否则调用旧版本方法
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}