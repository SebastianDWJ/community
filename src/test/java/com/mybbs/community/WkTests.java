package com.mybbs.community;

import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


public class WkTests {
    public static void main(String[] args) {
        String cmd = "D:/developer/wkhtmltopdf/bin/wkhtmltoimage --quality 75 http://www.baidu.com D:/developer/workplace_idea/community/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);//并发的
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
