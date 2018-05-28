package com.example.livelib.Push.Queue;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class QueueManager {
    //private static  int YUVQueueSize=150;
    //private static  int FrameQueueSize=300;

     // private static ConcurrentLinkedQueue<byte[]> YUVQueue=new ConcurrentLinkedQueue<>();
     // private static ConcurrentLinkedQueue<byte[]> FrameQueue=new ConcurrentLinkedQueue<>();
    private static LinkedBlockingQueue<byte[]> YUVQueue = new LinkedBlockingQueue<>();
    //private static ArrayBlockingQueue<byte[]> FrameQueue = new ArrayBlockingQueue<>(FrameQueueSize);
    private static LinkedBlockingQueue<byte[]> FrameQueue=new LinkedBlockingQueue<>();

    public static boolean addDataToYUVQueue(byte[] imageData) {return YUVQueue.offer(imageData);
    }

    public static byte[] takeDataFromYUVQueue() throws InterruptedException {
        return YUVQueue.take();
    }

    public static int getYUVQueueSize() {
        return YUVQueue.size();
    }

    public static void clearYUVQueue(){
        YUVQueue.clear();
    }


    public static boolean addDataToFrameQueue(byte[] frameData) {
        return FrameQueue.offer(frameData);
    }

    public static byte[] takeDataFromFrameQueue() throws InterruptedException {
        return FrameQueue.take();
    }

    public static int getFrameQueueSize() {
        return FrameQueue.size();
    }

    public static void clearFrameQueue(){
        FrameQueue.clear();
    }

    public static boolean isFrameQueueEmpty(){
        return FrameQueue.isEmpty();
    }

    public static boolean isYUVQueueEmpty(){
        return YUVQueue.isEmpty();
    }
}
