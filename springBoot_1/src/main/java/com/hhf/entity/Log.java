package com.hhf.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Document(collection = "my_log")
public class Log  implements Serializable {

    private String URL;//请求url
    private String HTTP_METHOD;//请求方式
    private String IP;//请求ip
    private Map<String,String> params;//请求参数
    private String RESPINSE;//响应
    private Date date;//时间

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getHTTP_METHOD() {
        return HTTP_METHOD;
    }

    public void setHTTP_METHOD(String HTTP_METHOD) {
        this.HTTP_METHOD = HTTP_METHOD;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getRESPINSE() {
        return RESPINSE;
    }

    public void setRESPINSE(String RESPINSE) {
        this.RESPINSE = RESPINSE;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
