package cn.poile.blog.common.util;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: yaohw
 * @create: 2020-05-20 14:22
 **/
@Log4j2
public class HttpClientUtil {

    /**
     * get请求
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String,String> params) {
        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nameValuePairs = null;
        if (params != null && params.size() != 0) {
            nameValuePairs  = new ArrayList<>(params.size());
            for(Map.Entry<String, String> entry:params.entrySet()) {
                NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                nameValuePairs.add(nameValuePair);
            }
        }
        try {
            String paramsStr = nameValuePairs == null ? "" : "?" + EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            String fullUrl = url + paramsStr;
            log.info("请求链接:" + fullUrl);
            // 创建HttpGet
            HttpGet httpGet = new HttpGet(fullUrl);
            // 设置请求的Header
            // 这里有个bug，gitee相关接口不设置这个会报403，不知道为啥
            httpGet.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000000).setConnectionRequestTimeout(3000000)
                    .setSocketTimeout(3000000).build();
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpGet);

            // 获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()){
                log.error("错误信息:" + result);
                throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"第三方接口请求错误");
            }
        }catch (Exception e) {
            log.error("请求异常:{0}", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"第三方接口请求错误");
        }
        return result;
    }

    /**
     * post 请求
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map<String,String> params) {
        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nameValuePairs = null;
        if (params != null && params.size() != 0) {
            nameValuePairs  = new ArrayList<>(params.size());
            for(Map.Entry<String, String> entry:params.entrySet()) {
                NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                nameValuePairs.add(nameValuePair);
            }
        }
        try {
            log.info("请求链接:" + url);
            // 创建httpPost
            HttpPost httpPost = new HttpPost(url);
            // 设置请求的Header
            // 这里有个bug，gitee相关接口不设置这个会报403，不知道为啥
            httpPost.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
            if (nameValuePairs != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000000).setConnectionRequestTimeout(3000000)
                    .setSocketTimeout(3000000).build();
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpPost);

            // 获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()){
                log.error("错误信息:" + result);
                throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"第三方接口请求错误");
            }
        }catch (Exception e) {
            log.error("请求异常:{0}", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"第三方接口请求错误");
        }
        return result;
    }


}
