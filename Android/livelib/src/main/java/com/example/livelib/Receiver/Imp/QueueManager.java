package com.example.livelib.Receiver.Imp;

import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Hamik Young on 2018/1/22.
 */

public class QueueManager {
    private static ArrayBlockingQueue<byte[]> frameQueue = new ArrayBlockingQueue<byte[]>(10);
    private static ArrayBlockingQueue<UdpStruct> udpOrderQueue = new ArrayBlockingQueue<UdpStruct>(300);


    static void addDataToFrameQueue(byte[] b) {
        if (frameQueue.size()>10)
            frameQueue.remove();
        frameQueue.add(b);
    }

    static byte[] getDataFromFrameQueue() {
        return frameQueue.poll();
    }

    static int getFrameQueueSize() {
        return frameQueue.size();
    }

    static void addDataToUdpOrderQueue(UdpStruct udpStruct) {
        if (udpOrderQueue.size() > 250)
            udpOrderQueue.remove();
        udpOrderQueue.add(udpStruct);
    }

    static UdpStruct getUdpFromOrderQueue() {
        return udpOrderQueue.poll();
    }

    static int getOrderQueueSize() {
        return udpOrderQueue.size();
    }
}
