package com.example.hy.liveexampleandroid.Push.Encoder;

import android.util.Log;

import com.example.hy.liveexampleandroid.Push.Queue.QueueManager;

/**
 * Created by UPC on 2018/1/7.
 */

public class EncoderImp implements Encoder {
    private static final String TAG="EncoderImp";


    @Override
    public void startEncoder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    QueueManager.pollDataFromYUVQueue();
                    Log.i(TAG, "run: takeYUV");

            }
        }).start();
    }

    @Override
    public void stopEncoder() {

    }

    @Override
    public void onDestroy() {

    }
}
