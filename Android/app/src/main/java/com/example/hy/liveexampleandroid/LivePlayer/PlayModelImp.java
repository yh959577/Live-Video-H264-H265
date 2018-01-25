package com.example.hy.liveexampleandroid.LivePlayer;

import android.view.SurfaceHolder;

import com.example.livelib.Receiver.Imp.ReceiverImp;
import com.example.livelib.Receiver.Interface.Receiver;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Hamik Young on 2018/1/19.
 */

public class PlayModelImp implements PlayModel {
    private Receiver mReceiver;

    PlayModelImp(SurfaceHolder surfaceHolder) {
        mReceiver = ReceiverImp.buildReceiver(surfaceHolder);

    }

    @Override
    public void startPlay(String address) {

        try {
            mReceiver.startPlay(address);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void startPlay(String address, int port) {
        try {
            mReceiver.startPlay(address, port);
        } catch (SocketException |UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPlay() {
        mReceiver.stopPlay();
    }

    @Override
    public void takePic() {
        mReceiver.takePic();
    }

    @Override
    public void onDestroy() {
        mReceiver.stopPlay();
    }
}
