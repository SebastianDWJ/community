package com.mybbs.community.dao;

import com.mybbs.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //offset起始页，limit显示多少个
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，必须用别名
    int selectDiscussPostRows(@Param("userId") int userId);

}
