package com.mybbs.community.service;

import com.mybbs.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 把userId存到redis的set中
     */
    public void like(int userId,int entityType,int entityId,int entityUserId) {   //实体发送人entityUser
//        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        if (redisTemplate.opsForSet().isMember(key, userId)) {
//            //已赞,去掉
//            redisTemplate.opsForSet().remove(key, userId);
//        } else {
//            //去点赞
//            redisTemplate.opsForSet().add(key, userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey  = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
    }

    /**
     * 查询点赞个数
     */
    public long findEntityLikeCount(int entityType,int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 查看某人对某实体的点赞状态
     */

    public int findEntityLikeStatus(int userId, int entityType,int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(key, userId)? 1 : 0;
    }

    /**
     * 查询某个用户的赞
     */
    public int findUserLikeCount(int userId){
        String userLikeKey  = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count==null? 0 : count.intValue();
    }

}
