import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServerThread extends Thread {
  static   InetAddress mRequestInetAddress=null;
    static int mRequestPort=0;
    static boolean isRequest;
    static boolean isStop;
    private int mReceivePort=8613;
    private ServerSocket mReceiveSocket;


    TcpServerThread() {
    mReceiveSocket.a
    }

  static   InetAddress getRequestInetAddress(){
        return mRequestInetAddress;
    }
    static int getRequestPort(){
        return mRequestPort;
    }
    static boolean IsReceiveRequest(){
        return (mRequestInetAddress!=null)&&(mRequestPort!=0);
    }

}
