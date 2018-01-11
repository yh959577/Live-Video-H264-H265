package com.example.livelib.Push.UdpSend;

import java.net.UnknownHostException;

/**
 * Created by Hamik Young on 2018/1/5.
 */

public interface VideoSender {
    void initial(String pushAddress) throws UnknownHostException;
    void initial(String ip, int port) throws UnknownHostException;
    void startSendVideoData();
    void stop();
}
