package com.mybbs.community.controller;

import com.mybbs.community.dao.DiscussPostMapper;
import com.mybbs.community.entity.DiscussPost;
import com.mybbs.community.entity.Page;
import com.mybbs.community.service.DiscussPostService;
import com.mybbs.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，会实例化model和page，并把page注入到model里
        //所有在thymeleaf中直接可以访问page的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!=null){
            for (DiscussPost discussPost : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("discussPost",discussPost);
                map.put("user",userService.findUserById(discussPost.getUserId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);


        return "/index";
    }
}
