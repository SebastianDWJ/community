package com.mybbs.community.dao;

import com.mybbs.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //offset起始页，limit显示多少个
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，必须用别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
