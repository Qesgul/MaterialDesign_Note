package com.trendmicro.materialdesign_note.act;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.trendmicro.materialdesign_note.R;
import com.trendmicro.materialdesign_note.Utils.RSAUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * Created by zheng_liu on 2018/2/27.
 */

public class BaseActivity extends AppCompatActivity {
    protected Context context;
    protected Cipher cipher;
    protected PublicKey Key1;
    protected PrivateKey Key2;
    protected SharedPreferences preference;
    protected SharedPreferences.Editor editor;
    protected Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        context = this;
        preference = getSharedPreferences("User", Context.MODE_PRIVATE);
        flag=preference.getBoolean("firststart", true);
        try {
            Key1= RSAUtils.getPublicKey(preference.getString("PublicKey", ""));
            Key2= RSAUtils.getPrivateKey(preference.getString("PrivateKey", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onResme(){
        super.onResume();

    }
}
