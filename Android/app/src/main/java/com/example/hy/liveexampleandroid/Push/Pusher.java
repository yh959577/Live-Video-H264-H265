package com.example.hy.liveexampleandroid.Push;

import android.hardware.camera2.CameraAccessException;
import android.util.Size;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Pusher {
  void initial();
  void startPush() throws CameraAccessException;
  void stopPush() throws CameraAccessException;
  void setPreviewSize(Size previewSize);
  void setPushSize(Size pushSize);
  void setPushType(String pushType);
  void switchCamera();
  void onDestroy();
}
