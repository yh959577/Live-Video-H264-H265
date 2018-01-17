import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpServerThread extends Thread {
    DatagramSocket mReceiveSocket;
    DatagramPacket mReceivePacket;
    int mReceivePort = 8612;
    byte[] mRecData;
    byte[] mSendData;

    UdpServerThread() {

    }

    @Override
    public void run() {
        //super.run();
        initialReceive();
        while (true){
            try {
                mReceiveSocket.receive(mReceivePacket);
                System.out.println("receive data len=="+mReceivePacket.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSendData = new byte[mReceivePacket.getLength()];
            System.arraycopy(mReceivePacket.getData(), 0, mSendData, 0, mSendData.length);
            if (ClientList.getClientSize() > 0) {
                for (int i = 0; i < ClientList.getClientSize(); i++) {
                    new UdpSendThread(mSendData,ClientList.getClientInetAddress(i),ClientList.getClientPort(i)).start();
                }
            }

        }
    }

    private void initialReceive() {
        mRecData = new byte[400];
        mReceivePacket = new DatagramPacket(mRecData, 0, mRecData.length);
        try {
            mReceiveSocket = new DatagramSocket(mReceivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }
}
