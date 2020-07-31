package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private FollowService followService;
    //异步请求
    @RequestMapping(path = "/follow" ,method = RequestMethod.POST)
    public  String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已关注");
    }
    @RequestMapping(path = "/unfollow" ,method = RequestMethod.POST)
    public  String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }
    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user=userService.findUseById(userId);
        if (user==null){
            throw  new RuntimeException("该用户不存在");

        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int)followService.findFolloweeCount(userId,ENTITY_TYPE_USER));


        List<Map<String,Object>> userlist=followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if (userlist!=null){
            for (Map<String,Object> map:userlist){
                User u=(User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }

        }
        model.addAttribute("users",userlist);
        return "/site/followee";

    }

    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user=userService.findUseById(userId);
        if (user==null){
            throw  new RuntimeException("该用户不存在");

        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER,userId));


        List<Map<String,Object>> userlist=followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if (userlist!=null){
            for (Map<String,Object> map:userlist){
                User u=(User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }

        }
        model.addAttribute("users",userlist);
        return "/site/follower";

    }


    private boolean hasFollowed(int userId){
        if (hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }

}
