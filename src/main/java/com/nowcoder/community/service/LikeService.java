package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    //点赞和个人主页赞的个数需要保持事务
     //entityUserId 为某个帖子的发表者
      public void like(int userId,int entityType,int entityId,int entityUserId){
         redisTemplate.execute(new SessionCallback() {
             @Override
             public Object execute(RedisOperations operations) throws DataAccessException {
                 String entityLikeKey =RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                 String userLikeKEY =RedisKeyUtil.getUserLikeKey(entityUserId);

                 boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);

                 if (isMember){   //如果已经点赞了，再点击则取消点赞，同时，这个用户的个人主页的赞数减去1，下面的同理
                     operations.opsForSet().remove(entityLikeKey,userId);
                     operations.opsForValue().decrement(userLikeKEY);
                 }else {
                     operations.opsForSet().add(entityLikeKey,userId);
                     operations.opsForValue().increment(userLikeKEY);
                 }
                 return null;
             }
         });
      }

      //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId){
          String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
          return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId ){
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1:0;
    }
    //查询某个用户获得的赞的数量
    public int findUserLikeKey(int userId){
        String userLikeKEY =RedisKeyUtil.getUserLikeKey(userId);
        Integer count=(Integer) redisTemplate.opsForValue().get(userLikeKEY);
        return count == null ? 0 :count.intValue();
    }
}
