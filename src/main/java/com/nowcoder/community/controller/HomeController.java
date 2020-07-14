package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//处理首页的方法
public class HomeController implements CommunityConstant {
     @Autowired
    private DiscussPostService discussPostService;

     @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
     @RequestMapping(path ="/index",method= RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
         //方法调用之前，SpringMVC会自动实例化Model和Page，并将Page注入Model
         //因此，在thymeleaf中可以直接访问Page对象中的数据。
         page.setRows(discussPostService.findDiscussPostRows(0));
         page.setPath("/index");

         //找出数据库中存的帖子,实现分页查询(userId=0 表示查询全部的帖子！)
         List<DiscussPost> list=discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
         List<Map<String,Object>> discussPosts=new ArrayList<>();
         if (list!=null){
             //遍历集合中查询出来的每一条帖子
             for (DiscussPost post: list){
                 Map<String,Object> map=new HashMap<>();
                 map.put("post",post);
                 //拿到帖子中的userId，通过这个userId查到发帖的人User，用于前端渲染。
                 User user=userService.findUseById(post.getUserId());
                 map.put("user",user);
                 //每一个map对应一个帖子，其中包括内容post和user，用于前端渲染。

                 //用于显示首页帖子的赞的个数
                 long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                 map.put("likeCount",likeCount);

                 discussPosts.add(map);
             }
         }
         //discussPosts为一个list集合，里面有N个map对应N个帖子，可用于前端的显示。
         model.addAttribute("discussPosts",discussPosts);
         return "/index";
     }
}
