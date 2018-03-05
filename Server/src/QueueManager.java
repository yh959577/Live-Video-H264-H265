import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueManager {
   private static ArrayBlockingQueue<byte[]> udpCache=new ArrayBlockingQueue<>(1000);


   public static void addDataToQueue(byte[] data){
        if (udpCache.size()>900)
            udpCache.poll();
          udpCache.add(data);

   }

   public static byte[] takeDataFromCache(){
       return udpCache.poll();
   }
}
