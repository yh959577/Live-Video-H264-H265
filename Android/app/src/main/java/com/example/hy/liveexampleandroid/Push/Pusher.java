package com.example.hy.liveexampleandroid.Push;

import android.util.Size;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Pusher {
  void initial();
  void startPush();
  void stopPush();
  void setPreviewSize(Size previewSize);
  void setPushSize(Size pushSize);
  void setPushType(String pushType);
  void onDestroy();
}
