package com.example.hy.liveexampleandroid.LiveSend;

import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

/**
 * Created by Hamik Young on 2017/12/29.
 */

public interface SendInteractor {
    void initialPusher(TextureView textureView, CameraManager cameraManager,String pushAddress);
    void stopPush();
    void startPush();
    void onDestroy();
}
