package com.mybbs.community.controller;

import com.mybbs.community.entity.DiscussPost;
import com.mybbs.community.entity.User;
import com.mybbs.community.service.DiscussPostService;
import com.mybbs.community.util.CommunityUtil;
import com.mybbs.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    HostHolder hostHolder;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user==null){
            return CommunityUtil.getJSONString(403,"你还没有登录！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);


        //错误最后统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }
}
