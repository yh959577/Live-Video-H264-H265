package com.example.hy.liveexampleandroid.LiveSend;

import android.util.Size;

/**
 * Created by Hamik Young on 2017/12/29.
 */

public interface SendPresenter {
    int initialPusher();
    int startPushVideo(String pushAddress);
    void stopPushVideo();
    void takePic();
    void switchCamera();
    void setPreviewSize(Size previewSize);
    void setPushSize(Size pushSize);
    void setPushType(String pushType);
    void onDestroy();
}
