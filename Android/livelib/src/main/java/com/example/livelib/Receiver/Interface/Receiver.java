package com.example.livelib.Receiver.Interface;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by UPC on 2018/1/18.
 */

public interface Receiver {

    void startPlay(String address) throws UnknownHostException, SocketException;

    void startPlay(String address, int port) throws UnknownHostException, SocketException;

    void stopPlay();

    void takePic();

}
