package com.hhf.utils;

import lombok.Data;

/**
  * @author hhf-050069
  * @date 2020-9-17 11:30:40
 */
@Data
public class HttpClientResult {
    private final int code;
    private final String content;

    public HttpClientResult(int code) {
        this(code, null);
    }

    public HttpClientResult(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public boolean isSuccess() {
        return 200 == code;
    }
}
