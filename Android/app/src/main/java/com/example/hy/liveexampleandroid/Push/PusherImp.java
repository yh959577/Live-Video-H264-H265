package com.example.hy.liveexampleandroid.Push;

import android.hardware.camera2.CameraManager;
import android.os.Environment;
import android.view.TextureView;

import com.example.hy.liveexampleandroid.Push.Camera.Camera;
import com.example.hy.liveexampleandroid.Push.Camera.CameraImp;
import com.example.hy.liveexampleandroid.Push.Encoder.Encoder;
import com.example.hy.liveexampleandroid.Push.Queue.QueueManager;

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

  public   static PusherImp buildPusher(TextureView textureView, CameraManager cameraManager,String pushAddress){
        return new PusherImp(textureView,cameraManager,pushAddress);
    }

    private PusherImp(TextureView textureView, CameraManager cameraManager,String pushAddress){
        mCamera=new CameraImp(textureView,cameraManager);
        mPushAddress=pushAddress;
    }

    @Override
    public void initial() {
        mCamera.initial();
    }

    @Override
    public void startPush() {
        mCamera.setIsProcessImage(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path=Environment.getExternalStorageDirectory().getPath()+"/testYUV.yuv";
                    FileOutputStream outputStream=new FileOutputStream(new File(path));
                    while (true){
                        try {
                            outputStream.write(QueueManager.takeDataFromYUVQueue());
                        }catch (Exception e){

                        }
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void stopPush() {

    }

    @Override
    public void onDestroy() {
        mCamera.onDestroy();
    }
}
