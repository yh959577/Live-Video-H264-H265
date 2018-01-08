package com.example.hy.liveexampleandroid.LiveSend;

import android.hardware.camera2.CameraManager;
import android.util.Size;
import android.view.TextureView;

import com.example.hy.liveexampleandroid.Push.Pusher;
import com.example.hy.liveexampleandroid.Push.PusherImp;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class SendInteractorImp implements SendInteractor {
    private Pusher mPusher;

    @Override
    public void initialPusher(TextureView textureView, CameraManager cameraManager,String pushAddress) {
      mPusher=PusherImp.buildPusher(textureView, cameraManager,pushAddress);
      mPusher.initial();
    }

    @Override
    public void stopPush() {
      mPusher.stopPush();
    }

    @Override
    public void startPush() {
      mPusher.startPush();
    }

    @Override
    public void setPreviewSize(Size previewSize) {
             mPusher.setPreviewSize(previewSize);
    }

    @Override
    public void setPushSize(Size pushSize) {
           mPusher.setPushSize(pushSize);
    }

    @Override
    public void setPushType(String pushType) {
           mPusher.setPushType(pushType);
    }

    @Override
    public void onDestroy() {
       mPusher.onDestroy();
    }
}
