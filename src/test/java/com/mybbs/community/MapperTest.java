package com.mybbs.community;

import com.mybbs.community.dao.DiscussPostMapper;
import com.mybbs.community.dao.LoginTicketMapper;
import com.mybbs.community.dao.UserMapper;
import com.mybbs.community.entity.DiscussPost;
import com.mybbs.community.entity.LoginTicket;
import com.mybbs.community.entity.User;
import com.mybbs.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    UserMapper userMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    LoginTicketMapper loginTicketMapper;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(138);
        System.out.println(user);

        User liubei = userMapper.selectByName("liubei");
        System.out.println(liubei);

        User user1 = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user1);
    }
    @Test
    public void testInsertUser(){
        User user = new User();
        user.setEmail("@@@@@");
        user.setSalt("2121");

        int i = userMapper.insertUser(user);
        System.out.println(i);

    }
    @Test
    public void testUpdateUser(){
        int i = userMapper.updateStatus(150, 1);
        System.out.println(i);
        int sasasa = userMapper.updateHeader(150, "sasasa");
        System.out.println(sasasa);
        int i1 = userMapper.updatePassword(150, "123455");
        System.out.println(i1);
    }
    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10,0);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }


    @Test
    public void testInertLoginMapper(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setId(1);
        loginTicket.setUserId(123);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60));
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(loginTicketMapper.selectByTicket(loginTicket.getTicket()));
        loginTicketMapper.updateStatus(loginTicket.getTicket(),1);
    }
}
