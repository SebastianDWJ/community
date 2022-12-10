package com.mybbs.community.util;

import java.util.Date;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    int REMEMBERME_EXPIRED_SECONDS = 3600 * 24 * 30;
}
