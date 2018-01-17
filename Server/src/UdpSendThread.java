import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class UdpSendThread extends Thread {
   private InetAddress mInetAddress;
   private int mPort;
   private byte[] mSendData;

    UdpSendThread(byte[]sendData,InetAddress inetAddress,int port){
        mSendData=sendData;
        mInetAddress=inetAddress;
         mPort=port;
      }
    @Override
    public void run() {
        //super.run();
        try {
            DatagramSocket socket=new DatagramSocket();
            DatagramPacket packet=new DatagramPacket(mSendData,mSendData.length,mInetAddress,mPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
