package model;

import enums.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static constants.StringConstants.CRLF;
import static constants.StringConstants.SPACE;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponse {
    @Builder.Default  // 使用HttpResponse.builder().xxx().xxx().build();的时候不传递version的值的时候就用默认值
    private String version = "HTTP/1.1";
    private HttpStatusCode httpStatusCode;
    private List<HttpHeader> headers;
    private String body;

    public String getText(){
        StringBuilder sb = new StringBuilder();
        sb.append(version);
        sb.append(SPACE);
        sb.append(httpStatusCode.getText());
        sb.append(CRLF);  // 响应行结束
        // 下面几行是响应头
        if(headers!=null) {
            for (HttpHeader header : headers) {
                sb.append(header.getText());
                sb.append(CRLF);
            }
        }
        sb.append(CRLF);  // 响应头和响应体之间空一行
        // 下面是响应体
        if(body!=null){
            sb.append(body);
        }

        return sb.toString();

    }
}