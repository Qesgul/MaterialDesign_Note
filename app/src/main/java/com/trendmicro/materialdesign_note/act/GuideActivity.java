package com.trendmicro.materialdesign_note.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.trendmicro.materialdesign_note.R;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zheng_liu on 2018/2/24.
 */

public class GuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        getPermission();
        Timer timer = new Timer();
        TimerTask tast = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(GuideActivity.this, CameraActivity.class);
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(tast, 4000);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限  第二个参数是一个 数组 说明可以同时申请多个权限
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA, Manifest.permission.VIBRATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA, Manifest.permission.VIBRATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        }
    }
}
