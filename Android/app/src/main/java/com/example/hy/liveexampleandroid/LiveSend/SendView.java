package com.example.hy.liveexampleandroid.LiveSend;

import android.graphics.SurfaceTexture;

/**
 * Created by Administrator on 2017/12/29.
 */

public interface SendView {
    void toastMessage(String message);
    void showSettingPopWindow();
    void IpIsEmpty();
    void IpError();
    void btnTextChangeToStart();
    void btnTextChangeToStop();
    void initialCamera();
}
