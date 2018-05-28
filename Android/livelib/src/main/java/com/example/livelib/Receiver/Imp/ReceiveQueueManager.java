package com.example.livelib.Receiver.Imp;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hamik Young on 2018/1/22.
 */

public class ReceiveQueueManager {
    //private static ArrayBlockingQueue<byte[]> frameQueue = new ArrayBlockingQueue<byte[]>(200);
    //private static ArrayBlockingQueue<UdpStruct> mUdp264OrderQueue = new ArrayBlockingQueue<UdpStruct>(300);
    private static LinkedBlockingQueue<byte[]> mH264FrameQueue=new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<byte[]> mH265FrameQueue=new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<UdpStruct> mUdp264OrderQueue =new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<UdpStruct> mUdp265OrderQueue=new LinkedBlockingQueue<>();

    static boolean  addDataToH264FrameQueue(byte[] b){
     return  mH264FrameQueue.offer(b);
    }

    static byte[] getDataFromH264FrameQueue() throws InterruptedException {
        return mH264FrameQueue.take();
    }

    static boolean isH264FrameQueueEmpty(){
        return mH264FrameQueue.isEmpty();
    }

    static boolean  addDataToH265FrameQueue(byte[] b){
        return  mH265FrameQueue.offer(b);
    }

    static byte[] getDataFromH265FrameQueue() throws InterruptedException {
        return mH265FrameQueue.take();
    }

    static boolean isH265FrameQueueEmpty(){
        return mH265FrameQueue.isEmpty();
    }

    static boolean addDataTo264UdpOrderQueue(UdpStruct udpStruct) {
       return mUdp264OrderQueue.offer(udpStruct);
    }

    static UdpStruct get264UdpFromOrderQueue() throws InterruptedException {
        return mUdp264OrderQueue.take();
    }

    static int get264OrderQueueSize() {
        return mUdp264OrderQueue.size();
    }

    static boolean addDataTo265UdpOrderQueue(UdpStruct udpStruct) {
        return mUdp265OrderQueue.offer(udpStruct);
    }

    static UdpStruct get265UdpFromOrderQueue() throws InterruptedException {
        return mUdp265OrderQueue.take();
    }

    static int get265OrderQueueSize() {
        return mUdp265OrderQueue.size();
    }
}
