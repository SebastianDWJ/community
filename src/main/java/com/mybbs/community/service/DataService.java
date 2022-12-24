package com.mybbs.community.service;

import com.mybbs.community.util.HostHolder;
import com.mybbs.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    //指定ip计入
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(sdf.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }
    //统计指定日期范围
    public long calculateUV(Date start,Date end){
        if (start==null || end==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(sdf.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);
        }
        //合并后存到redis
        String redisKey = RedisKeyUtil.getUVKey(sdf.format(start),sdf.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //指定userId计入
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(sdf.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }
    //统计指定日期范围
    public long calculateDAU(Date start,Date end){
        if (start==null || end==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(sdf.format(calendar.getTime()));
            keyList.add(key.getBytes(StandardCharsets.UTF_8));
            calendar.add(Calendar.DATE,1);
        }
        //OR后存到redis

        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(sdf.format(start),sdf.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }

}
