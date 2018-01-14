import java.io.IOException;

public class LiveServerMain {

    public static void main(String[] args) {
        try {
            LiveServer mLiveServer=new LiveServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
