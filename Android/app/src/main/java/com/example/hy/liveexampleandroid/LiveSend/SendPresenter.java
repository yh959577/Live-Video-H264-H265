package com.example.hy.liveexampleandroid.LiveSend;

/**
 * Created by Administrator on 2017/12/29.
 */

public interface SendPresenter {
    int initialCamera();
    int startPushVideo(String ip);
    void stopPushVideo();
    void takePic();
    void switchCamera();
}
