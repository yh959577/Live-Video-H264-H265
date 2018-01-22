package com.example.livelib.Receiver.Interface;

import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Hamik Young on 2018/1/22.
 */

public interface UdpReceiver {

  void initial(InetAddress address, int port) throws SocketException;
  void startReceive();
  void stopReceive();

}
