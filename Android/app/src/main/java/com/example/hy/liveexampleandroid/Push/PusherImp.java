package com.example.hy.liveexampleandroid.Push;

import android.hardware.camera2.CameraManager;
import android.os.Environment;
import android.util.Size;
import android.view.TextureView;

import com.example.hy.liveexampleandroid.Push.Camera.Camera;
import com.example.hy.liveexampleandroid.Push.Camera.CameraImp;
import com.example.hy.liveexampleandroid.Push.Encoder.Encoder;
import com.example.hy.liveexampleandroid.Push.Encoder.EncoderImp;
import com.example.hy.liveexampleandroid.Push.Queue.QueueManager;
import com.example.hy.liveexampleandroid.Push.UdpSend.VideoSender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class PusherImp implements Pusher {
    private String mPushAddress;
    private Camera mCamera;
    private Encoder mEncoder;
    private VideoSender mVideoSender;
    public static Size[] supportSize ;

    public static PusherImp buildPusher(TextureView textureView, CameraManager cameraManager, String pushAddress) {
        return new PusherImp(textureView, cameraManager, pushAddress);
    }

    private PusherImp(TextureView textureView, CameraManager cameraManager, String pushAddress) {
        mCamera = new CameraImp(textureView, cameraManager);
        mPushAddress = pushAddress;
        mEncoder=new EncoderImp();
    }

    @Override
    public void initial() {
        mCamera.initial();

    }

    @Override
    public void startPush() {
        mCamera.setIsProcessImage(true);
         mEncoder.startEncoder();
     //   mVideoSender.sendVideoData(mPushAddress);
    }

    @Override
    public void stopPush() {
        mCamera.setIsProcessImage(false);
    }

    @Override
    public void onDestroy() {
        mCamera.onDestroy();
    }
}
