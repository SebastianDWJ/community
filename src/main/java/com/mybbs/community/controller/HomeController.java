package com.mybbs.community.controller;

import com.mybbs.community.dao.DiscussPostMapper;
import com.mybbs.community.entity.DiscussPost;
import com.mybbs.community.entity.Page;
import com.mybbs.community.service.DiscussPostService;
import com.mybbs.community.service.LikeService;
import com.mybbs.community.service.UserService;
import com.mybbs.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(value = "orderMode", defaultValue = "0") int orderMode) {
        //方法调用前，会实例化model和page，并把page注入到model里
        //所有在thymeleaf中直接可以访问page的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("discussPost", discussPost);
                map.put("user", userService.findUserById(discussPost.getUserId()));

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);


        return "/index";
    }

    @GetMapping("/error")
    public String error() {
        return "/error/500";
    }

    //拒绝访问的提示页面
    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }
}
