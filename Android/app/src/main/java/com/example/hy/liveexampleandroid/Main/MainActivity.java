package com.example.hy.liveexampleandroid.Main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.example.hy.liveexampleandroid.LivePlayer.PlayActivity;
import com.example.hy.liveexampleandroid.LiveSend.SendActivity;
import com.example.hy.liveexampleandroid.PermissionUtil;
import com.example.hy.liveexampleandroid.R;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sendButton).setOnClickListener(this);
        findViewById(R.id.playButton).setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            String[] permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO};
            PermissionUtil.checkPermission(this,permissions);
        }
    }
    @Override
    public void onClick(View view) {
     switch (view.getId()){
         case R.id.sendButton:
             if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                 // TODO: Consider calling
                 //    ActivityCompat#requestPermissions
                 // here to request the missing permissions, and then overriding
                 //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                 //                                          int[] grantResults)
                 // to handle the case where the user grants the permission. See the documentation
                 // for ActivityCompat#requestPermissions for more details.
                 PermissionUtil.checkPermission(this, Manifest.permission.CAMERA);
             }else {
                 startActivity(new Intent(this, SendActivity.class));
             }
             break;
         case R.id.playButton:
             startActivity(new Intent(this, PlayActivity.class));
             break;
             default:
                 break;
     }
    }
}
