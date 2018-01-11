package com.example.livelib.Push.Camera;

import android.hardware.camera2.CameraAccessException;
import android.util.Size;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Camera {

 void initial();
 void setIsProcessImage(boolean isProcessImage);
 void switchCamera();
 void setPreviewSize(Size previewSize);
 void startPush(Size encoderSize) throws CameraAccessException;
 void stopPush() throws CameraAccessException;
 void closeCamera();

}
