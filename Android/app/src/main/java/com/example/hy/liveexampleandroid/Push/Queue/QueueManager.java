package com.example.hy.liveexampleandroid.Push.Queue;

import android.media.Image;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class QueueManager {
    private static LinkedBlockingQueue<byte[]> ImageQueue = new LinkedBlockingQueue();
    private static LinkedBlockingQueue<byte[]> FrameQueue = new LinkedBlockingQueue();

    public static void putDataToImageQueue(byte[] imageData) throws InterruptedException {
        ImageQueue.put(imageData);
    }

    public static byte[] takeDataFromImageQueue() throws InterruptedException {
        return ImageQueue.take();
    }

    public static void putDataToFrameQueue(byte[] frameData) throws InterruptedException {
        FrameQueue.put(frameData);
    }

    public static byte[] takeDataFromFrameQueue() throws InterruptedException {
        return FrameQueue.take();
    }
}
