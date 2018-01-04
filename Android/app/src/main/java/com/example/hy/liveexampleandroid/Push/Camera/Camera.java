package com.example.hy.liveexampleandroid.Push.Camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.os.Handler;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Camera {

 void initial();
 void setIsProcessImage(boolean isProcessImage);
 void onDestroy();

}
