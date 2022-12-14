package com.mybbs.community.util;

import java.util.Date;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    int REMEMBERME_EXPIRED_SECONDS = 3600 * 24 * 30;

    //帖子
    int ENTITY_TYPE_POST =1;
    //评论
    int ENTITY_TYPE_COMMENT = 2;
    //用户
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";
    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH = "publish";
    /**
     * 主题：删帖
     */
    String TOPIC_DELETE = "delete";
    /**
     * 主题；分享
     */
    String TOPIC_SHARE = "share";

    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";
    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";
    /**
     * 权限：管理员
     */
    String AUTHORITY_MODERATOR = "moderator";


}
