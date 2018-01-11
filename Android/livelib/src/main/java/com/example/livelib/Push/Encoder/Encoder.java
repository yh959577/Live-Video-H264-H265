package com.example.livelib.Push.Encoder;

import android.util.Size;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Encoder {
    void initial();
    void startEncoder();
    void stopEncoder();
    void setPushSize(Size pushSize);
    void setPushType(String pushType);
    Size getPushSize();
    void onDestroy();
}
