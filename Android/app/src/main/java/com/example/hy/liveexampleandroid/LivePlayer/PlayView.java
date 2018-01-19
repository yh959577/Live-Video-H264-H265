package com.example.hy.liveexampleandroid.LivePlayer;

import android.view.TextureView;

/**
 * Created by Hamik Young on 2018/1/19.
 */

public interface PlayView {

    void btnTextChangeToStart();

    void btnTextChangeToStop();

    void showProgress();

    void stopProgress();

    boolean IpIsValid();

    boolean IpIsEmpty();

    void showIpEmptyError();

    void showIpInvalidError();

    TextureView supplyTextureView();

}
