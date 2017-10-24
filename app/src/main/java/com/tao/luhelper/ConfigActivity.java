package com.tao.luhelper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.R.attr.path;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        final SharedPreferences sp = getSharedPreferences("CONFIG", MODE_WORLD_READABLE);

        final EditText etUser = (EditText)findViewById(R.id.editTextUser);
        final EditText etLoginpassword = (EditText)findViewById(R.id.editTextLoginpassword);
        final EditText etTradepassword = (EditText)findViewById(R.id.editTextTradepassword);
        final EditText etMaxmoney = (EditText)findViewById(R.id.editTextMaxmoney);
        final EditText etMinmoney = (EditText)findViewById(R.id.editTextMinmoney);
        final EditText etMinrate = (EditText)findViewById(R.id.editTextMinrate);
        final Button btBegin = (Button)findViewById(R.id.buttonBegin);
        etUser.setText(sp.getString("UserName", ""));
        etLoginpassword.setText(sp.getString("LoginPassword", ""));
        etTradepassword.setText(sp.getString("TradePassword", ""));
        etMaxmoney.setText(Long.toString(sp.getLong("MaxMoney", 0)));
        etMinmoney.setText(Long.toString(sp.getLong("MinMoney", 0)));
        etMinrate.setText(Float.toString(sp.getFloat("MinRate", 0)));
        btBegin.setText(sp.getBoolean("Monitor", false) ? "停止" : "开始");

        btBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("UserName", etUser.getText().toString());
                editor.putString("LoginPassword", etLoginpassword.getText().toString());
                editor.putString("TradePassword", etTradepassword.getText().toString());
                editor.putLong("MaxMoney", Long.valueOf(etMaxmoney.getText().toString()));
                editor.putLong("MinMoney", Long.valueOf(etMinmoney.getText().toString()));
                editor.putFloat("MinRate", Float.valueOf(etMinrate.getText().toString()));
                if (sp.getBoolean("Monitor", false)) {
                    editor.putBoolean("Monitor", false);
                } else {
                    editor.putBoolean("Monitor", true);
                }
                editor.apply();

                if (!sp.getBoolean("Monitor", false)) {
                    btBegin.setText("开始");
                } else {
                    btBegin.setText("停止");

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.MAIN");
                    intent.setClassName("com.lufax.android", "com.lufax.android.activity.WelcomeActivity");
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
