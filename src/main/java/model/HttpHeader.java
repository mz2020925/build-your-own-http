package model;

import constants.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpHeader {
    private String key;
    private List<String> values;


    public String getText() {
        return key + StringConstants.COLON + String.join(";", values);  // key: value1;value2;value3
    }
}