package com.example.hy.liveexampleandroid.Push.Encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import com.example.hy.liveexampleandroid.Push.Queue.QueueManager;

/**
 * Created by UPC on 2018/1/7.
 */

public class EncoderImp implements Encoder {
    private static final String TAG = "EncoderImp";
    private Thread mEncodeThread;
    private String mEncodeType;
    private Size mEncodeSize;
    private MediaCodec mMediaCodec;
    private MediaFormat mMediaFormat;

    public EncoderImp() {



        mEncodeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                QueueManager.pollDataFromYUVQueue();
            }
        });
    }

    @Override
    public void startEncoder() {
       mEncodeThread.start();
    }

    @Override
    public void stopEncoder() {

    }

    @Override
    public void onDestroy() {

    }
}
