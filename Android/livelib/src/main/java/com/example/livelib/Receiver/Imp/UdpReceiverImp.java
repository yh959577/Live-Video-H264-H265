package com.example.livelib.Receiver.Imp;

import android.util.Log;

import com.example.livelib.Receiver.Interface.UdpReceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
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
    private Thread mReceiveThread;
    private boolean isReceiveData;
    private final String TAG = "UdpReceiverImp";
    private List<UdpStruct> mOrderedList;

    @Override
    public void initial(InetAddress address, int port) throws SocketException {
        initialUdp();
        mSendHeartRunnable = new SendUdpHeartRunnable(mDatagramSocket, address, port);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(mSendHeartRunnable, 5, 5, TimeUnit.SECONDS);


        mReceiveThread = new Thread(() -> {
            mSendHeartRunnable.run();
            while (isReceiveData) {
                try {
                    mDatagramSocket.receive(mDatagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mRcBuf[0] == 'R' && mRcBuf[1] == 'C') {
                    Log.i(TAG, "receive heart beat!!!!");
                } else {
                    Log.i(TAG, "receive video data len===" + mDatagramPacket.getLength());
                    UdpStruct udpStruct = new UdpStruct(mRcBuf, mDatagramPacket.getLength());
                    if (mOrderedList.size() > 0)
                        for (int i = mOrderedList.size() - 1; i >= 0; i--) {
                            Log.i(TAG, "QueueManager.getOrderListSize(): ==" + mOrderedList.size());
                            Log.i(TAG, "initial: I===" + i);
                            if (udpStruct.getSequenceNum() > mOrderedList.get(i).getSequenceNum()) {
                                //   QueueManager.addDataToOrderList(i + 1, udpStruct);
                                mOrderedList.add(i + 1, udpStruct);
                                break;
                            }
                        }
                    else mOrderedList.add(udpStruct);
                }
                if (mOrderedList.size() > 50) {
                    QueueManager.addDataToUdpOrderQueue(mOrderedList.get(0));
                    mOrderedList.remove(0);
                }
            }
            mOrderedList.clear();
            service.shutdown();
            mDatagramSocket.close();
        });
    }

    @Override
    public void startReceive() {
        isReceiveData = true;
        mReceiveThread.start();
    }

    @Override
    public void stopReceive() {
        isReceiveData = false;
    }

    private void initialUdp() throws SocketException {
        mDatagramSocket = new DatagramSocket();
        mRcBuf = new byte[500];
        mDatagramPacket = new DatagramPacket(mRcBuf, mRcBuf.length);
        mOrderedList = new ArrayList<>();
    }
}
