package com.example.hy.liveexampleandroid.LivePlayer;

/**
 * Created by Hamik Young on 2018/1/19.
 */

public interface PlayPresenter {
    void startPlay(String address);
    void startPlay(String address,int port);
    void stopPlay();
    void takePic();
    void onDestroy();



}
