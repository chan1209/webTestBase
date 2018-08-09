package webTestUtils;

//import org.apache.commons.lang.exception.ExceptionUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.testng.log4testng.Logger;
import webTestUtils.vo.HttpResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 * @author chenpei
 * @date 2018-04-09
 */
public class HttpUtil {
    private static final Logger logger = Logger.getLogger(HttpUtil.class);
    //最大http连接数
    private static final int MAX_CONNECTIONS = 50000;

    //同主机最大http连接数
    private static final int MAX_PER_ROUTE = 100;

    //核心线程处理数
    private static final int CORE_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 5;

    //核心线程处理数
    private static final int MAX_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 100;

    //最大从连接池获取连接的最大等待时间，单位毫秒
    private static final int MAX_CONNECTION_REQUEST_TIMEOUT = 5000;

    //tcp连接建立timeout，单位毫秒
    private static final int MAX_CONNECTION_TIMEOUT = 5000;

    //读取数据最大等待时间，单位毫秒
    private static final int MAX_SOCKET_READ_TIMEOUT = 30000;

    //http线程处理超时时间,单位秒
    private static final int THREAD_EXECUTE_TIMEOUT = 3;

    /**
     * 阻塞队列大小.
     */
    private static final int BLOCK_QUEUE_SIZE = 1000;

    private HttpClient httpClient;

    private FutureRequestExecutionService requestExecService;

    ResponseHandler<HttpResult> handler = null;

    private static HttpUtil httpUtil = new HttpUtil();

    private ThreadPoolExecutor threadPoolExecutor;

    private HttpUtil(){
        init();
    }

    public static HttpUtil getHttpUtil(){
        return httpUtil;
    }

    public void closeExecService(){
        try {
            this.requestExecService.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void setCorePoolSize(int corePoolSize) {
        threadPoolExecutor.setCorePoolSize(corePoolSize);
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
    }

    public void allowCoreThreadTimeOut(boolean value) {
        threadPoolExecutor.allowCoreThreadTimeOut(value);
    }

    public void init(){
        try{
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(MAX_CONNECTIONS);
            cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
            RequestConfig requestConfig= RequestConfig.custom().setSocketTimeout(MAX_SOCKET_READ_TIMEOUT).setConnectionRequestTimeout(MAX_CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(MAX_CONNECTION_TIMEOUT).build();

            httpClient = HttpClientBuilder.create().disableAutomaticRetries().setDefaultRequestConfig(requestConfig).setConnectionManager(cm).build();

            threadPoolExecutor = new ThreadPoolExecutor(CORE_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE, 10L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(BLOCK_QUEUE_SIZE));

            // 设置允许线程池核心线程空闲时回收线程.
            threadPoolExecutor.allowCoreThreadTimeOut(true);

            requestExecService = new FutureRequestExecutionService(httpClient, threadPoolExecutor);
            handler = new ResponseHandler<HttpResult>() {
                public HttpResult handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    HttpResult httpResult = new HttpResult();
                    httpResult.setResultCode(response.getStatusLine().getStatusCode());
                    //if (httpResult.getResultCode() == HttpStatus.SC_OK) {
                    httpResult.setResponseBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
                    //}
                    return httpResult;
                }
            };
        }catch(Exception e){
            throw new RuntimeException("http client util初始化异常",e);
        }
    }

    private HttpResult sendHttpPostRequestWithExecutors(String httpUrl, Map<String, String> params, Map<String, String> requestHead) throws RuntimeException{
        return this.sendHttpPostRequestWithExecutors( httpUrl,  params, requestHead, null);
    }

    private HttpResult sendHttpPostRequestWithExecutors(String httpUrl, Map<String, String> params, Map<String, String> requestHead,Integer timeoutSeconds) throws RuntimeException{
        HttpResult httpResult = new HttpResult();
        HttpPost method = new HttpPost(httpUrl);
        long beginTime = System.currentTimeMillis();
        try {
            if (requestHead != null && !requestHead.isEmpty()) {
                Set<Map.Entry<String, String>> heads = requestHead.entrySet();
                for (Map.Entry<String, String> head : heads) {
                    method.addHeader(head.getKey(), head.getValue());
                }
            }
            if (params != null && !params.isEmpty()) {
                Set<Map.Entry<String, String>> entries = params.entrySet();
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : entries) {
                    pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                method.setEntity(new UrlEncodedFormEntity(pairs,"UTF-8"));
            }
            HttpRequestFutureTask<HttpResult> futureTask=requestExecService.execute(method, HttpClientContext.create(), handler);
            httpResult = futureTask.get((null ==timeoutSeconds || 0==timeoutSeconds)? THREAD_EXECUTE_TIMEOUT:timeoutSeconds,TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e)+"["+httpUrl+"["+params);
            throw new RuntimeException(e);
        } finally {
            method.releaseConnection();
            httpResult.setExecuteTime(System.currentTimeMillis() - beginTime);
        }
        return httpResult;
    }


    private HttpResult sendHttpGetRequestWithExecutors(String httpUrl, Map<String, String> params, Map<String, String> requestHead) throws RuntimeException {
        return sendHttpGetRequestWithExecutors(httpUrl, params, requestHead,null);
    }

    private HttpResult sendHttpGetRequestWithExecutors(String httpUrl, Map<String, String> params, Map<String, String> requestHead,Integer timeoutSeconds) throws RuntimeException {

        HttpResult httpResult = new HttpResult();
        if (params != null && !params.isEmpty()) {
            if( -1 == httpUrl.indexOf("?") ){
                httpUrl+="?"+parseRequestQueryString(params);
            }else{
                httpUrl+="&"+parseRequestQueryString(params);
            }
        }
        HttpGet method = new HttpGet(httpUrl);

        long beginTime = System.currentTimeMillis();
        try {
            if (requestHead != null && !requestHead.isEmpty()) {
                Set<Map.Entry<String, String>> heads = requestHead.entrySet();
                for (Map.Entry<String, String> head : heads) {
                    method.addHeader(head.getKey(), head.getValue());
                }
            }
            HttpRequestFutureTask<HttpResult> futureTask=requestExecService.execute(method, (HttpContext) HttpClientContext.create(), handler);
            httpResult = futureTask.get((null ==timeoutSeconds || 0==timeoutSeconds)? THREAD_EXECUTE_TIMEOUT:timeoutSeconds,TimeUnit.SECONDS);
        }catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e)+"["+httpUrl+"["+params);
            throw new RuntimeException(e);
        } finally {
            method.releaseConnection();
            httpResult.setExecuteTime(System.currentTimeMillis() - beginTime);
        }
        return httpResult;
    }


    public HttpResult postDataWithExecutors(String httpUrl, Map<String, String> requestHead, String data,Integer timeoutSeconds) throws RuntimeException {
        HttpResult httpResult = new HttpResult();
        HttpPost method = new HttpPost(httpUrl);

        long beginTime = System.currentTimeMillis();
        try {
            if (requestHead != null && !requestHead.isEmpty()) {
                Set<Map.Entry<String, String>> heads = requestHead.entrySet();
                for (Map.Entry<String, String> head : heads) {
                    method.addHeader(head.getKey(), head.getValue());
                }
            }
            if (null != data && !"".equalsIgnoreCase(data)) {
                method.setEntity(EntityBuilder.create().setContentEncoding("UTF-8").
                        setContentType(ContentType.APPLICATION_JSON).setText(data).build());

            }
            HttpRequestFutureTask<HttpResult> futureTask=requestExecService.execute(method, (HttpContext) HttpClientContext.create(), handler);
            httpResult = futureTask.get((null ==timeoutSeconds || 0==timeoutSeconds)? THREAD_EXECUTE_TIMEOUT:timeoutSeconds,TimeUnit.SECONDS);
        }catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e)+"["+httpUrl);
            throw new RuntimeException(e);
        } finally {
            method.releaseConnection();
            httpResult.setExecuteTime(System.currentTimeMillis() - beginTime);
        }
        return httpResult;
    }

    public HttpResult postDataWithExecutors(String httpUrl, Map<String, String> requestHead, String data) throws IOException {
        return postDataWithExecutors(httpUrl,requestHead,data,null);
    }



    public HttpResult postFileWithExecutors(String httpUrl, Map<String, String> params, Map<String, String> requestHead, File file, String fileName, Integer timeoutSeconds) throws RuntimeException {
        HttpResult httpResult = new HttpResult();
        HttpPost method = new HttpPost(httpUrl);
        long beginTime = System.currentTimeMillis();
        try {
            if (requestHead != null && !requestHead.isEmpty()) {
                Set<Map.Entry<String, String>> heads = requestHead.entrySet();
                for (Map.Entry<String, String> head : heads) {
                    method.addHeader(head.getKey(), head.getValue());
                }
            }
            MultipartEntityBuilder multipartEntityBuilder=MultipartEntityBuilder.create();
            multipartEntityBuilder.addPart(fileName, new FileBody(file));
            if (params != null && !params.isEmpty()) {
                Set<Map.Entry<String, String>> entries = params.entrySet();
                for (Map.Entry<String, String> param : entries) {
                    multipartEntityBuilder.addTextBody(param.getKey(), param.getValue());
                }
            }
            method.setEntity(multipartEntityBuilder.build());
            HttpRequestFutureTask<HttpResult> futureTask=requestExecService.execute(method, (HttpContext) HttpClientContext.create(), handler);
            httpResult = futureTask.get((null ==timeoutSeconds || 0==timeoutSeconds)? THREAD_EXECUTE_TIMEOUT:timeoutSeconds,TimeUnit.SECONDS);
        }catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e)+"["+httpUrl+"["+params);
            throw new RuntimeException(e);
        } finally {
            method.releaseConnection();
            httpResult.setExecuteTime(System.currentTimeMillis() - beginTime);
        }
        return httpResult;
    }


    public HttpResult postFileWithExecutors(String httpUrl, Map<String, String> params,Map<String, String> requestHead,File file,String fileName) throws RuntimeException {
        return postFileWithExecutors(httpUrl, params, requestHead, file, fileName,null);
    }

    public HttpResult sendHttpRequest(String httpUrl, Map<String, String> params, String httpMethodType, Map<String, String> requestProperties) throws RuntimeException {
        if (httpMethodType.equalsIgnoreCase("GET")) {
            return this.sendHttpGetRequestWithExecutors(httpUrl, params, requestProperties);
        } else {
            return this.sendHttpPostRequestWithExecutors(httpUrl, params, requestProperties);
        }
    }

    public HttpResult sendHttpRequest(String httpUrl, Map<String, String> params, String httpMethodType, Map<String, String> requestProperties,Integer timeoutSeconds) throws RuntimeException {
        if (httpMethodType.equalsIgnoreCase("GET")) {
            return this.sendHttpGetRequestWithExecutors(httpUrl, params, requestProperties,timeoutSeconds);
        } else {
            return this.sendHttpPostRequestWithExecutors(httpUrl, params, requestProperties,timeoutSeconds);
        }
    }

    private String parseRequestQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        Set<Map.Entry<String, String>> entries = params.entrySet();
        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>(params.size());
        for (Map.Entry<String, String> entry : entries) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return URLEncodedUtils.format(pairs, "UTF-8");
    }

}