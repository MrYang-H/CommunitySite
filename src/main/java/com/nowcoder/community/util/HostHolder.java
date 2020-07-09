package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;
//持有用户的信息，用于代替session对象，线程隔离
//以线程为key
@Component
public class HostHolder {
       private ThreadLocal<User> users=new ThreadLocal<>();

       public void setUsers(User user){
                 users.set(user);

       }

       public User getUser(){
           return users.get();
       }

       public void clear(){
           users.remove();
       }
}
