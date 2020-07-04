package com.hhf.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * HttpClient工具类
 *
 * @author hhf
 * @since 1.0.0
 */

@Slf4j
public final class HttpClientUtils {

    /**
     * 最大连接数
     */
    private static final int MAX_TOTAL_CONNECTIONS = 200;
    /**
     * 获取连接的最大等待时间
     */
    private static final int WAIT_TIMEOUT = 60 * 1000;
    /**
     * 每个路由最大连接数
     */
    private static final int MAX_ROUTE_CONNECTIONS = 50;
    /**
     * 连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 5 * 1000;

    /**
     * 读取超时时间
     */
    private static final int READ_TIMEOUT = 5 * 1000;

     /**
      * 每个HOST的最大连接数量(连接至目的主机的线程数)
      */
    private static final int MAX_CONN_PRE_HOST = 5;

    /**
     * 缺省编码格式
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /** 锁 */
    private static final Object SYNC_LOCK = new Object();

    /** httpClient实例 */
    private static Map<String, CloseableHttpClient> HTTP_CLIENT_MAP = new HashMap<String, CloseableHttpClient>();

    private HttpClientUtils(){}

    /**
     * 设置httpRequestBase
     */
    private static void config(final HttpRequestBase httpRequestBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(WAIT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(READ_TIMEOUT).build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 设置Header
     */
    @SuppressWarnings("all")
    private static void setHeader(final Map<String, String> headers, final HttpRequestBase httpRequestBase) {
        if(headers != null && headers.size() > 0){
            headers.forEach((k, v) -> {
                httpRequestBase.setHeader(k, v);
            });
        }
        config(httpRequestBase);
    }

    /**
     * 获取HttpClient对象
     * @param url url
     * @return CloseableHttpClient实例
     */
    
    @SuppressWarnings("all")
    public static CloseableHttpClient getHttpClient(final String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if(url.startsWith("https")){
            port = 443;
        }
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        CloseableHttpClient httpClient = HTTP_CLIENT_MAP.get(hostname);
        if (httpClient == null) {
            synchronized (SYNC_LOCK) {
                if (httpClient == null) {
                    httpClient = createHttpClient(hostname, port);
                    HTTP_CLIENT_MAP.put(hostname, httpClient);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     * @param hostname the hostname
     * @param port     the port
     * @return CloseableHttpClient httpclient
     */
    @SuppressWarnings("all")
    public static CloseableHttpClient createHttpClient(final String hostname, final int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        // 创建host
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), MAX_CONN_PRE_HOST);
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(final IOException exception, final int executionCount, final HttpContext context) {
                // 如果已经重试了3次，就放弃
                if (executionCount >= 3) {
                    return false;
                }
                // 如果服务器丢掉了连接，那么就重试
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                // 不要重试SSL握手异常
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                // 超时
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                // 目标服务器不可达
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                // 连接被拒绝
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                // SSL握手异常
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }

    /**
     * 设置post请求参数
     * @param httpost  the httpost
     * @param params   the params
     */
    private static void setPostParams(final HttpPost httpost, final Map<String, Object> params) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0) {
            return;
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_CHARSET));
        } catch (final UnsupportedEncodingException e) {
            throw e;
        }
    }

    /**
     * 发送post请求
     * @param url the url
     * @param httppost the httppost
     * @param params the params
     * @param reqeustBody the reqeustBody
     * @return String
     * @throws Exception  e
     */
    @SuppressWarnings("deprecation")
	private static String doPost(final String url, final HttpPost httppost,
            final String reqeustBody, final Map<String, Object> params) throws Exception {
        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        try {
            if (!StringUtils.isEmpty(reqeustBody)) {
                StringEntity sn = new StringEntity(reqeustBody, "utf-8");
                sn.setContentType("application/json");
                httppost.setEntity(sn);
            }
            response = getHttpClient(url).execute(httppost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, DEFAULT_CHARSET);
        } catch (final Exception e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httppost.releaseConnection();
                httppost.abort();
                getHttpClient(url).getConnectionManager().closeIdleConnections(0, TimeUnit.MICROSECONDS);
            } catch (final IOException e) {
                log.info("IOException",e);
            }
        }
    }

    /**
     * 普通的post请求，获取内容
     *
     * @param url the url
     * @param params the params
     * @param reqeustBody the reqeustBody
     * @return string
     * @throws Exception e
     */
    public static String post(final String url, final String reqeustBody,
            final Map<String, Object> params) throws Exception {
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        return doPost(url, httppost, reqeustBody, params);
    }

    /**
     * 模拟的post请求，获取内容
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param reqeustBody the reqeustBody
     * @return string
     * @throws Exception e
     */
    public static String browserPost(final String url, final Map<String, String> headers,
            final String reqeustBody, final Map<String, Object> params) throws Exception {
        HttpPost httppost = new HttpPost(url);
        setHeader(headers, httppost);
        return doPost(url, httppost, reqeustBody, params);
    }

    /**
     * 发送get请求
     * @param url the url
     * @return String
     * @throws IOException e
     */
    private static String doGet(final String url, final HttpGet httpget) throws IOException{
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httpget, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, DEFAULT_CHARSET);
        } catch (final IOException e) {
            throw e;
        } finally {
            try {
                if (response != null){
                    response.close();
                }
                httpget.releaseConnection();
                httpget.abort();
                getHttpClient(url).getConnectionManager().closeIdleConnections(0, TimeUnit.MICROSECONDS);
            } catch (final IOException e) {
              log.info("IOException",e);
            }
        }
    }

    /**
     * GET请求URL获取内容
     *
     * @param url the url
     * @return string
     * @throws IOException e
     */
    public static String get(final String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        config(httpget);
        return doGet(url, httpget);
    }

    /**
     * 模拟浏览器GET请求URL获取内容
     *
     * @param url the url
     * @param headers the headers
     * @return string
     * @throws IOException e
     */
    public static String browserGet(final String url, final Map<String, String> headers) throws IOException {
        HttpGet httpget = new HttpGet(url);
        setHeader(headers, httpget);
        return doGet(url, httpget);
    }

    /**
     * Decode Unicode
     *
     * @param theString the theString
     * @return string
     * @throws IOException e
     */
    @SuppressWarnings("all")
    public static String decodeUnicode(final String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
    public static Map<Object, Object> post(String jsonObj, String marsServer) throws IOException {
		HttpPost post = null;
		Map<Object, Object> resultMap = new HashMap<Object, Object>();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		post = new HttpPost(marsServer);
		// 设置超时时间
		RequestConfig requestConfig = RequestConfig.custom().build();
		post.setConfig(requestConfig);
		// 构造消息头
		post.setHeader("Content-type", "application/json; charset=utf-8");
		post.setHeader("Connection", "Close");
		if("http://172.16.37.98:9080/mars".equals(marsServer)){
			post.setHeader("User-Agent", "PostmanRuntime/7.15.0");
			post.setHeader("Accept", "*/*");
		}
		// 构建消息实体
		JSONObject jsobj1 = new JSONObject();
		jsobj1.put("prov", "");
		jsobj1.put("city", "");
		jsobj1.put("county", "");
		jsobj1.put("addr", jsonObj);

		StringEntity entity = new StringEntity(jsobj1.toString(), Charset.forName("UTF-8"));
		entity.setContentEncoding("UTF-8");
		// 发送Json格式的数据请求
		entity.setContentType("application/json");
		post.setEntity(entity);

		HttpResponse response = httpclient.execute(post);

		// 检验返回码
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			resultMap.put("data", "");
			resultMap.put("code", statusCode);
			resultMap.put("msg", "调用SDK程序通讯出错");
			return resultMap;
		} else {
			if (response.getEntity() != null) {
				String entityData = EntityUtils.toString(response.getEntity(), "UTF-8");
				if(StringUtils.isNotEmpty(entityData)){
					Map map = JSON.parseObject(entityData, Map.class);
					if(map!=null){
						Object data = map.get("data");
						if(data!=null){
							Map mapType = (Map)data;
							for (Object obj : mapType.keySet()) {
								resultMap.put(obj, mapType.get(obj));
							}
							return resultMap;
						}
					}
				}

			}
		}
		return null;
	}
    public static Map<Object, Object> post1(JSONObject jsonObj, String url) throws IOException {
		HttpPost post = null;
		Map<Object, Object> resultMap = new HashMap<>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		post = new HttpPost(url);
		// 设置超时时间
		RequestConfig requestConfig = RequestConfig.custom().build();
		post.setConfig(requestConfig);
		// 构造消息头
		post.setHeader("Content-type", "application/json; charset=utf-8");
		post.setHeader("Connection", "Close");
		post.setHeader("User-Agent", "PostmanRuntime/7.15.0");
		post.setHeader("Accept", "*/*");
//		post.setHeader("User-Agent", "PostmanRuntime/7.15.0");
//		post.setHeader("User-Agent", "PostmanRuntime/7.15.0");

//		post.setHeader("_user","gisservice");
//		post.setHeader("_token","rH9cK2eF1gC2nG4e");
		// 构建消息实体
		StringEntity entity = new StringEntity(jsonObj.toString(), Charset.forName("UTF-8"));
		entity.setContentEncoding("UTF-8");
		// 发送Json格式的数据请求
		entity.setContentType("application/json");
		post.setEntity(entity);

		HttpResponse response = httpclient.execute(post);

		// 检验返回码
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			resultMap.put("data", "");
			resultMap.put("code", statusCode);
			resultMap.put("msg", "调用SDK程序通讯出错");
			return resultMap;
		} else {
			if (response.getEntity() != null) {
				String entityData = EntityUtils.toString(response.getEntity(), "UTF-8");
				if(StringUtils.isNotEmpty(entityData)){
					Map map = JSON.parseObject(entityData, Map.class);
					if(map!=null){
						Object data = map.get("data");
						if(data!=null){
							Map mapType = (Map)data;
							for (Object obj : mapType.keySet()) {
								resultMap.put(obj, mapType.get(obj));
							}
							return resultMap;
						}
					}
				}

			}
		}
		return null;
	}
    public static void main(String[] args) {
    	/*Map<String, Object> params=new HashMap<String, Object>();
    	params.put("addr", "中国,陕西省,延安街道郝家湾建材市场阳光照明");
		try {
			Map<Object, Object> map=HttpClientUtils.post("青浦区徐泾镇华徐公路", "https://api-gateway.yimidida.com/mars-ms/mars?_user=gisservice&_token=rH9cK2eF1gC2nG4e");
			System.out.println(map.get("alProvince"));
			System.out.println(map.get("alCity"));
			System.out.println(map.get("alDistrict"));
			
			System.out.println(map.get("alTown"));
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
}
