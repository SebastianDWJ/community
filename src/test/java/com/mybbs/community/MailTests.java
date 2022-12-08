package com.mybbs.community;

import com.mybbs.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {
    @Autowired
    MailClient mailClient;
    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void testMail(){
        mailClient.sendMail("593267463@qq.com","这是一个主题","我是content内容！");
    }
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","sunday");
        String content = templateEngine.process("/mail/demo1", context);
        System.out.println(content);
        mailClient.sendMail("593267463@qq.com","这也是一个主题",content);
    }
}
