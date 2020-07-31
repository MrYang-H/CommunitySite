package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController implements CommunityConstant {
    @Autowired
    private FollowService followService;
     @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
     @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
         User user =userService.findUseById(userId);
         if (user ==null){
             throw new RuntimeException("该用户不存在");
         }
           model.addAttribute("user",user);
         //点赞数量
         int likeCount =likeService.findUserLikeKey(userId);
         model.addAttribute("likeCount",likeCount);

         //关注数量
       long followeeCount  = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
         model.addAttribute("followeeCount",followeeCount);
         //粉丝数量
        long followerCount =  followService.findFollowerCount(ENTITY_TYPE_USER,userId);
         model.addAttribute("followerCount",followerCount);
         //是否关注
         boolean hasFollowed = false;
         if (hostHolder.getUser() != null){
             hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
         }
         model.addAttribute("hasFollowed",hasFollowed);
         return "/site/profile";
     }
}
