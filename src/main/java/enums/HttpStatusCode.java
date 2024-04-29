package enums;

import constants.StringConstants;
import lombok.Getter;

public enum HttpStatusCode {

    // 枚举类HttpStatusCode的实例列表
    OK("200", "OK"),
    NOT_FOUND("404", "Not Found"),
    CREATED("201","Created");

    @Getter  // 给statusCode一个getter方法
    public final String statusCode;

    public final String statusText;


    HttpStatusCode(String statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public String getText() {
        return statusCode + StringConstants.SPACE + statusText;
    }


}