import java.io.IOException;

public class LiveServerMain {

    public static void main(String[] args) {
        try {
            TcpServerThread tcpServer=new TcpServerThread();
            tcpServer.start();
            UdpServerThread  udpServerThread=new UdpServerThread();
            udpServerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
