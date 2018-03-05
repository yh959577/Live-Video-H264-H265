import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class UdpSendThread extends Thread {
    private byte[] mSendData;
    private DatagramSocket mSocket;
    private SocketAddress mAddress = null;
    private UdpReceiveThread mReceiveThread;
    private FileOutputStream fileOutputStream;

    UdpSendThread(DatagramSocket datagramSockets, UdpReceiveThread receiveThread) {

        mSocket = datagramSockets;
        mSendData = new byte[800];

        mReceiveThread=receiveThread;
        try {

            fileOutputStream=new FileOutputStream(new File("e:/ServerSend264.h264"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //super.run();
        DatagramPacket packet = new DatagramPacket(mSendData, mSendData.length);
        int byteNum=0;
        while (true) {
            if (mReceiveThread.getAddress()==null)
                continue;
            if (mAddress==null) {
                mAddress = mReceiveThread.getAddress();
                packet.setSocketAddress(mAddress);
            }
            byte[] sendData = QueueManager.takeDataFromCache();
            if (sendData != null) {
                System.out.println("poll data");
                try {
                    fileOutputStream.write(sendData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                packet.setData(sendData);
                packet.setLength(sendData.length);
                try {
                    mSocket.send(packet);

//                    byteNum+=packet.getLength();
//                    if (byteNum>(5*213)){
//                        byteNum=0;
//                        try {
//                            Thread.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }

                    System.out.println("send Data");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (IOException  e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
