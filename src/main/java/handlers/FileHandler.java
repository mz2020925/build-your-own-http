package handlers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {

    public static String readFileToBody(String directory, String fileName) throws IOException {

        Path path = Paths.get(directory, fileName).toAbsolutePath();
        File file = new File(path.toString());
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s doesn't exist", path.toString()));
        }

        // return Files.readString(path);  // 这是JDK11才加入的方法

        // jdk8的字节流方法，当然如果只传输字符的话，使用字符流的话会更好
        // 字节缓冲输入流：BufferedInputStream(InputStream in)
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toString()));
        // 一次读取一个字节数组数据
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = bis.read(bytes)) != -1) {
            sb.append(new String(bytes, 0, len));
        }
        // 释放资源
        bis.close();

        return sb.toString();
    }


    public static void writeBodyToFile(String directory, String fileName, String content) throws IOException {

        Path path = Paths.get(directory, fileName).toAbsolutePath();  // 绝对路径
        File file = new File(path.toString());
        if (!file.exists())
            Files.createFile(path);
        // Files.writeString(path, content);  // 这是JDK11才加入的方法

        // 用jdk8的字节流的方法，当然如果只传输字符的话，使用字符流的话会更好
        // 字节缓冲输出流：BufferedOutputStream(OutputStream out)
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path.toString()));
        // 写数据
        if(content!=null) bos.write(content.getBytes());
        // 释放资源
        bos.close();
    }


}
