import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LiveServer {
  private ServerSocket mServerSocket;
  private int mPort=8613;

 LiveServer() throws IOException {
     listen(mPort);
 }

 private void listen(int port) throws IOException {
     mServerSocket=new ServerSocket(port);
     while (true){
       Socket client= mServerSocket.accept();
    if (!checkInetAddressIsExist(client)&&checkIsValidOpenRequest(client.getInputStream()))
        ClientList.addClientSocket(client);
    if (checkInetAddressIsExist(client)&&checkIsValidCloseRequest(client.getInputStream()))
        for (int i = 0; i <ClientList.getClientSize() ; i++) {
            if (ClientList.getClientSocket(i).getInetAddress()==client.getInetAddress())
                ClientList.getClientSocket(i).close();
                ClientList.removeClient(i);
    }
     }
 }

 private boolean checkIsValidOpenRequest(InputStream inputStream){
     byte[] command=new byte[10];
     try {
         inputStream.read(command);
     } catch (IOException e) {
         e.printStackTrace();
     }
      return command[0]=='O'&&command[1]=='L';
 }

 private boolean checkInetAddressIsExist(Socket clientSocket){
       if (ClientList.getClientSize()>0)
           for (int i = 0; i <ClientList.getClientSize() ; i++) {
               if (clientSocket.getInetAddress()==ClientList.getClientSocket(i).getInetAddress())
                   return true;
           }
           return false;
 }

 private boolean checkIsValidCloseRequest(InputStream inputStream){
     byte[] command=new byte[10];
     try {
         inputStream.read(command);
     } catch (IOException e) {
         e.printStackTrace();
     }
     return command[0]=='C'&&command[1]=='L';
 }
}
