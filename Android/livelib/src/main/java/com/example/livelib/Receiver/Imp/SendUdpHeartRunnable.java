package com.example.livelib.Receiver.Imp;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Hamik Young on 2018/1/22.
 */

public class SendUdpHeartRunnable implements Runnable {
  private DatagramSocket mDatagramSocket;
  private DatagramPacket mDatagramPacket;
   private  final  String TAG="SendUdpHeartRunnable";


    SendUdpHeartRunnable(DatagramSocket datagramSocket,InetAddress address,int port){
        mDatagramSocket=datagramSocket;
        mDatagramPacket=new DatagramPacket(new byte[]{'M', 'S', 'G'}, 3,address , port);
    }

    @Override
    public void run() {
        try {
            mDatagramSocket.send(mDatagramPacket);
            Log.i(TAG, "run: sendHeart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
