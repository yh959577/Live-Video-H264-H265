import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class UdpReceiveThread extends Thread {
    private DatagramSocket mSocket;
    private DatagramPacket mPacket;
    private byte[] rcb;
    private SocketAddress mAddress = null;
    private FileOutputStream fileOutputStream;
    UdpReceiveThread(DatagramSocket socket) {
        mSocket = socket;
        rcb = new byte[800];
        mPacket = new DatagramPacket(rcb, rcb.length);
        try {
            fileOutputStream=new FileOutputStream(new File("e:/Server264.h264"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Start Receive!!!");
        while (true) {
            try {
                mSocket.receive(mPacket);
                if (rcb[0] == 'M' && rcb[1] == 'S' && rcb[2] == 'G') {
                    System.out.println("Receive udp heart"+" Ip==="+mPacket.getSocketAddress().toString());
                    if (mAddress == null)
                        mAddress = mPacket.getSocketAddress();
                } else {
                    byte[] sendData = new byte[mPacket.getLength()];
                    System.arraycopy(mPacket.getData(), 0, sendData, 0, mPacket.getLength());
                    QueueManager.addDataToQueue(sendData);
                    System.out.println("add data to queue");
                    fileOutputStream.write(sendData);
                }
                mPacket.setLength(rcb.length);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public SocketAddress getAddress() {
        return mAddress;
    }
}
