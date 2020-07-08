package community;

import com.nowcoder.community.CommunityApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class Redis {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        String redisKey="test:count";

        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }
    @Test
    public  void  testHash(){
        String rediskey="test:user";
        redisTemplate.opsForHash().put(rediskey,"id",100);
        redisTemplate.opsForHash().put(rediskey,"username","zhangsan");
        System.out.println(redisTemplate.opsForHash().get(rediskey,"id"));
        System.out.println(redisTemplate.opsForHash().get(rediskey,"username"));

    }
    @Test
    public void testBoundOperations(){
        String rediskey="test:count";
        BoundValueOperations operations=redisTemplate.boundValueOps(rediskey);
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    //事务
    @Test
    public void Transaction(){
        Object obj=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String rediskey="test:tx";
                //启动事务
                operations.multi();

                operations.opsForSet().add(rediskey,"zhangsan");
                operations.opsForSet().add(rediskey,"Lisi");
                operations.opsForSet().add(rediskey,"wangu");
                System.out.println(operations.opsForSet().members(rediskey));
                //提交事务
                 return operations.exec();
            }
        });
        System.out.println(obj);
    }

}
