package com.example.livelib.Receiver.Imp;

import com.example.livelib.Receiver.Interface.UdpReceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.Executor;
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

    @Override
    public void initial(InetAddress address, int port) throws SocketException {
        initialUdp();
        mSendHeartRunnable = new SendUdpHeartRunnable(mDatagramSocket, address, port);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(mSendHeartRunnable, 0, 35, TimeUnit.SECONDS);


        mReceiveThread = new Thread(() -> {
            while (isReceiveData) {
                try {
                    mDatagramSocket.receive(mDatagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mRcBuf[0] == 'R' && mRcBuf[1] == 'C') {
                    System.out.println("receive heart beat!!!!");
                } else {
                    System.out.println("receive video data len===" + mDatagramPacket.getLength());
                    UdpStruct udpStruct = new UdpStruct(mRcBuf, mDatagramPacket.getLength());
                    if (ReceiveDataManager.getOrderListSize() > 0)
                        for (int i = ReceiveDataManager.getOrderListSize() - 1; i >= 0; i--) {
                            if (udpStruct.getSequenceNum() > ReceiveDataManager.getUdpFromOrderList(i).getSequenceNum()) {
                                ReceiveDataManager.addDataToOrderList(i + 1, udpStruct);
                                break;
                            }
                        }
                    else ReceiveDataManager.addDataToOrderList(0, udpStruct);
                }
            }
            ReceiveDataManager.clearOrderList();
            service.shutdown();
            mDatagramSocket.close();
        });





    }

    @Override
    public void startReceive() {
        isReceiveData=true;
        mReceiveThread.start();
    }

    @Override
    public void stopReceive() {
       isReceiveData=false;
    }

    private void initialUdp() throws SocketException {
        mDatagramSocket = new DatagramSocket();
        mRcBuf = new byte[500];
        mDatagramPacket = new DatagramPacket(mRcBuf, mRcBuf.length);
    }
}
