package com.mybbs.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getValue(HttpServletRequest request, String name){

        if (request==null || name==null){
            throw new ArithmeticException("参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("ticket")){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
