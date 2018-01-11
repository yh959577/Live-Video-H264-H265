package com.example.hy.liveexampleandroid.LiveSend;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Size;
import android.view.TextureView;

import com.example.livelib.Push.Pusher;
import com.example.livelib.Push.PusherImp;

import java.net.UnknownHostException;


/**
 * Created by Hamik Young on 2018/1/4.
 */

public class SendInteractorImp implements SendInteractor {
    private Pusher mPusher;

    @Override
    public void initialPusher(TextureView textureView, CameraManager cameraManager) {
            mPusher= PusherImp.buildPusher(textureView, cameraManager);

        //   mPusher.initial();
    }

    @Override
    public void stopPush() {
        try {
            mPusher.stopPush();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPush(String pushAddress) {
        try {
            mPusher.startPush(pushAddress);
        } catch (CameraAccessException | UnknownHostException e) {
            e.printStackTrace();
        }
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
    public void switchCamera() {
        mPusher.switchCamera();
    }

    @Override
    public void onDestroy() {
       mPusher.onDestroy();
    }
}
