package com.mybbs.community.dao;

import com.mybbs.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询某人的会话列表(最新消息)
    List<Message> selectConversations(int userId,int offset,int limit);
    //查看会话总数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查看总死信数量
    int selectLetterCount(String conversationId);

    //查看未读数量
    int selectLetterUnreadCount(int userId, String conversationId);

    //发送私信
    int insertMessage(Message message);
    //修改消息状态
    int updateStatus(List<Integer> ids, int status);
}
