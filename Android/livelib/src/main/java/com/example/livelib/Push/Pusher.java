package com.example.livelib.Push;

import android.hardware.camera2.CameraAccessException;
import android.util.Size;

import java.net.UnknownHostException;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Pusher {
  void initial() throws UnknownHostException;
  void startPush(String pushAddress) throws CameraAccessException, UnknownHostException;
  void stopPush() throws CameraAccessException;
  void setPreviewSize(Size previewSize);
  void setPushSize(Size pushSize);
  void setPushType(String pushType);
  void setPushAddress(String address);
  void switchCamera();
  void onDestroy();
}
