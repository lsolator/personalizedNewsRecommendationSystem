package com.news.recommend.controller;

import com.google.gson.Gson;
import com.news.recommend.entity.News;
import com.news.recommend.entity.Tag;
import com.news.recommend.entity.result.Comment;
import com.news.recommend.entity.result.DataResponse;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.service.NewService;
import com.news.recommend.service.TagService;
import com.news.recommend.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class NewsController {
    @Resource
    private NewService newService;

    @Resource
    private TagService tagService;

    @Resource
    private UserService userService;

    @Resource
    private HttpSession session;

    //    阅读文章
    @RequestMapping(value = "/news/{nid}", method = RequestMethod.GET)
    public String newsPage(@PathVariable(value = "nid", required = false) String nid, ModelMap modelMap) {
        String uid = (String) session.getAttribute("uid");
        if (uid != null) {
            if (newService.isCollect(uid, nid)) {
                modelMap.addAttribute("isCollect", 1);
            }
        }
        Integer size = 0;
        News news = newService.ReadNews(uid, nid);
        String category = tagService.NewsHotWord(nid, 1).get(0).getTagName();
        List<Tag> tags = tagService.NewsHotWord(nid, 2);
        List<ShowNews> newsList = newService.MostFamiliarNewsList(nid, 5);
        List<Comment> commentList = newService.CommentList(nid);
        if (commentList.size() > 0) {
            modelMap.addAttribute("commentList", commentList);
            size = commentList.size();
        }
        modelMap.addAttribute("news", news);
        modelMap.addAttribute("similarNewsList", newsList);
        modelMap.addAttribute("category", category);
        modelMap.addAttribute("tags", tags);
        modelMap.addAttribute("size", size);
        return "newsdetail";
    }

    //查询页面
    @GetMapping(value = "/search/")
    public String searchNews(@RequestParam(value = "q", required = false) String q,
                             @RequestParam(value = "p", defaultValue = "1") int p, ModelMap modelMap) {
        Pageable pageable = PageRequest.of(p - 1, 10);
        Map<String, Object> news = newService.FindNewsByKeyWord(q, pageable);
        List<ShowNews> list = (List<ShowNews>) news.get("list");
        modelMap.addAttribute("searchList", list);
        modelMap.addAttribute("q", q);
        modelMap.addAttribute("p", p);
        modelMap.addAttribute("total", news.get("total"));
        if (list == null || list.size() == 0) {
            modelMap.addAttribute("total", 1);
        }
        return "search";
    }

    @ResponseBody
    @PostMapping(value = "/news/comment")
    public String comment(@RequestParam(value = "comment") String comment, @RequestParam(value = "nid") String nid, ModelMap modelMap) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            return new Gson().toJson(new DataResponse("0", "用户没有登陆", null));
        }
        if (comment == null) {
            return new Gson().toJson(new DataResponse("2", "评论不能为空", null));
        }
        userService.AddNewsComment(uid, nid, comment);
        return new Gson().toJson(new DataResponse("1", "评论成功", null));
    }

}
