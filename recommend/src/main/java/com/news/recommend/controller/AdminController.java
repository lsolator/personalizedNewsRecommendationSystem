package com.news.recommend.controller;

import com.google.gson.Gson;
import com.news.recommend.entity.News;
import com.news.recommend.entity.Tag;
import com.news.recommend.service.NewService;
import com.news.recommend.service.TagService;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Resource
    private NewService newService;

    @Resource
    private TagService tagService;

    @Value("${startDate}")
    private String startDate;

    @Value("${endDate}")
    private String endDate;

    @ResponseBody
    @PostMapping(value = "/admin/login")
    public String adminLogin(@RequestParam(value = "username") String username, @RequestParam(value = "password") String pwd) {
        if ("admin".equals(username) && "admin".equals(pwd)) {
            return new Gson().toJson("1");
        } else return new Gson().toJson("0");
    }

    @ResponseBody
    @PostMapping(value = "/admin/del")
    public String deleteNews(@RequestParam(value = "nid") String nid) {
        newService.DeleteNews(nid);
        return new Gson().toJson("1");
    }

    @ResponseBody
    @PostMapping(value = "/admin/add")
    public String addNews(@RequestBody Map<String,String> aNews) {
        String title = aNews.get("title");
        String content = aNews.get("content");
        String tagName = aNews.get("tagName");
        newService.SaveNews(title, content, tagName);
        return new Gson().toJson("1");
    }

    @ResponseBody
    @PostMapping(value = "/admin/update")
    public String updateNews(@RequestBody Map<String,String> aNews) {
        String nid = aNews.get("nid");
        String title = aNews.get("title");
        String content = aNews.get("content");
        newService.AlertNews(nid, title, content);
        return new Gson().toJson("1");
    }

    @ResponseBody
    @GetMapping(value = "/admin/news")
    public String returnNews(@RequestParam(value = "page") Integer page,
                             @RequestParam(value = "limit") Integer limit) {
        Pageable pageable = PageRequest.of(page-1, limit);
        Map<String, Object> maplist = newService.returnNews(pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("message", "success");
        map.put("count", maplist.get("count"));
        map.put("data", maplist.get("list"));
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @ResponseBody
    @GetMapping(value = "/api/tag")
    public String tagList() {
        List<Tag> list = tagService.getTagList();
        return new Gson().toJson(list);
    }

    @ResponseBody
    @PostMapping(value = "/api/news")
    public String oneNews(@RequestParam(value = "nid") String nid) {
        System.out.println(nid);
        News news = newService.getOnNews(nid);
        System.out.println("++++++++++++++");
        System.out.println(news);
        HashMap<String, String> map = new HashMap<>();
        map.put("nid", news.getNid());
        map.put("title", news.getTitle());
        map.put("content", news.getContent());
        return new Gson().toJson(map);
    }

}