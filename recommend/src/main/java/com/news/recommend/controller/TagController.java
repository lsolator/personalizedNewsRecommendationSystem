package com.news.recommend.controller;

import com.news.recommend.entity.Tag;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.service.NewService;
import com.news.recommend.service.TagService;
import com.news.recommend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class TagController {
    @Resource
    private TagService tagService;

    @Resource
    private NewService newService;

    @Resource
    private UserService userService;

    @Resource
    private HttpSession session;

    @Value("${startDate}")
    private String startDate;

    @Value("${endDate}")
    private String endDate;

    @Value("${latestNum}")
    private String latestNum;

    //话题页面
    @GetMapping(value = {"/topic/{tagName}","/topic/"})
    public String topicNews(@PathVariable(value = "tagName", required = false) String tagName,
                            @RequestParam(value = "p", defaultValue = "1") int p, ModelMap modelMap) {
        List<ShowNews> latestNews = newService.LatestNews(Integer.valueOf(latestNum));
        modelMap.addAttribute("latestNews", latestNews);

        Pageable pageable = PageRequest.of(p - 1, 10);
        Map<String, Object> map = null;
        if (tagName == null) {
            List<Tag> tags = tagService.HotTopicList(startDate, endDate);
            map = tagService.HotTopicNewsList(startDate, endDate, pageable);
            modelMap.addAttribute("topics", tags);
        } else {
            map = tagService.TopicNewsList(tagName, 2, pageable);
        }
        List<ShowNews> news = (List<ShowNews>) map.get("list");
        modelMap.addAttribute("tagName", tagName);
        modelMap.addAttribute("news", news);
        modelMap.addAttribute("total", map.get("total"));
        modelMap.addAttribute("p", p);
        String uid = (String) session.getAttribute("uid");
        if (uid != null) {
            Integer num = userService.IsAttentionTag(uid, tagName);
            if (num != 0) {
                modelMap.addAttribute("isAttention", "1");
            }
        }
        return "topics";
    }


    //主题页面
    @GetMapping(value = {"/category/{tagName}","/category/"})
    public String category(@PathVariable(value = "tagName", required = false) String tagName,
                           @RequestParam(value = "p", defaultValue = "1") int p, ModelMap modelMap) {
        List<ShowNews> latestNews = newService.LatestNews(Integer.valueOf(latestNum));
        modelMap.addAttribute("latestNews", latestNews);
        Pageable pageable = PageRequest.of(p - 1, 10);
        List<Tag> tags = tagService.getTagList();
        if (tagName == null) {
            tagName = tags.get(0).getTagName();
        }
        Map<String, Object> map = tagService.TopicNewsList(tagName, 1, pageable);
        List<ShowNews> news = (List<ShowNews>) map.get("list");
        modelMap.addAttribute("tagName", tagName);
        modelMap.addAttribute("news", news);
        modelMap.addAttribute("total", map.get("total"));
        modelMap.addAttribute("p", p);
        modelMap.addAttribute("tags", tags);
        String uid = (String) session.getAttribute("uid");
        if (uid != null) {
            Integer num = userService.IsAttentionTag(uid, tagName);
            if (num != 0) {
                modelMap.addAttribute("isAttention", "1");
            }
        }
        return "category";
    }

}
