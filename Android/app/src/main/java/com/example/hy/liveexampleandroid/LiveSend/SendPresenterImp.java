package com.example.hy.liveexampleandroid.LiveSend;

/**
 * Created by Hamik Young on 2018/1/2.
 */

public class SendPresenterImp implements SendPresenter {
   private SendView mSendView;
   private SendInteractor mSendInteractor;



    public SendPresenterImp(SendView sendView,SendInteractor sendInteractor){
           mSendView=sendView;
           mSendInteractor=sendInteractor;
    }


    @Override
    public int initialPusher() {
        mSendInteractor.initialPusher(mSendView.supplyTextureView(),mSendView.supplyCameraManager(),
                mSendView.getPushIp());
        return 0;
    }

    @Override
    public int startPushVideo(String ip){
//        if (IpChecker.IsIpEmpty(ip))
//            mSendView.IpIsEmpty();
//        else if (IpChecker.IsIpValid(ip)) {
//            mSendView.btnTextChangeToStop();
//            mSendInteractor.startPush();
//        }
//        else
//            mSendView.IpError();
        mSendView.btnTextChangeToStop();
        mSendInteractor.startPush();
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

    @Override
    public void onDestroy() {
        mSendInteractor.onDestroy();
    }
}
