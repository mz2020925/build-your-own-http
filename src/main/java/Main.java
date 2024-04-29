import connection.ConnectionHandler;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    /*
    第2/8阶段：回复 200，即使用 200 OK 响应响应 HTTP 请求。
      您将需要：
        接受 TCP 连接
        从连接中读取数据（我们将在后面的阶段对其进行解析）
        响应方式HTTP/1.1 200 OK\r\n（末尾有两个 \r\n）
        HTTP/1.1 200 OK是 HTTP 状态行。
        \r\n，也称为 CRLF，是 HTTP 使用的行尾标记。
        第一个\r\n表示响应行的结束。
        第二个\r\n表示响应头部分的结束（在本例中为空）。
      暂时可以忽略从连接接收到的数据。我们将在后面的阶段对其进行解析。
      有关 HTTP 响应结构的更多详细信息，请查看 MDN 文档（https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages#http_responses）。

    第3/8阶段：下面是 HTTP 请求的内容：
      GET /index.html HTTP/1.1
      Host: localhost:4221
      User-Agent: curl/7.64.1

      其中：
      GET /index.html HTTP/1.1是起跑线。
      GET是 HTTP 方法。
      /index.html是路径。
      HTTP/1.1是 HTTP 版本。
      Host: localhost:4221和User-Agent: curl/7.64.1是 HTTP Headers。
      请注意，所有这些行都用 \r\n分隔，而不仅仅是 \n。
      在此阶段，我们将只专注于从请求中提取路径。
      如果路径为 /，则需要使用 200 OK 响应进行响应。否则，您需要使用“404 Not Found”响应进行响应。
     */
    public static void main123(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        // Uncomment this block to pass the first stage
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        /*
        Socket类的getInputStream方法与getOutputStream方法：
        1.客户端上的使用
        1.1 getInputStream方法可以得到一个输入流，客户端的Socket对象上的getInputStream方法得到输入流其实就是从服务器端发回的数据。
        1.2 getOutputStream方法得到的是一个输出流，客户端的Socket对象上的getOutputStream方法得到的输出流其实就是发送给服务器端的数据。

        2.服务器端上的使用
        2.1 getInputStream方法得到的是一个输入流，服务端的Socket对象上的getInputStream方法得到的输入流其实就是从客户端发送给服务器端的数据流。
        2.2 getOutputStream方法得到的是一个输出流，服务端的Socket对象上的getOutputStream方法得到的输出流其实就是发送给客户端的数据。
         */
        //
        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept(); // Wait for connection from client.
            System.out.println("accepted new connection");
            // 为了处理客户端的 HTTP 请求，我们创建一个 BufferedReader包装的InputStreamReader ，而 又从客户端的输入流中读取。此设置使我们能够以有效的方式从套接字连接中读取文本。
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // 然后，我们读取请求的第一行，这是包含 HTTP 方法、路径和版本的起始行。我们将此行打印到控制台以进行调试。
            String startLine = in.readLine();
            System.out.println(startLine);
            // 接下来，我们使用空格作为分隔符将起始线拆分为多个部分。path 是结果数组中的第二个元素，因此我们将其提取并存储在名为 path的变量中。
            String[] arr = startLine.split(" ");
            String path = arr[1];
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            // 根据路径判断是回复200 OK还是404 NOT FOUND
            String response = "HTTP/1.1 200 OK\r\n\r\n";
            if (!path.equals("/")) {
                response = "HTTP/1.1 404 NOT FOUND \r\n\r\n";
            }
            out.println(response);  // 用PrintWriter就不用手动转换成byte[]了，但是我记得原来都是手动转换成byte[]而且还是1024Byte的一段一段发

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /*
    第4/8阶段：
    测试人员将向您发送请求：GET /echo/<a-random-string>
    您的程序需要以 200 OK 响应进行响应。回应 内容类型应为 text/plain，并且应包含随机字符串 <a-random-string>作为正文。
    例如，以下是您可能会收到的请求：
        GET /echo/abc HTTP/1.1
        Host: localhost:4221
        User-Agent: curl/7.64.1
    以下是您需要发回的回复：
        HTTP/1.1 200 OK
        Content-Type: text/plain
        Content-Length: 3

        abc
     */
    // 第4/8阶段开始以及之后的，当然之前的123也可以通过
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4221)) {
            String directory = null;
            if (args.length >= 2) {  // 测试平台会传递两个参数进来 "--directory <directory>"
                String command = args[0];
                if (command.equalsIgnoreCase("--directory"))
                    directory = args[1];
            }

            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Wait for connection from client.  线程工作在阻塞模式
                System.out.println("接受一个新连接，下面将创建一个线程来处理这个HTTP连接");
                // Thread t = new Thread(new ConnectionHandler(clientSocket));
                Thread t = new Thread(new ConnectionHandler(clientSocket, directory));
                /**
                 * 从这里就可以看出这里用的是 同步阻塞的Java网络IO编程。而且使用的工具是Stream，后面会有更加先进的工具Channel、Netty框架。
                 *
                  */

                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    // @Test
    // public void server() {
    //     try (ServerSocket serverSocket = new ServerSocket(4221)) {
    //         serverSocket.setReuseAddress(true);
    //         while (true) {
    //             Socket clientSocket = serverSocket.accept();// Wait for connection from client.
    //             System.out.println("accepted new connection");
    //             Thread t = new Thread(new ConnectionHandler(clientSocket));
    //             t.start();
    //         }
    //     } catch (IOException e) {
    //         System.out.println("IOException: " + e.getMessage());
    //     }
    // }

    @Test
    public  void test(){

        System.out.println("sc"+23);
    }
}


