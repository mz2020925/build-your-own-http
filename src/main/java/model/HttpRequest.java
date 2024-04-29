package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest {
    // 请求行的三个部分
    String httpMethod;
    String path;
    String version;

    // 请求头的几行
    List<HttpHeader> headers;

    // 请求体
    String body;

}