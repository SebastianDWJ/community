package com.mybbs.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private String REPLACEMENT = "***";

    public TrieNode rootNode = new TrieNode();

    //构建前缀树
    @PostConstruct
    private void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null) {
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败："+e.getMessage());
        }
    }

    //将一个敏感词加到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            Character c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                //加到前缀树中
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);

            }
            //指向子节点
            tempNode = subNode;
            //加标志
            if(i==keyword.length()-1) tempNode.setWordEnd(true);
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return  过滤后文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)) return null;

        //指针1
        TrieNode node = rootNode;
        //指针2
        int start = 0;
        //指针3
        int position = 0;
        StringBuilder sb = new StringBuilder();

        while(start<text.length()){
            if(position<text.length()){
                Character c = text.charAt(position);
                //过滤特殊字符
                if(isSymbol(c)){
                    if(node==rootNode){
                        start++;
                        sb.append(c);
                    }
                    ++position;
                    continue;
                }

                //检查下级节点
                node = node.getSubNode(c);
                if(node==null){
                    //以begin开始的不是敏感词
                    sb.append(text.charAt(start));
                    //前进
                    position = ++start;
                    node = rootNode;
                }else if(node.isWordEnd()){//是敏感词
                    sb.append(REPLACEMENT);
                    //前进
                    start = ++position;
                    node = rootNode;
                }else {//还没检测完
                    position++;
                }
            }else{//position越界
                sb.append(text.charAt(start));
                //前进
                position = ++start;
                node = rootNode;
            }
        }
        return sb.toString();
    }


    //判断是否是符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF是东亚字符范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);
    }


    private class TrieNode {
        private boolean isWordEnd = false;
        private Map<Character, TrieNode> subNodes = new HashMap<>();



        public boolean isWordEnd() {
            return isWordEnd;
        }

        public void setWordEnd(boolean wordEnd) {
            isWordEnd = wordEnd;
        }

        public TrieNode addSubNode(Character c, TrieNode node) {
            return subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
