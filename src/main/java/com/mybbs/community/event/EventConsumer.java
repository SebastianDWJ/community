package com.mybbs.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.mybbs.community.entity.DiscussPost;
import com.mybbs.community.entity.Event;
import com.mybbs.community.entity.Message;
import com.mybbs.community.service.DiscussPostService;
import com.mybbs.community.service.ElasticsearchService;
import com.mybbs.community.service.MessageService;
import com.mybbs.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    MessageService messageService;
    @Autowired
    ElasticsearchService elasticService;
    @Autowired
    DiscussPostService discussPostService;
    @Value("${wk.image.command}")
    private String wkImageCmd;
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);//转为对象
        if (event == null) {
            logger.error("消息的格式错误！");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);//系统id=1
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        //message的content  把传过来的event放进去 需要在前端显示一些信息
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : content.entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));


        messageService.addMessage(message);
    }


    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);//转为对象
        if (event == null) {
            logger.error("消息的格式错误！");
            return;
        }

        //处理事件
        //把帖子存到es中
        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        elasticService.saveDiscussPost(discussPost);
    }

    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);//转为对象
        if (event == null) {
            logger.error("消息的格式错误！");
            return;
        }

        //处理事件
        elasticService.deleteDiscussPost(event.getEntityId());
    }

    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);//转为对象
        if (event == null) {
            logger.error("消息的格式错误！");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCmd + " --quality 75 " + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.error("生成长图失败："+cmd);
        } catch (IOException e) {
            logger.error("生成长图失败："+e.getMessage());
        }
    }
}
