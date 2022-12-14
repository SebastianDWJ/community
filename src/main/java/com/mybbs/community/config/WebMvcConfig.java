package com.mybbs.community.config;

import com.mybbs.community.controller.interceptor.DataInterceptor;
import com.mybbs.community.controller.interceptor.LoginInterceptor;
import com.mybbs.community.controller.interceptor.LoginRequiredInterceptor;
import com.mybbs.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;
//    @Autowired
//    LoginRequredInterceptor loginRequiredInterceptor;
    @Autowired
    MessageInterceptor messageInterceptor;
    @Autowired
    DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*jpeg","/**/*.png");
//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*jpeg","/**/*.png");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*jpeg","/**/*.png");
        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*jpeg","/**/*.png");

    }


}
