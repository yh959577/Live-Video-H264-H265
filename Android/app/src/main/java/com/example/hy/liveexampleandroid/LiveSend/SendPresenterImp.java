package com.example.hy.liveexampleandroid.LiveSend;

import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;

import com.example.hy.liveexampleandroid.IpChecker;

/**
 * Created by Administrator on 2018/1/2.
 */

public class SendPresenterImp implements SendPresenter {
   private SendView mSendView;
   private SendInteractor mSendInteractor;
   private CameraManager mCameraManager;


    public SendPresenterImp(SendView sendView,SendInteractor sendInteractor){
           mSendView=sendView;
           mSendInteractor=sendInteractor;
    }


    @Override
    public int initialCamera() {
        mSendView.initialCamera();
        return 0;
    }

    @Override
    public int startPushVideo(String ip){
        if (IpChecker.IsIpEmpty(ip))
            mSendView.IpIsEmpty();
        else if (IpChecker.IsIpValid(ip)) {
            mSendView.btnTextChangeToStop();

        }
        else
            mSendView.IpError();
        return 0;
    }

    @Override
    public void stopPushVideo() {
     mSendView.btnTextChangeToStart();
    }

    @Override
    public void takePic() {

    }

    @Override
    public void switchCamera() {

    }
}
