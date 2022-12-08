package com.mybbs.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
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
        return DigestUtils.md5DigestAsHex(generateUUID().getBytes(StandardCharsets.UTF_8));
    }
}
