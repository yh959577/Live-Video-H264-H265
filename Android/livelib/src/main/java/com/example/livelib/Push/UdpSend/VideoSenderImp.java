package com.example.livelib.Push.UdpSend;

import android.media.MediaFormat;
import android.util.Log;

import com.example.livelib.Push.Queue.QueueManager;
import com.example.livelib.Util.ByteTransitionUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Hamik Young on 2018/1/11.
 */

public class VideoSenderImp implements VideoSender {
    private int mPort;
    private DatagramSocket mDatagramSocket;
    private InetAddress mInetAddress;
    private Thread mSendThread;
    private String mPushType;
    private byte mTypeTag;
    private boolean isRunning = true;
    private int singleUdpSize = 300;
    private static final String TAG = "VideoSenderImp";
    private int udpPackNum = 0;

    @Override
    public void initial(String pushAddress,String pushType) throws UnknownHostException {
        mInetAddress = InetAddress.getByName(pushAddress.substring(0, pushAddress.indexOf(':')));
        mPort = Integer.valueOf(pushAddress.substring(pushAddress.indexOf(':') + 1));
        mPushType=pushType;
        if (mPushType.equals(MediaFormat.MIMETYPE_VIDEO_AVC))
            mTypeTag='r';
       else if (mPushType.equals(MediaFormat.MIMETYPE_VIDEO_HEVC))
            mTypeTag='e';

        initialSendWork();
    }

    @Override
    public void initial(String ip, int port,String pushType) throws UnknownHostException {
        mInetAddress = InetAddress.getByName(ip);
        mPort = port;
        mPushType=pushType;
        initialSendWork();
    }

    @Override
    public void startSendVideoData() {
        isRunning = true;
        mSendThread.start();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    private void initialSendWork() {

        try {
            mDatagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        mSendThread = new Thread(() -> {
            while (isRunning || QueueManager.getFrameQueueSize() > 0) {
                if (QueueManager.getFrameQueueSize() > 0) {
                    try {
                        Log.i(TAG, "initialSendWork: frameQueueSize===" + QueueManager.getFrameQueueSize());
                        sendPacket(QueueManager.pollDataFromFrameQueue(), singleUdpSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            QueueManager.clearFrameQueue();
            mDatagramSocket.close();
            Log.i(TAG, "initialSendWork: pacNum====" + udpPackNum);


        });
    }

    private void sendPacket(byte[] frameData, int packetSize) throws IOException {
        if (frameData == null) {
            Log.i(TAG, "sendPacketNull: ");
            return;
        }
        if (udpPackNum >= Integer.MAX_VALUE / 2) {
            udpPackNum = 0;
        }
        byte[] srcData = new byte[frameData.length];
        System.arraycopy(frameData, 0, srcData, 0, frameData.length);
        int len = srcData.length;
        Log.i(TAG, "sendPacket: len====" + len);
        int packetNum = len / packetSize;
        int remainNum = len % packetSize;
        int offset = 0;
        for (offset = 0; offset < packetNum; offset++) {
            byte[] sendBytes = new byte[packetSize];
            System.arraycopy(srcData, offset * packetSize, sendBytes, 0, packetSize);
            byte[] completeUdpData = addHead(sendBytes);
            mDatagramSocket.send(new DatagramPacket(completeUdpData, completeUdpData.length, mInetAddress, mPort));
            udpPackNum++;
            Log.i(TAG, "sendPacketNum: " + udpPackNum);
        }
        byte[] remainBytes = new byte[remainNum];
        System.arraycopy(srcData, offset * packetSize, remainBytes, 0, remainNum);
        byte[] completeUdpData = addHead(remainBytes);
        mDatagramSocket.send(new DatagramPacket(completeUdpData, completeUdpData.length, mInetAddress, mPort));
        udpPackNum++;
        Log.i(TAG, "sendPacketNum: " + udpPackNum);
    }

    private byte[] addHead(byte[] sendData) {
        byte[] sequenceNum = ByteTransitionUtil.intToByte(udpPackNum);
        byte[] timeNum = ByteTransitionUtil.longToBytes(System.currentTimeMillis());
        byte[] completeUdpData = new byte[sendData.length + sequenceNum.length + timeNum.length+1];

        System.arraycopy(sequenceNum, 0, completeUdpData, 0, sequenceNum.length);
        System.arraycopy(timeNum, 0, completeUdpData, sequenceNum.length, timeNum.length);
        completeUdpData[sequenceNum.length+timeNum.length]=mTypeTag;
        System.arraycopy(sendData, 0, completeUdpData, sequenceNum.length + timeNum.length+1, sendData.length);

        return completeUdpData;
    }

}
