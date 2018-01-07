package com.example.hy.liveexampleandroid.Push.Queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class QueueManager {
    private static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue(30);
    private static ArrayBlockingQueue<byte[]> FrameQueue = new ArrayBlockingQueue(30);

    public static void addDataToYUVQueue(byte[] imageData)  {
        YUVQueue.add(imageData);
    }
    public static int getYUVSize(){
        return YUVQueue.size();
    }

    public static byte[] pollDataFromYUVQueue()  {
        return YUVQueue.poll();
    }

    public static void addDataToFrameQueue(byte[] frameData)  {
        FrameQueue.add(frameData);
    }

    public static byte[] pollDataFromFrameQueue(){
        return FrameQueue.poll();
    }
}
