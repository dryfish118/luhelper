package com.tao.luhelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static android.R.attr.path;
import static java.lang.Thread.sleep;

public class ConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        GlobleUtil.putBoolean("Monitor", false);

        final EditText etUser = (EditText)findViewById(R.id.editTextUser);
        final EditText etGesture = (EditText)findViewById(R.id.editTextGesture);
        final EditText etLoginpassword = (EditText)findViewById(R.id.editTextLoginpassword);
        final EditText etTradepassword = (EditText)findViewById(R.id.editTextTradepassword);
        final EditText etMaxmoney = (EditText)findViewById(R.id.editTextMaxmoney);
        final EditText etMinmoney = (EditText)findViewById(R.id.editTextMinmoney);
        final EditText etMinrate = (EditText)findViewById(R.id.editTextMinrate);
        final Button btBegin = (Button)findViewById(R.id.buttonBegin);
        etUser.setText(GlobleUtil.getString("UserName", ""));
        etGesture.setText(GlobleUtil.getString("Gesture", ""));
        etLoginpassword.setText(GlobleUtil.getString("LoginPassword", ""));
        etTradepassword.setText(GlobleUtil.getString("TradePassword", ""));
        etMaxmoney.setText(GlobleUtil.getString("MaxMoney", "0"));
        etMinmoney.setText(GlobleUtil.getString("MinMoney", "0"));
        etMinrate.setText(GlobleUtil.getString("Rofit", "0"));
        btBegin.setText(GlobleUtil.getBoolean("Monitor", false) ? "停止" : "开始");

        btBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobleUtil.getBoolean("Monitor", false)) {
                    GlobleUtil.putBoolean("Monitor", false);

                    btBegin.setText("开始");
                } else {
                    btBegin.setText("停止");

                    GlobleUtil.putString("UserName", etUser.getText().toString());
                    GlobleUtil.putString("Gesture", etGesture.getText().toString());
                    GlobleUtil.putString("LoginPassword", etLoginpassword.getText().toString());
                    GlobleUtil.putString("TradePassword", etTradepassword.getText().toString());
                    GlobleUtil.putString("MaxMoney", etMaxmoney.getText().toString());
                    GlobleUtil.putString("MinMoney", etMinmoney.getText().toString());
                    GlobleUtil.putString("Rofit", etMinrate.getText().toString());

                    GlobleUtil.putBoolean("Class:", false);
                    GlobleUtil.putInt("Step", 0);

                    GlobleUtil.putBoolean("Monitor", true);

                    try {
                        // 杀死后台进程
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        am.killBackgroundProcesses("com.lufax.android");

                        // 启动进程
                        (new Timer()).schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.MAIN");
                                intent.setClassName("com.lufax.android", "com.lufax.android.activity.WelcomeActivity");
                                startActivity(intent);
                            }
                        }, 2000);
                    } catch (Exception e) {
                    }
                }
            }
        });

        findViewById(R.id.buttonTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Button btBegin = (Button)findViewById(R.id.buttonBegin);
        if (GlobleUtil.getBoolean("Monitor", false)) {
            btBegin.setText("停止");
        } else {
            btBegin.setText("开始");
        }
    }
}
