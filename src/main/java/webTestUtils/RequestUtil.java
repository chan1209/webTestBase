package webTestUtils;

import com.alibaba.fastjson.JSON;
import webTestUtils.vo.HttpResult;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by chenpei on 2018-04-09.
 */
public class RequestUtil {

    private final static int SUCCESS_CODE = 200;

    public  <T> T executePost(String url, Map<String,String> map, Class<T> clazz) throws Exception {
        HttpResult result = HttpUtil.getHttpUtil().sendHttpRequest(url,map,"POST",null);
        if (result.getResultCode() != SUCCESS_CODE){
            throw new Exception("request failed:"+result.getResultCode()+","+result.getResponseBody());
        }else {
            String respJson = result.getResponseBody();
            return JSON.parseObject(respJson, clazz);
        }
    }

    public <T> T executePostWithData(String url, Map<String,String> paramObject, String data, Class<T> clazz) throws Exception {
        HttpResult result = HttpUtil.getHttpUtil().postDataWithExecutors(getURL(url,paramObject),null,data);
        if (result.getResultCode() != SUCCESS_CODE){
            throw new Exception("request failed:"+result.getResultCode()+","+result.getResponseBody());
        }else {
            String respJson = result.getResponseBody();
            return JSON.parseObject(respJson, clazz);
        }
    }

    public <T> T executePostWithFile(String url, Map<String,String> map, Class<T> clazz, File file,String fileName) throws Exception {
        HttpResult result = HttpUtil.getHttpUtil().postFileWithExecutors(url,map,null,file,fileName);
        if (result.getResultCode() != SUCCESS_CODE){
            throw new Exception("request failed:"+result.getResultCode()+","+result.getResponseBody());
        }else {
            String respJson = result.getResponseBody();
            return JSON.parseObject(respJson, clazz);
        }
    }

    public <T> T executeGet(String url, Map<String,String> map, Class<T> clazz) throws Exception {
        HttpResult result = HttpUtil.getHttpUtil().sendHttpRequest(url,map,"GET",null);
        if (result.getResultCode() != SUCCESS_CODE){
            throw new Exception("request failed:"+result.getResultCode()+","+result.getResponseBody());
        }else {
            String respJson = result.getResponseBody();
            return JSON.parseObject(respJson, clazz);
        }
    }


    public String getURL(String url, Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder tmp = new StringBuilder();
        tmp.append(url).append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(entry.getValue()==null||entry.getValue().length()==0){
                continue;
            }
            tmp.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(),"utf-8")).append("&");
        }
        tmp.delete(tmp.length()-1,tmp.length());
        return tmp.toString();
    }



    public String getMD5SignURL(String url, Map<String, String> map, String signKey) {
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        String signURL = "";
        //将url和data的值拼接后传入
        StringBuilder base = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            // 值为空时不参考签名.
            if (value == null || value.length() == 0) {
                continue;
            }
            base.append(key).append("=").append(value).append("&");
        }
        if (base.length() > 1) {
            base.deleteCharAt(base.length() - 1);
        }
        String md5Encode = MD5Util.MD5Encode(base.toString() +"&key="+ signKey);
        signURL = url+ "&sign=" + md5Encode;
        System.out.println(signURL);
        return signURL;
    }

    /**
     * @param param 参数转换map并返回
     */
    public Map<String,String> paramConvertMap(String param){
        Map<String,String> map = new HashMap<String,String>();
        String[] params = param.split("&");
        for(int i = 0;i < params.length; i++){
            String[] keyValue = params[i].split("=");
            map.put(keyValue[0],keyValue[1]);
        }
        return map;
    }



}
