import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientList {
private static List<Socket> mClientSockets=new ArrayList<>();

 public static InetAddress getClientInetAddress(int index){
     return mClientSockets.get(index).getInetAddress();
 }
 public static Socket getClientSocket(int index){
     return mClientSockets.get(index);
 }

 public static void removeClient(int i){
     mClientSockets.remove(i);
 }

 public static void addClientSocket(Socket socket){
     mClientSockets.add(socket);
 }

 public static List getClientList(){
     return mClientSockets;
 }

 public static int getClientSize(){
     return mClientSockets.size();
 }

 public static int getClientPort(int i){
     return mClientSockets.get(i).getPort();
 }


}
