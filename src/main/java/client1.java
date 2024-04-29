import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class client1 {

    @Test
    public void testClient() {

        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 4221);
            socket.setReuseAddress(true);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String request = "GET /user-agent HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\n\r\n";
            for (int i = 0; i < 100; i++) {
                System.out.println("客户端发送数据-" + i);
                out.println(request);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("IOException: " + e.getMessage());
        }
        // finally {
        //     try {
        //         assert socket != null;
        //         socket.close();
        //     }catch (IOException e){
        //         System.out.println("IOException: " + e.getMessage());
        //     }catch (NullPointerException e){
        //         System.out.println("NullPointerException: " + e.getMessage());
        //     }
        // }
    }
}
