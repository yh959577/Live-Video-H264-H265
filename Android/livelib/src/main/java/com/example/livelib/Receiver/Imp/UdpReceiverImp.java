package com.example.livelib.Receiver.Imp;

import android.os.Environment;
import android.util.Log;

import com.example.livelib.Receiver.Interface.UdpReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
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
    private List<UdpStruct> mOrderedList;
    private FileOutputStream fileOutputStream;
    private LinkedBlockingDeque<byte[]> udpCache;
    private ScheduledExecutorService mSendHeartService;
    private ExecutorService mReceiveService;
    private Runnable mReceiveUdpRunnable;
    private Runnable mProcessUdpRunnable;


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

        mReceiveService.submit(() -> {
                    mSendHeartRunnable.run();
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            mDatagramSocket.receive(mDatagramPacket);
                            if (mRcBuf[0] == 'R' && mRcBuf[1] == 'C') {
                                Log.i(TAG, "receive heart beat!!!!");
                            } else {
                                byte[] data = new byte[mDatagramPacket.getLength()];
                                System.arraycopy(mDatagramPacket.getData(), 0, data, 0, mDatagramPacket.getLength());
                                udpCache.add(data);
                            }
                            mDatagramPacket.setLength(mRcBuf.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
        mReceiveService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = udpCache.poll();
                if (data != null) {
                    try {
                        fileOutputStream.write(data);

                        UdpStruct udpStruct = new UdpStruct(data, data.length);
                        if (mOrderedList.size() > 0)
                            for (int i = mOrderedList.size() - 1; i >= 0; i--) {
                                Log.i(TAG, "ReceiveQueueManager.getOrderListSize(): ==" + mOrderedList.size());
                                if (udpStruct.getSequenceNum() > mOrderedList.get(i).getSequenceNum()) {
                                    //   ReceiveQueueManager.addDataToOrderList(i + 1, udpStruct);
                                    mOrderedList.add(i + 1, udpStruct);
                                    break;
                                }
                            }
                        else mOrderedList.add(udpStruct);
                        ReceiveQueueManager.addDataToUdpOrderQueue(mOrderedList.get(0));
                        mOrderedList.remove(0);
                        Log.i(TAG, "write to udp orderedQueue: ");
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
        });

//
//        mReceiveThread = new Thread(() -> {
//            mSendHeartRunnable.run();
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    mDatagramSocket.receive(mDatagramPacket);
//                    if (mRcBuf[0] == 'R' && mRcBuf[1] == 'C') {
//                        Log.i(TAG, "receive heart beat!!!!");
//                    } else {
//                        byte[] data = new byte[mDatagramPacket.getLength()];
//                        System.arraycopy(mDatagramPacket.getData(), 0, data, 0, mDatagramPacket.getLength());
//                        udpCache.add(data);
////                        Log.i(TAG, "receive video data len===" + mDatagramPacket.getLength());
////                        try {
////                            fileOutputStream.write(mDatagramPacket.getData(), 0, mDatagramPacket.getLength());
////
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
//
//
//                    }
//                    mDatagramPacket.setLength(mRcBuf.length);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//                //     UdpStruct udpStruct = new UdpStruct(mDatagramPacket.getData(), mDatagramPacket.getLength());
//
////                    if (mOrderedList.size() > 0)
////                        for (int i = mOrderedList.size() - 1; i >= 0; i--) {
////                            Log.i(TAG, "ReceiveQueueManager.getOrderListSize(): ==" + mOrderedList.size());
////                            Log.i(TAG, "initial: I===" + i);
////                            if (udpStruct.getSequenceNum() > mOrderedList.get(i).getSequenceNum()) {
////                                //   ReceiveQueueManager.addDataToOrderList(i + 1, udpStruct);
////                                mOrderedList.add(i + 1, udpStruct);
////                                break;
////                            }
////                        }
////                    else mOrderedList.add(udpStruct);
//                //mOrderedList.add(udpStruct);
//                //    ReceiveQueueManager.addDataToUdpOrderQueue(udpStruct);
//
//            }
//
////                if (mOrderedList.size() > 50) {
////                    ReceiveQueueManager.addDataToUdpOrderQueue(mOrderedList.get(0));
////                    mOrderedList.remove(0);
////                }
////                Log.d(TAG, "time consume: "+(System.currentTimeMillis()-startTime));
////            mOrderedList.clear();
////            service.shutdown();
////            mDatagramSocket.close();
//        });
//        mProcessThread = new Thread(() -> {
//
//            while (true) {
//                byte[] data = udpCache.poll();
//                if (data != null) {
//                    try {
//                        fileOutputStream.write(data);
//                        Log.i(TAG, "write to file: ");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            }
//        });
//

    }

    @Override
    public void startReceive() {
     //   mReceiveThread.start();
    //    mProcessThread.start();
        mSendHeartService.scheduleAtFixedRate(mSendHeartRunnable, 5, 45, TimeUnit.SECONDS);


    }

    @Override
    public void stopReceive() {
        mReceiveService.shutdown();
        mSendHeartService.shutdown();

    }

    private void initialUdp() throws SocketException {
        udpCache = new LinkedBlockingDeque<>();
        mDatagramSocket = new DatagramSocket();
        mRcBuf = new byte[500];
        mDatagramPacket = new DatagramPacket(mRcBuf, mRcBuf.length);
        mOrderedList = new ArrayList<>();
    }
}
