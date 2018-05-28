package com.example.livelib.Receiver.Imp;

import android.os.Environment;
import android.util.Log;

import com.example.livelib.Receiver.Interface.UdpReceiver;
import com.example.livelib.Util.ByteTransitionUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hamik Young on 2018/1/22.
 */

public class UdpReceiverImp implements UdpReceiver {
    //private UdpStruct mUdpStruct;
    private SendUdpHeartRunnable mSendHeartRunnable;
    private DatagramSocket mDatagramSocket;
    private DatagramPacket mDatagramPacket;
    private byte[] mRcBuf;
    //   private Thread mReceiveThread;
    //  private Thread mProcessThread;
    private final String TAG = "UdpReceiverImp";
    private LinkedList<UdpStruct> mH264OrderedList;
    private List<UdpStruct> mH265OrderedList;
   private FileOutputStream fileOutputStream;
    private LinkedBlockingQueue<byte[]> mUdpReceiveQueue;
    private ScheduledExecutorService mSendHeartService;
    private ExecutorService mReceiveService;

    @Override
    public void initial(InetAddress address, int port) throws SocketException {
        initialUdp();
        try {
            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() +
                    "/receive264.h264"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mSendHeartRunnable = new SendUdpHeartRunnable(mDatagramSocket, address, port);
        mSendHeartService = Executors.newSingleThreadScheduledExecutor();

        mReceiveService = Executors.newFixedThreadPool(2);


    }

    @Override
    public void startReceive() {

        mReceiveService.submit(()->{
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Log.i(TAG, "prepare receiveData");
                    mDatagramSocket.receive(mDatagramPacket);
                //    fileOutputStream.write(mDatagramPacket.getData(),0,mDatagramPacket.getLength());
                    int packetLen=ByteTransitionUtil.byteToInt(mDatagramPacket.getData());
                   byte[] realData=new byte[packetLen];
                    Log.i(TAG, "startReceive: receiveDataRealLen==="+packetLen);
                    Log.i(TAG, "startReceive: receiveDataLen!!!!==="+mDatagramPacket.getLength());
                    Log.i(TAG, "startReceive: receiveDataActualLen==="+mDatagramPacket.getData().length);
                    System.arraycopy(mDatagramPacket.getData(),4,realData,0,packetLen);
               //     Log.i(TAG, "startReceive: realDataContent==="+ Arrays.toString(realData));

                //    fileOutputStream.write(mDatagramPacket.getData());
                   mUdpReceiveQueue.offer(realData);
              //   mDatagramPacket.setLength(mRcBuf.length);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mReceiveService.submit(() -> {
            Log.i(TAG, "initialSubmit2: ");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] data=mUdpReceiveQueue.take();




                    UdpStruct udpStruct = new UdpStruct(data, data.length);
                    Log.i(TAG, "UdpStructDataLen: =="+data.length);
                    Log.i(TAG, "UdpStructDataSequnce:=== "+udpStruct.getSequenceNum());
                    Log.i(TAG, "UdpStructDataTimeNum===: "+udpStruct.getTimeNum());
                    Log.i(TAG, "UdpStructDataVideoDataContent: "+Arrays.toString(udpStruct.getVideoData()));


                    ReceiveQueueManager.addDataTo264UdpOrderQueue(udpStruct);




//                    if (udpStruct.getVideoTypeTag() == 'r') {
//                        Log.i(TAG, "find this is H264 format");
//                        if (mH264OrderedList.size() > 0)
//                            for (int i = mH264OrderedList.size() - 1; i >= 0; i--) {
//                                if (udpStruct.getSequenceNum() > mH264OrderedList.get(i).getSequenceNum()) {
//                                    mH264OrderedList.add(i + 1, udpStruct);
//                                    break;
//                                }
//                            }
//                        else mH264OrderedList.add(udpStruct);
//                        ReceiveQueueManager.addDataTo264UdpOrderQueue(mH264OrderedList.get(0));
//                        mH264OrderedList.remove(0);
//                        Log.i(TAG, "write to h264 udp orderedQueue: ");
//
//
//                    } else if (udpStruct.getVideoTypeTag() == 'e') {
//                        Log.i(TAG, "find this is H265 format");
//                        if (mH265OrderedList.size() > 0)
//                            for (int i = mH265OrderedList.size() - 1; i >= 0; i--) {
//                                if (udpStruct.getSequenceNum() > mH265OrderedList.get(i).getSequenceNum()) {
//                                    //   ReceiveQueueManager.addDataToOrderList(i + 1, udpStruct);
//                                    mH265OrderedList.add(i + 1, udpStruct);
//                                    break;
//                                }
//                            }
//                        else mH265OrderedList.add(udpStruct);
//
//                        ReceiveQueueManager.addDataTo265UdpOrderQueue(mH265OrderedList.get(0));
//                        mH265OrderedList.remove(0);
//                        Log.i(TAG, "write to h264 udp orderedQueue: ");
//                    } else {
//                        Log.i(TAG, "find this is wrong format no format info in packet: ");
//                    }
                    mDatagramPacket.setLength(mRcBuf.length);
                } catch ( InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        });
        mSendHeartService.scheduleAtFixedRate(mSendHeartRunnable, 5, 45, TimeUnit.SECONDS);
    }

    @Override
    public void stopReceive() {
        mReceiveService.shutdown();
        mSendHeartService.shutdown();

    }

    private void initialUdp() throws SocketException {
    //  udpCache = new LinkedBlockingDeque<>();
        mDatagramSocket = new DatagramSocket();
        mDatagramSocket.setReceiveBufferSize(1024*1024);
        mRcBuf = new byte[500];
        mDatagramPacket = new DatagramPacket(mRcBuf, mRcBuf.length);
        mUdpReceiveQueue=new LinkedBlockingQueue<>();
        mH264OrderedList = new LinkedList<>();

    }
}
