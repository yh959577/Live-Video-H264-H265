package com.example.hy.liveexampleandroid.LiveSend;

/**
 * Created by Hamik Young on 2017/12/29.
 */

public interface SendPresenter {
    int initialPusher();
    int startPushVideo(String ip);
    void stopPushVideo();
    void takePic();
    void switchCamera();
    void onDestroy();
}
