package connection;

import enums.HttpStatusCode;
import handlers.FileHandler;
import model.HttpHeader;
import model.HttpRequest;
import model.HttpResponse;
import parser.HttpParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectionHandler implements Runnable {

    private Socket socket;

    private String directory;

    public ConnectionHandler(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public ConnectionHandler(Socket clientSocket, String directory) {
        this.socket = clientSocket;
        this.directory = directory;
    }


    @Override
    public void run() {  // 线程start就会执行run方法，run方法会调用response方法和writeResponse
        try {
            while (true) {
                InputStream in = socket.getInputStream();  // 从服务端的socket中获取《请求》中的数据，线程在这里会处于阻塞状态
                // if (in.available() == 0) {
                //     continue;
                // }
                HttpRequest httpRequest = HttpParser.parse(in);  // 解析请求数据
                // System.out.println(httpRequest.toString());  // 打印《请求》对象

                OutputStream out = socket.getOutputStream();
                HttpResponse response = response(httpRequest);  // 根据《请求》和题目要求，生成《响应》
                writeResponse(response, out);  // 返回《响应》response之前要把response转换字符串
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HttpResponse response(HttpRequest req) {
        String path = req.getPath();


        if (path.equalsIgnoreCase("/")) {  // 如果
            List<HttpHeader> headers = new ArrayList<>();  // 这是响应头
            headers.add(new HttpHeader("Content-Type", Collections.singletonList("text/plain")));
            headers.add(new HttpHeader("Content-Length", Collections.singletonList("0")));
            return HttpResponse.builder().httpStatusCode(HttpStatusCode.OK)
                    .headers(headers)
                    .build();
        } else if (path.startsWith("/echo/")) {
            // int idx = path.indexOf("/echo/");  // 返回子字符串"/echo/"在原字符串中从哪个索引开始
            String echoText = path.substring(6);

            List<HttpHeader> headers = new ArrayList<>();
            headers.add(new HttpHeader("Content-Type", Collections.singletonList("text/plain")));
            headers.add(new HttpHeader("Content-Length", Collections.singletonList(String.valueOf(echoText.length()))));

            return HttpResponse.builder()
                    .httpStatusCode(HttpStatusCode.OK)
                    .headers(headers)
                    .body(echoText)
                    .build();

        } else if (path.equalsIgnoreCase("/user-agent")) {
            String body = "";
            for (HttpHeader header : req.getHeaders()) {
                if (header.getKey().equals("User-Agent")) {
                    body = String.join("", header.getValues());
                }
            }
            List<HttpHeader> headers = new ArrayList<>();
            headers.add(new HttpHeader("Content-Type", Collections.singletonList("text/plain")));
            headers.add(new HttpHeader("Content-Length", Collections.singletonList(String.valueOf(body.length()))));
            return HttpResponse.builder()
                    .httpStatusCode(HttpStatusCode.OK)
                    .headers(headers)
                    .body(body)
                    .build();
        } else if (path.contains("/files/")) {
            if (req.getHttpMethod().equalsIgnoreCase("GET")) {
                return responseOfGetFile(req);  // 从服务端获取文件
            } else if (req.getHttpMethod().equalsIgnoreCase("POST")) {
                return responseOfUploadFile(req);  // 向服务器上传文件
            }
        }
        return HttpResponse.builder()
                .httpStatusCode(HttpStatusCode.NOT_FOUND)
                .build();


    }

    public void writeResponse(HttpResponse response, OutputStream out) throws IOException {

        String responseText = response.getText();  // ******最后在返回《响应》之前，这里把《响应》转换成了字符串*******

        System.out.println("线程" + Thread.currentThread().getId() + "管理的连接的Response 如下:\n'''\n" + responseText + "\n'''\n");
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);  // 把字符串用UTF-编码方式编码，然后再转换成字节数组

        out.write(responseBytes);
        out.flush();
    }

    public HttpResponse responseOfGetFile(HttpRequest req) {
        String path = req.getPath();
        String fileName = path.replaceFirst("^/files/", "");  // 提取文件名称
        try {
            String body = FileHandler.readFileToBody(directory, fileName);

            List<HttpHeader> headers = new ArrayList<>();
            headers.add(new HttpHeader("Content-Type", Collections.singletonList("application/octet-stream")));
            headers.add(new HttpHeader("Content-Length", Collections.singletonList(String.valueOf(body.length()))));

            return HttpResponse.builder()
                    .httpStatusCode(HttpStatusCode.OK)
                    .headers(headers)
                    .body(body)
                    .build();
        } catch (IOException e) {
            System.err.println("Unable to find file");
            return HttpResponse.builder()
                    .httpStatusCode(HttpStatusCode.NOT_FOUND)
                    .build();
        }
    }

    public HttpResponse responseOfUploadFile(HttpRequest req) {
        String path = req.getPath();
        String fileName = path.replaceFirst("^/files/", "");

        try {
            FileHandler.writeBodyToFile(directory, fileName, req.getBody());
            List<HttpHeader> headers = new ArrayList<>();
            headers.add(new HttpHeader("Content-Type", Collections.singletonList("text/plain")));
            headers.add(new HttpHeader("Content-Length", Collections.singletonList("0")));
            return HttpResponse.builder().httpStatusCode(HttpStatusCode.CREATED)
                    .headers(headers)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
