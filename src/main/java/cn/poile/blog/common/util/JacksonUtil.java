package cn.poile.blog.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * @author: yaohw
 * @create: 2019-10-25 16:41
 **/
public class JacksonUtil {

    private  static JacksonUtil JacksonUtil;
    private ObjectMapper objectMapper;

    private JacksonUtil (){}
    public static JacksonUtil me() {
        if (JacksonUtil == null) {
            synchronized (JacksonUtil.class) {
                if (JacksonUtil == null) {
                    JacksonUtil = newInstance();
                }
            }
        }
        return JacksonUtil;
    }


    /**
     * new实例
     * @return com.longmap.szwtl.common.util.Jacksons
     */
    private static JacksonUtil newInstance(){
        JacksonUtil jacksons = new JacksonUtil();
        jacksons.objectMapper= new ObjectMapper();
        jacksons.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jacksons.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
        jacksons.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return jacksons;
    }



    /**
     * 对象转json
     * @param obj
     * @return java.lang.String
     */
    public String readAsString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("解析对象错误",e);
        }
    }

    /**
     * json转对象
     * @param clazz
     * @param json
     * @return T
     */
    public <T> T parseString(Class<T> clazz, String json) {
        try {
            return    objectMapper.readerFor(clazz).readValue(json);
        } catch (Exception e) {
            throw new RuntimeException("解析对象错误",e);
        }
    }


    /**
     * json 转 List<T>
     * @param jsonString
     * @param clazz
     * @return java.util.List<T>
     */
    public  <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(
                List.class,clazz);
        List<T> list;
        try {
            list = objectMapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new RuntimeException("解析对象错误",e);
        }
        return list;
    }
}
