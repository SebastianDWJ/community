package com.mybbs.community.controller;

import com.mybbs.community.entity.DiscussPost;
import com.mybbs.community.entity.Page;
import com.mybbs.community.service.ElasticsearchService;
import com.mybbs.community.service.LikeService;
import com.mybbs.community.service.UserService;
import com.mybbs.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    ElasticsearchService elasticService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;

    ///search?keyword=xxx
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        Map<String, Object> searchResult = elasticService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());//从0开始
        List<DiscussPost> discussPostList = (List<DiscussPost>) searchResult.get("list");
        int count = (int) searchResult.get("count");
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(discussPosts!=null){
            for (DiscussPost discussPost : discussPostList) {
                HashMap<String, Object> map = new HashMap<>();
                //帖子
                map.put("discussPost",discussPost);
                //用户
                map.put("user",userService.findUserById(discussPost.getUserId()));
                //点赞
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);

        //分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(discussPostList!=null?count:0);
        return "/site/search";
    }
}
