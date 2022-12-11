package com.mybbs.community;

import com.mybbs.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTest {
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "赌博，哈哈哈阿斯顿，抢劫，!洗！钱!!,洗洗钱钱";
        text = sensitiveFilter.filter(text);
        System.out.println(text);


    }
}
