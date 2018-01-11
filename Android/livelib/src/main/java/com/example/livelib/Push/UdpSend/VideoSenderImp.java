package com.example.livelib.Push.UdpSend;

import com.example.livelib.Push.Queue.QueueManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by Hamik Young on 2018/1/11.
 */

public class VideoSenderImp implements VideoSender {
    private int mPort;
    private DatagramSocket mDatagramSocket;
    private InetAddress mInetAddress;
    private Thread mSendThread;
    private boolean isRunning=true;

    @Override
    public void initial(String pushAddress) throws UnknownHostException {
          mInetAddress =InetAddress.getByName( pushAddress.substring(0, pushAddress.indexOf(':')));
          mPort = Integer.valueOf(pushAddress.substring(pushAddress.indexOf(':') + 1));
          initialSendWork();
    }

    @Override
    public void initial(String ip, int port) throws UnknownHostException {
        mInetAddress=InetAddress.getByName(ip);
        mPort=port;
        initialSendWork();
    }

    @Override
    public void startSendVideoData() {
             isRunning=true;
             mSendThread.start();

    }

    @Override
    public void stop() {
         isRunning=false;
    }

    private void initialSendWork() {

        try {
            mDatagramSocket=new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        mSendThread=new Thread(()->{
            while (isRunning){
                if (QueueManager.getFrameQueueSize()>0){
                    try {
                        sendPacket(QueueManager.pollDataFromFrameQueue(),500);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            QueueManager.clearFrameQueue();
            mDatagramSocket.close();


        });
    }

    private void sendPacket(byte[] frameData,int packetSize) throws IOException {
        byte[] srcData= new byte[frameData.length];
        System.arraycopy(frameData,0,srcData,0,frameData.length);
        int len=srcData.length;
        int packetNum=len/packetSize;
        int remainNum=len%packetSize;
        int offset=0;
        for ( int i=0; i <packetNum ; i++) {
            byte[] sendBytes=new byte[packetSize];
            System.arraycopy(srcData,i*packetSize,sendBytes,0,packetSize);
            mDatagramSocket.send(new DatagramPacket(sendBytes,packetSize,mInetAddress,mPort));
            offset++;
        }
        byte[] remainBytes=new byte[remainNum];
        System.arraycopy(srcData,offset*packetSize,remainBytes,0,remainNum);
        mDatagramSocket.send(new DatagramPacket(remainBytes,remainNum,mInetAddress,mPort));
    }

    private void addHead(byte[] sendData){

    }
}
