package com.example.hy.liveexampleandroid.LiveSend;

import android.util.Size;

import com.example.livelib.Util.IpChecker;

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
        mSendInteractor.initialPusher(mSendView.supplyTextureView(),mSendView.supplyCameraManager());
        return 0;
    }

    @Override
    public int startPushVideo(String pushAddress){
        if (IpChecker.IsIpEmpty(pushAddress))
            mSendView.showIpEmptyError();
        else if (!IpChecker.IsIpValid(pushAddress))
            mSendView.showIpInvalidError();
        else {
            mSendView.btnTextChangeToStop();
            mSendInteractor.startPush(pushAddress);
        }
        return 0;
    }

    @Override
    public void stopPushVideo() {
     mSendView.btnTextChangeToStart();
     mSendInteractor.stopPush();
    }

    @Override
    public void takePic() {

    }

    @Override
    public void switchCamera() {
        mSendInteractor.switchCamera();
        mSendView.resetPopupWindow();
    }

    @Override
    public void setPreviewSize(Size previewSize) {
              mSendInteractor.setPreviewSize(previewSize);

    }

    @Override
    public void setPushSize(Size pushSize) {
             mSendInteractor.setPushSize(pushSize);
    }

    @Override
    public void setPushType(String pushType) {
             mSendInteractor.setPushType(pushType);
    }

    @Override
    public void onDestroy() {
        mSendInteractor.onDestroy();
    }
}
