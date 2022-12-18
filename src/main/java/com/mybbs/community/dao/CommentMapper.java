package com.mybbs.community.dao;

import com.mybbs.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //要做分页
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //评论数量
    int selectCountByEntity(int entityType, int entityId);

    //增加评论
    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
