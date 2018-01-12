package com.example.livelib.Push.Queue;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class QueueManager {
    private static  int YUVQueueSize=150;
    private static  int FrameQueueSize=300;
    private static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue(YUVQueueSize);
    private static ArrayBlockingQueue<byte[]> FrameQueue = new ArrayBlockingQueue(FrameQueueSize);

    public static void addDataToYUVQueue(byte[] imageData) {
        YUVQueue.add(imageData);
    }

    public static byte[] pollDataFromYUVQueue() {
        return YUVQueue.poll();
    }

    public static int getYUVQueueSize() {
        return YUVQueue.size();
    }

    public static int getYUVQueueCapacity(){
        return YUVQueueSize;
    }

    public static void clearYUVQueue(){
        YUVQueue.clear();
    }


    public static void addDataToFrameQueue(byte[] frameData) {
        FrameQueue.add(frameData);
    }

    public static byte[] pollDataFromFrameQueue() {
        return FrameQueue.poll();
    }

    public static int getFrameQueueSize() {
        return FrameQueue.size();
    }

    public static int getFrameQueueCapacity(){
        return FrameQueueSize;
    }
    public static void clearFrameQueue(){
        FrameQueue.clear();
    }

}
