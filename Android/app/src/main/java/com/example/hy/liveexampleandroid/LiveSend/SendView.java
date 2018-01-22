package com.example.hy.liveexampleandroid.LiveSend;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.view.TextureView;

/**
 * Created by Hamik Young on 2017/12/29.
 */

public interface SendView {
    void showSettingPopWindow();
    void showIpEmptyError();
    void showIpInvalidError();
    void btnTextChangeToStart();
    void btnTextChangeToStop();
    TextureView supplyTextureView();
    CameraManager supplyCameraManager();
    void resetPopupWindow();
}
