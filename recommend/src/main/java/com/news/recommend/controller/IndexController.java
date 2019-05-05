package com.news.recommend.controller;

import com.news.recommend.dao.RWeightRepository;
import com.news.recommend.entity.RecommendedWeight;
import com.news.recommend.entity.result.RecommendNews;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.entity.result.TagAndNews;
import com.news.recommend.service.NewService;
import com.news.recommend.service.RecommendService;
import com.news.recommend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class IndexController {
    @Resource
    private UserService userService;

    @Resource
    private NewService newService;

    @Resource
    private RecommendService recommendService;

    @Resource
    private HttpSession session;

    @Resource
    private RWeightRepository weightRepository;

    @Value("${recommendNum}")
    private String recommendNum;

    @Value("${recommendWeight}")
    private String recommendWeight;

    @Value("${startDate}")
    private String startDate;

    @Value("${endDate}")
    private String endDate;

    @Value("${latestNum}")
    private String latestNum;
    /**
     * 首页
     *
     * @return
     */
    @GetMapping(value = {"/" , "/index",})
    public String index(ModelMap modelMap){
        String uid = (String) session.getAttribute("uid");
        List<ShowNews> latestNews = newService.LatestNews(Integer.valueOf(latestNum));
        modelMap.addAttribute("latestNews", latestNews);
        if (uid == null) {
            return "redirect:/index-hot";
        }
        HashMap<String, RecommendNews> map = (HashMap<String, RecommendNews>) session.getAttribute("map");
        //banList
        List<String> banList = (List<String>) session.getAttribute("banList");
        if (banList == null) {
            banList = new ArrayList<>();
            session.setAttribute("banList",banList);
        }
        List<RecommendNews> list = new ArrayList<>();
        if (map == null || map.keySet().size() < 10) {
            Optional<RecommendedWeight> optional = weightRepository.findByUid(uid);
            RecommendedWeight weight = optional.get();
            map = recommendService.RecommendNews(uid,weight);
        }
        Iterator<Map.Entry<String, RecommendNews>> iterator = map.entrySet().iterator();
        int i = 0;
        //不感兴趣是否存在这篇文章
        int flag = 0;
        while (iterator.hasNext() && i < 10) {
            Map.Entry<String, RecommendNews> entry = iterator.next();
            for (String key: banList
                 ) {
                if (entry.getValue().getNid().equals(key)) {
                    flag = 1;
                    break;
                }
            }
            if (flag != 1) {
                list.add(entry.getValue());
                i++;
            } else flag = 0;
            iterator.remove();
        }
        session.setAttribute("map",map);
        modelMap.addAttribute("news", list);
        return "index";
    }

    @GetMapping(value = "/index-hot")
    public String indexHot(@RequestParam(value = "p", defaultValue = "1") int p, ModelMap modelMap) {
        List<ShowNews> latestNews = newService.LatestNews(Integer.valueOf(latestNum));
        modelMap.addAttribute("latestNews", latestNews);
        Pageable pageable = PageRequest.of(p - 1, 10);
        Map<String, Object> map = newService.HotNews(startDate, endDate, pageable);
        List<ShowNews> news = (List<ShowNews>) map.get("list");
        modelMap.addAttribute("news", news);
        modelMap.addAttribute("total", map.get("total"));
        modelMap.addAttribute("p", p);
        return "index-hot";
    }

    @GetMapping(value = "/index-attention")
    public String indexAttention(@RequestParam(value = "p", defaultValue = "1") int p, ModelMap modelMap) {
        List<ShowNews> latestNews = newService.LatestNews(Integer.valueOf(latestNum));
        modelMap.addAttribute("latestNews", latestNews);

        String uid = (String) session.getAttribute("uid");
        String status = null;
        if (uid != null) {
            List<TagAndNews> list = userService.TopicRecommend(uid, (p - 1) * 10, 10);
            if (list == null || list.size()==0) {
                list = userService.TopicRecommend(uid, 0, 10);
            }
            modelMap.addAttribute("news", list);
            status = "1";
        }
        modelMap.addAttribute("status", status);
        modelMap.addAttribute("p", p);
        return "index-attention";
    }

    @ResponseBody
    @GetMapping(value = "/index-ban")
    public String indexBan(@RequestParam("nid") String nid, @RequestParam("recommendType") String recommendType) {
        System.out.println(nid+"----"+recommendType);
        List<String> banList = (List<String>) session.getAttribute("banList");
        banList.add(nid);
        session.setAttribute("banList", banList);
        //更新用户推荐方式的比重
        String temp[] = recommendType.split("-");
        HashSet<String> set = new HashSet<String>(Arrays.asList(temp));
        //(String) session.getAttribute("uid")
        Optional<RecommendedWeight> optional = weightRepository.findByUid("1103197065002876928");
        if (!optional.isPresent()) {
            return "false";
        }
        RecommendedWeight recommendedWeight = optional.get();
        if (set.contains("userM")) {
            recommendedWeight.setUserM(recommendedWeight.getUserM() - Double.valueOf(recommendWeight));
        }
        if (set.contains("userR")) {
            recommendedWeight.setUserR(recommendedWeight.getUserR() - Double.valueOf(recommendWeight));
        }
        if (set.contains("userH")) {
            recommendedWeight.setUserH(recommendedWeight.getUserH() - Double.valueOf(recommendWeight));
        }
        weightRepository.save(recommendedWeight);
        return "ok";
    }

    @GetMapping(value = "/recommend")
    public String recommendChange(@RequestParam("nid") String nid, @RequestParam("recommendType") String recommendType) {
        //更新用户推荐方式的比重
        String uid = (String) session.getAttribute("uid");
        String temp[] = recommendType.split("-");
        HashSet<String> set = new HashSet<String>(Arrays.asList(temp));
        //(String) session.getAttribute("uid")
        Optional<RecommendedWeight> optional = weightRepository.findByUid(uid);
        if (!optional.isPresent()) {
            return "false";
        }
        RecommendedWeight recommendedWeight = optional.get();
        if (set.contains("userM")) {
            recommendedWeight.setUserM(recommendedWeight.getUserM() + Double.valueOf(recommendWeight));
        }
        if (set.contains("userR")) {
            recommendedWeight.setUserR(recommendedWeight.getUserR() + Double.valueOf(recommendWeight));
        }
        if (set.contains("userH")) {
            recommendedWeight.setUserH(recommendedWeight.getUserH() + Double.valueOf(recommendWeight));
        }
        weightRepository.save(recommendedWeight);
        return "redirect:/news/"+nid;
    }
}
