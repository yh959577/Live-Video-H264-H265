package com.example.livelib.Receiver.Imp;

import android.view.SurfaceHolder;

import com.example.livelib.Receiver.Interface.Decoder;
import com.example.livelib.Receiver.Interface.Receiver;
import com.example.livelib.Receiver.Interface.UdpReceiver;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by UPC on 2018/1/18.
 */

public class ReceiverImp implements Receiver {
    private UdpReceiver mUdpReceiver;
    private Decoder mDecoder;
    private InetAddress mInetAddress;
    private int mPort;

    private static ReceiverImp instance;

    public static ReceiverImp buildReceiver(SurfaceHolder holder) {
        if (null == instance) {
            instance = new ReceiverImp(holder);
        }
        return instance;
    }

    private ReceiverImp(SurfaceHolder holder) {
        mUdpReceiver = new UdpReceiverImp();
        mDecoder=new DecoderImp(holder);

    }

    @Override
    public void startPlay(String address) throws UnknownHostException, SocketException {
        mInetAddress = InetAddress.getByName(address.substring(0, address.indexOf(':')));
        mPort = Integer.valueOf(address.substring(address.indexOf(':') + 1));
        mUdpReceiver.initial(mInetAddress, mPort);
        mUdpReceiver.startReceive();
        mDecoder.startDecode();
    }

    @Override
    public void startPlay(String address, int port) throws UnknownHostException, SocketException {
        mInetAddress = InetAddress.getByName(address);
        mPort = port;
        mUdpReceiver.initial(mInetAddress, mPort);
        mUdpReceiver.startReceive();
        mDecoder.startDecode();
    }

    @Override
    public void stopPlay() {
        mUdpReceiver.stopReceive();
        mDecoder.stopDecode();
    }

    @Override
    public void takePic() {

    }
}
