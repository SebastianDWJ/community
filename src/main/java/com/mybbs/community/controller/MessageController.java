package com.mybbs.community.controller;

import com.mybbs.community.entity.Message;
import com.mybbs.community.entity.Page;
import com.mybbs.community.entity.User;
import com.mybbs.community.service.MessageService;
import com.mybbs.community.service.UserService;
import com.mybbs.community.util.CommunityUtil;
import com.mybbs.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
//        Integer.valueOf("asd");
        User user = hostHolder.getUser();
        //分页
        page.setPath("/letter/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList!=null){
            for(Message conversation: conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",conversation);
                map.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),conversation.getConversationId()));
                int targetId = user.getId()==conversation.getFromId()? conversation.getToId():conversation.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
            model.addAttribute("conversations",conversations);
        }
        //总的未读消息数量
        model.addAttribute("letterUnreadCount",messageService.findLetterUnreadCount(user.getId(),null));

        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    private String getLetterDetail(@PathVariable("conversationId")String conversationId, Model model, Page page){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!=null){
            for(Message letter : letterList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter",letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));

                letters.add(map);
            }
            model.addAttribute("letters",letters);
        }
        //私信目标
        model.addAttribute("target",getLetterTarget(conversationId));

        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] s = conversationId.split("_");
        int id0 = Integer.parseInt(s[0]);
        int id1 = Integer.parseInt(s[1]);

        int targetId = hostHolder.getUser().getId()==id0? id1:id0;
        return userService.findUserById(targetId);
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList!=null){
            for (Message message : letterList) {
                if(hostHolder.getUser().getId()==message.getToId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }


    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName,String content){
//        Integer.valueOf("abc");
        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }
        Message message = new Message();
        message.setContent(content);
        message.setToId(target.getId());
        message.setFromId(hostHolder.getUser().getId());
        message.setCreateTime(new Date());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @PostMapping("/letter/delete")
    @ResponseBody
    public String deleteLetter(int id){
        messageService.deleteMessage(id);
        return CommunityUtil.getJSONString(0);
    }




}
