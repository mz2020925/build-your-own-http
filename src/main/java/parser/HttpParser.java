package parser;

import model.HttpHeader;
import model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HttpParser {

    public static HttpRequest parse(InputStream ins) {
        BufferedReader br = new BufferedReader(new InputStreamReader(ins));

        // Parse first Line
        String input;
        List<String> headerLines = new ArrayList<>();
        String startLine;
        try {
            startLine = br.readLine();  // 读取《请求》的第一行
            HttpRequest request = parseStartLine(startLine);  // 解析请求行

            while (!(input = br.readLine()).equalsIgnoreCase("")) {
                headerLines.add(input);  // 如果《请求》的第二行不是""，就说明下面至少还有《请求头》
            }  // 当代码跳出循环的时候，如果有请求体的话，br指向的位置是请求头与请求体之间的空行
            List<HttpHeader> headers = parseHeaders(headerLines);  // 解析请求头
            request.setHeaders(headers);  // 把请求头的key-values结构，合并到request

            br.readLine();  // 跳过空行

            StringBuilder sb = new StringBuilder();  // 下面解析请求体
            while (br.ready()) {
                int read = br.read();  // 读取一个字符返回ASCII码
                sb.append(Character.toChars(read));
            }
            String body= sb.toString();
            request.setBody(body);

            // br.close();  // 当我们把br（就是socket的getInputStream()）关闭时，socket似乎会自动关闭
            return request;
        } catch (IOException | HttpParseException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<HttpHeader> parseHeaders(List<String> headerLines) {

        List<HttpHeader> headers = new ArrayList<>();
        for (String headerLine : headerLines) {
            try {
                String[] items = headerLine.split(": ");
                String key = items[0];
                String values = items[1];
                // 从values.split(" ")拷贝一个副本再来创建这个list的，并且创建的这个list是不可修改的
                List<String> valuesList = Collections.unmodifiableList(Arrays.asList(values.split(" ")));

                HttpHeader header = new HttpHeader(key, valuesList);
                headers.add(header);


            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return headers;

    }


    public static HttpRequest parseStartLine(String startLine) throws HttpParseException {

        HttpRequest req = new HttpRequest();
        try {
            List<String> parts = Collections.unmodifiableList(Arrays.asList(startLine.split(" ")));
            req.setHttpMethod(parts.get(0));  // 请求行的方法
            req.setPath(parts.get(1));  // 请求行的路径
            req.setVersion(parts.get(2));  // 请求行的HTTP版本
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Unable to parse Start Line");
            throw new HttpParseException("Unable to parse Start Line");
        }
        return req;
    }

    public static class HttpParseException extends Exception {
        public HttpParseException(String message) {
            super(message);
        }
    }


}