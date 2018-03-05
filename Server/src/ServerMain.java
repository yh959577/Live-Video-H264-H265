import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerMain {


    public static void main(String[] args) {
        DatagramSocket socket= null;
        try {
            socket = new DatagramSocket(8013);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        UdpReceiveThread udpReceiveThread=new UdpReceiveThread(socket);

        udpReceiveThread.start();

        UdpSendThread udpSendThread=new UdpSendThread(socket,udpReceiveThread);
        udpSendThread.start();
    }
}
