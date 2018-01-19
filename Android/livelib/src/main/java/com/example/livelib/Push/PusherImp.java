package com.example.livelib.Push;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Size;
import android.view.TextureView;

import com.example.livelib.Push.Camera.Camera;
import com.example.livelib.Push.Camera.CameraImp;
import com.example.livelib.Push.Encoder.Encoder;
import com.example.livelib.Push.Encoder.EncoderImp;
import com.example.livelib.Push.Exception.IllegalIpAddress;
import com.example.livelib.Push.UdpSend.VideoSender;
import com.example.livelib.Push.UdpSend.VideoSenderImp;
import com.example.livelib.Push.Util.IpChecker;

import java.net.UnknownHostException;


/**
 * Created by Hamik Young on 2018/1/4.
 */

public class PusherImp implements Pusher {
    private String mPushAddress;
    private Camera mCamera;
    private Encoder mEncoder;
    private VideoSender mVideoSender;
    public static Size[] supportSize;
    private int mPort;

    public static PusherImp buildPusher(TextureView textureView, CameraManager cameraManager) {
        return new PusherImp(textureView, cameraManager);
    }


    private PusherImp(TextureView textureView, CameraManager cameraManager)  {
          //  mPushAddress = pushAddress.substring(0, pushAddress.indexOf(':'));
         //   mPort = Integer.valueOf(pushAddress.substring(pushAddress.indexOf(':') + 1));
            mCamera = new CameraImp(textureView, cameraManager);
            mEncoder = new EncoderImp();
            mVideoSender=new VideoSenderImp();
            initial();
    }


    @Override
    public void initial()  {
        mCamera.initial();
      //  mVideoSender.initial(mPushAddress,mPort);
    }

    @Override
    public void startPush(String pushAddress) throws CameraAccessException, UnknownHostException {
        //    mCamera.setIsProcessImage(true);

        mEncoder.initial();
        mCamera.startPush(mEncoder.getPushSize());
        mEncoder.startEncoder();
        mVideoSender.initial(pushAddress);
        mVideoSender.startSendVideoData();
        //   mVideoSender.sendVideoData(mPushAddress);
    }

    @Override
    public void stopPush() throws CameraAccessException {
        // mCamera.setIsProcessImage(false);
        mCamera.stopPush();
        mEncoder.stopEncoder();
        mVideoSender.stop();
    }

    @Override
    public void setPreviewSize(Size previewSize) {
        mCamera.setPreviewSize(previewSize);
    }

    @Override
    public void setPushSize(Size pushSize) {
        mEncoder.setPushSize(pushSize);
    }

    @Override
    public void setPushType(String pushType) {
        mEncoder.setPushType(pushType);
    }

    @Override
    public void setPushAddress(String address) {

    }

    @Override
    public void switchCamera() {
        mCamera.switchCamera();
    }

    @Override
    public void onDestroy() {
        mCamera.closeCamera();
    }
}
