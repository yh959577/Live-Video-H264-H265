package com.example.hy.liveexampleandroid.Push.Queue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class QueueManager {
    private static LinkedBlockingQueue<byte[]> YUVQueue = new LinkedBlockingQueue();
    private static LinkedBlockingQueue<byte[]> FrameQueue = new LinkedBlockingQueue();

    public static void putDataToYUVQueue(byte[] imageData) throws InterruptedException {
        YUVQueue.put(imageData);
    }

    public static byte[] takeDataFromYUVQueue() throws InterruptedException {
        return YUVQueue.take();
    }

    public static void putDataToFrameQueue(byte[] frameData) throws InterruptedException {
        FrameQueue.put(frameData);
    }

    public static byte[] takeDataFromFrameQueue() throws InterruptedException {
        return FrameQueue.take();
    }
}
