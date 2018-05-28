package com.example.livelib.Push.UdpSend;

import android.media.AudioRecord;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

import com.example.livelib.Push.Queue.QueueManager;
import com.example.livelib.Util.ByteTransitionUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

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
    private int singleUdpSize = 300;
    private int mSendPacketNumber=0; //how many packet had send
    private static final String TAG = "VideoSenderImp";
    private int udpPackNum = 0;
    private FileOutputStream fileOutputStream;
    private FileOutputStream sendFileOutputStream;
    private DatagramPacket mSendPacket;
    private LinkedBlockingDeque<byte[]> mUdpSendQueue;
    private ExecutorService mExecutor;

    @Override
    public void initial(String pushAddress, String pushType) throws UnknownHostException {
        mInetAddress = InetAddress.getByName(pushAddress.substring(0, pushAddress.indexOf(':')));
        mPort = Integer.valueOf(pushAddress.substring(pushAddress.indexOf(':') + 1));
        mPushType = pushType;
        if (mPushType.equals(MediaFormat.MIMETYPE_VIDEO_AVC))
            mTypeTag = 'r';
        else if (mPushType.equals(MediaFormat.MIMETYPE_VIDEO_HEVC))
            mTypeTag = 'e';
        try {
            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()
                    + "/testH264.h264"));
            sendFileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()
                        + "/sendFile.h264"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        initialSendWork();
    }

    @Override
    public void initial(String ip, int port, String pushType) throws UnknownHostException {
        mInetAddress = InetAddress.getByName(ip);
        mPort = port;
        mPushType = pushType;
        initialSendWork();
    }

    @Override
    public void startSendVideoData() {
        mExecutor.submit(()->{
            while (!Thread.currentThread().isInterrupted()) {
                byte[] frameData = new byte[0];
                try {
                    frameData = QueueManager.takeDataFromFrameQueue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Log.i(TAG, "initialSendWork: frameQueueSize===" + QueueManager.getFrameQueueSize());
                    Log.i(TAG, "the frame size: ===="+frameData.length);
                    //sendPurePacket(frameData, singleUdpSize);
                    sendPacket(frameData, singleUdpSize);
                    //fileOutputStream.write(frameData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            QueueManager.clearFrameQueue();

            Log.i(TAG, "initialSendWork: pacNum====" + udpPackNum);

        });

        mExecutor.submit(()->{
            while (!Thread.currentThread().isInterrupted()||!mUdpSendQueue.isEmpty()){
                try {
                    byte[] data=mUdpSendQueue.take();
                     data= addLengthToHead(data);

                    if (mSendPacket==null)
                        mSendPacket=new DatagramPacket(data,data.length,mInetAddress,mPort);
                    else
                        mSendPacket.setData(data);
                    mDatagramSocket.send(mSendPacket);
                    sendFileOutputStream.write(data);
                    // Log.i(TAG, "finish one send act !");
                   //System.currentTimeMillis();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            mUdpSendQueue.clear();
            mDatagramSocket.close();
        });
    }

    @Override
    public void stop() {
        //isRunning = false;
        //mSendThread.interrupt();
        mExecutor.shutdown();
    }

    private void initialSendWork() {

        try {
            mDatagramSocket = new DatagramSocket();
            mDatagramSocket.setSendBufferSize(5*1024);
            Log.i(TAG, "sendBuffer==: "+mDatagramSocket.getSendBufferSize());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        mExecutor= Executors.newFixedThreadPool(2);
        mUdpSendQueue=new LinkedBlockingDeque<>();
    }

    private void sendPurePacket(byte[] frameData, int packetSize) throws IOException {
        if (udpPackNum >= Integer.MAX_VALUE / 2) {
            udpPackNum = 0;
        }
        int len = frameData.length;
        int packetNum = len / packetSize;
        Log.i(TAG, "one bag====: " + packetNum);
        int remainNum = len % packetSize;
        int offset = 0;
        for (int i = 0; i < packetNum; i++) {
            byte[] sendBytes = new byte[packetSize];
            System.arraycopy(frameData, i * packetSize, sendBytes, 0, packetSize);
//            if (mSendPacket == null)
//                mSendPacket = new DatagramPacket(sendBytes, sendBytes.length, mInetAddress, mPort);
//            else
//                mSendPacket.setData(sendBytes);
//            mDatagramSocket.send(mSendPacket);
            mUdpSendQueue.add(sendBytes);
           // sendFileOutputStream.write(sendBytes);
            udpPackNum++;
            offset++;
        }
        byte[] remainBytes = new byte[remainNum];
        System.arraycopy(frameData, offset * packetSize, remainBytes, 0, remainNum);

//        if (mSendPacket == null)
//            mSendPacket = new DatagramPacket(remainBytes, remainBytes.length, mInetAddress, mPort);
//        else
//            mSendPacket.setData(remainBytes);
//        mDatagramSocket.send(mSendPacket);
        mUdpSendQueue.add(remainBytes);
      //  sendFileOutputStream.write(remainBytes);
        udpPackNum++;
        Log.i(TAG, "sendPacketNum: " + udpPackNum);
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
        Log.i(TAG, "mSendPacket: len====" + len);
        int packetNum = len / packetSize;
        Log.i(TAG, "sendPacketTotalNum: "+packetNum);
        int remainNum = len % packetSize;
        int offset = 0;
        for (int i = 0; i < packetNum; i++) {
            byte[] sendBytes = new byte[packetSize];
            System.arraycopy(srcData, i * packetSize, sendBytes, 0, packetSize);
            byte[] completeUdpData = addHead(sendBytes);
//            if (mSendPacket == null)
//                mSendPacket = new DatagramPacket(completeUdpData, completeUdpData.length, mInetAddress, mPort);
//            else
//                mSendPacket.setData(completeUdpData);

          //  mDatagramSocket.send(mSendPacket);
            mUdpSendQueue.add(completeUdpData);
            //sendFileOutputStream.write(completeUdpData);
            udpPackNum++;
            offset++;
            Log.i(TAG, "sendPacketNum: " + udpPackNum);
        }
        byte[] remainBytes = new byte[remainNum];
        System.arraycopy(srcData, offset * packetSize, remainBytes, 0, remainNum);
        byte[] completeUdpData = addHead(remainBytes);
//        if (mSendPacket == null)
//            mSendPacket = new DatagramPacket(completeUdpData, completeUdpData.length, mInetAddress, mPort);
//        else
//            mSendPacket.setData(completeUdpData);
//
//        mDatagramSocket.send(mSendPacket);
        mUdpSendQueue.add(completeUdpData);
        //sendFileOutputStream.write(completeUdpData);
        udpPackNum++;
        Log.i(TAG, "sendPacketNum: " + udpPackNum);
    }

    private byte[] addHead(byte[] sendData) {
        byte[] sequenceNum = ByteTransitionUtil.intToByte(udpPackNum);   //order num
        byte[] timeNum = ByteTransitionUtil.longToBytes(System.currentTimeMillis()); //
        byte[] completeUdpData = new byte[sendData.length + sequenceNum.length + timeNum.length + 1];

        System.arraycopy(sequenceNum, 0, completeUdpData, 0, sequenceNum.length);
        System.arraycopy(timeNum, 0, completeUdpData, sequenceNum.length, timeNum.length);
        completeUdpData[sequenceNum.length + timeNum.length] = mTypeTag;
        System.arraycopy(sendData, 0, completeUdpData, sequenceNum.length + timeNum.length + 1, sendData.length);

        return completeUdpData;
    }

    private byte[] addLengthToHead(byte[] completeData){
        byte[] sendData=new byte[completeData.length+4];
        System.arraycopy(ByteTransitionUtil.intToByte( completeData.length),
                0,sendData,0,4);
        System.arraycopy(completeData,0,sendData,4,completeData.length);
        return sendData;
    }

}
