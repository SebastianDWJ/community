package com.mybbs.community.util;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static final String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //生成密码
    public static final String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(Map.Entry<String,Object> entry : map.entrySet()){
                json.put(entry.getKey(),entry.getValue());
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

//    public static void main(String[] args) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("name","liubei");
//        map.put("age",11);
//        System.out.println(getJSONString(0,"啊哈哈",map));
//    }
}
