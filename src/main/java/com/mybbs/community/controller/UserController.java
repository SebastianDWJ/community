package com.mybbs.community.controller;

import com.mybbs.community.annotation.LoginRequired;
import com.mybbs.community.entity.Page;
import com.mybbs.community.entity.User;
import com.mybbs.community.service.FollowService;
import com.mybbs.community.service.LikeService;
import com.mybbs.community.service.UserService;
import com.mybbs.community.util.CommunityConstant;
import com.mybbs.community.util.CommunityUtil;
import com.mybbs.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    public String uploadPath;

    @Value("${community.path.domain}")
    public String domain;

    @Value("${server.servlet.context-path}")
    public String contextPath;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String upload(MultipartFile headImage, Model model) {
        if (headImage == null) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }
        String filename = headImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }

        //?????????????????????
        filename = CommunityUtil.generateUUID() + suffix;
        //????????????
        File dest = new File(uploadPath + '/' + filename);
        try {
            headImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
            throw new RuntimeException("????????????????????????????????????", e);
        }

        //?????????????????????url
        //http:localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headUrl);

        return "redirect:/index";
    }

    @GetMapping("/header/{filename}")
    public void getHeadImg(@PathVariable("filename") String filename, HttpServletResponse response) {
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        String location = uploadPath + '/' + filename;
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(location);
        ) {
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer,0, b);
            }
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("/changepassword")
    public String changePassword(String oldPassword, String newPassword, Model model){
        if(oldPassword==null){
            model.addAttribute("oldError","????????????????????????");
            return "/site/setting";
        }
        if(newPassword==null){
            model.addAttribute("newError","????????????????????????");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.changePassword(user.getId(), oldPassword, newPassword);
        if(map.containsKey("oldError")){
            model.addAttribute("oldError",map.get("oldError"));
            return "/site/setting";
        }
        if(map.containsKey("newError")){
            model.addAttribute("newError",map.get("newError"));
            return "/site/setting";
        }

        return "redirect:/index";
    }

    //????????????
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId")int userId,Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("?????????????????????");
        }
        //??????
        model.addAttribute("user",user);

        //????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //?????? ????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //???????????????
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }




}
