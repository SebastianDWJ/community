package com.mybbs.community.controller;

import com.mybbs.community.entity.Event;
import com.mybbs.community.event.EventProducer;
import com.mybbs.community.util.CommunityConstant;
import com.mybbs.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

@Controller
public class ShareController implements CommunityConstant {
    private static Logger logger = LoggerFactory.getLogger(ShareController.class);
    @Value("${wk.image.command}")
    private String wkImageCmd;
    @Value("${wk.image.storage}")
    private String wkImageStorage;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    EventProducer eventProducer;

    @GetMapping("/share")
    @ResponseBody
    public String share(String htmlUrl) {
        //文件名
        String fileName = CommunityUtil.generateUUID();

        //异步生成成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("fileName", fileName)
                .setData("htmlUrl", htmlUrl)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);

        //返回访问路径
        HashMap<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);
        return CommunityUtil.getJSONString(0, null, map);
    }

    @GetMapping("/share/image/{fileName}")
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空！");
        }
        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b;
            while ((b= fis.read(buffer))!=1){
                os.write(buffer,0,b);
            }

        } catch (IOException e) {
            logger.error("生成长图失败："+e.getMessage());
        }
    }
}
