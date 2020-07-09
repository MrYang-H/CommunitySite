package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/dicuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add" ,method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
         //从hostHolder查询用户，只有登录的用户才能发帖
        User user =hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403,"你还没登录!");
        }

        DiscussPost post =new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
         //报错的情况，将来统一处理。
        return  CommunityUtil.getJSONString(0,"发布成功");
    }
     //帖子详情信息 （从首页进入）
    @RequestMapping(path = "/detail/{discussPostId}" ,method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        //查询帖子
      DiscussPost post =  discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user=userService.findUseById(post.getUserId());
        model.addAttribute("user",user);

        return "/site/discuss-detail";
    }

}
