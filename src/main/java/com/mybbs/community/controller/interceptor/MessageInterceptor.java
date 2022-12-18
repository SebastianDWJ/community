package com.mybbs.community.controller.interceptor;

import com.mybbs.community.entity.User;
import com.mybbs.community.service.MessageService;
import com.mybbs.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount",noticeUnreadCount+letterUnreadCount);
        }
    }
}
