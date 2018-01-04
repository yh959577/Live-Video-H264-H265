package com.example.hy.liveexampleandroid.Push;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Pusher {
  void initial();
  void startPush();
  void stopPush();
  void onDestroy();
}
