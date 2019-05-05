package com.news.recommend.controller;

import com.google.gson.Gson;
import com.news.recommend.dao.RWeightRepository;
import com.news.recommend.entity.RecommendedWeight;
import com.news.recommend.entity.Tag;
import com.news.recommend.entity.User;
import com.news.recommend.entity.result.CollectNews;
import com.news.recommend.entity.result.DataResponse;
import com.news.recommend.entity.result.ShowUser;
import com.news.recommend.service.TagService;
import com.news.recommend.service.UserService;
import com.news.util.IdGen;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private HttpSession session;

    @Resource
    private RWeightRepository rWeightRepository;

    @Resource
    private TagService tagService;

    @Value("${userM}")
    private String userM;

    @Value("${userR}")
    private String userR;

    @Value("${userH}")
    private String userH;

    @Value("${pageNum}")
    private String pageNum;

    //登陆页面
    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    public String loginPage(ModelMap modelMap) {
        List<Tag> tags = tagService.getTagList();
        modelMap.addAttribute("tags", tags);
        return "login";
    }

    //登陆验证
    @ResponseBody
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String login(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "password", required = false) String password,
                        @RequestParam(value = "captcha", required = false) String captcha,
                        @RequestParam(value = "rememberMe", required = false) String rememberMe) {
        System.out.println(username + "----" + password + "--------" + captcha + "------------" + rememberMe);
        User user = userService.findByPhoneNum(username);
        if (user == null || !password.equals(user.getPassWord())) {
            return new Gson().toJson(new DataResponse("0", "用户名或者密码错误！", null));
        }
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (!kaptcha.equals(captcha)) {
            return new Gson().toJson(new DataResponse("3", "验证码错误！", null));
        }
        session.setAttribute("uid", String.valueOf(user.getUid()));
        session.setAttribute("username", user.getUserName());
        session.setAttribute("phone", user.getPhoneNumber());
        if (user.getPrefWords() == null) {
            return new Gson().toJson(new DataResponse("2", "第一次登录，选择关注的主题", null));
        }
        return new Gson().toJson(new DataResponse("1", "成功", null));
    }

    //注册页面
    @RequestMapping(value = "/user/reg", method = RequestMethod.GET)
    public String regPage() {
        return "reg";
    }

    //注册验证
    @ResponseBody
    @RequestMapping(value = "/user/reg", method = RequestMethod.POST)
    public String register(@RequestParam(value = "username", required = false) String username,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam(value = "phoneNumber", required = false) String phone) {
        User user = userService.findByPhoneNum(phone);
        if (user != null) {
            return new Gson().toJson(new DataResponse("0", "该手机号/邮箱已注册！", null));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User user1 = new User(IdGen.get().nextId(), username, password, phone, sdf.format(new Date()));
        userService.SaveUser(user1);
        //生成基础推荐策略
        RecommendedWeight weight = new RecommendedWeight(String.valueOf(user1.getUid()), Double.valueOf(userM), Double.valueOf(userR), Double.valueOf(userH));
        rWeightRepository.save(weight);
        return new Gson().toJson(new DataResponse("1", "成功", null));
    }

    //登出
    @GetMapping(value = "/user/logout")
    public String userlogout() {
        session.removeAttribute("uid");
        session.removeAttribute("username");
        session.removeAttribute("phone");
        return "redirect:/user/login";
    }

    //用户主页操作
    @GetMapping(value = {"/userMessage-{tab}"})
    public String message(@PathVariable(value = "tab", required = false) String tab,
                          @RequestParam(value = "p", defaultValue = "1") int p,
                          ModelMap modelMap) {
        System.out.println(tab);
        String uuid = (String) session.getAttribute("uid");

        if (uuid == null) {
            return "redirect:/user/login";
        }

        //关注数
        Integer userNum = userService.AttentionUser(uuid);
        Integer attentionNum = userService.UserAttention(uuid);
        modelMap.addAttribute("userNum", userNum);
        modelMap.addAttribute("attentionNum", attentionNum);

        //收藏tab
        if ("collect".equals(tab)) {
            Pageable pageable = PageRequest.of(p - 1, Integer.valueOf(pageNum));
            Map<String, Object> collectNews = userService.UserCollectNews(uuid, pageable);
            List<CollectNews> list = (List<CollectNews>) collectNews.get("list");
            modelMap.addAttribute("p", p);
            if (list == null || list.size() == 0) {
                modelMap.addAttribute("total", 1);
            } else {
                modelMap.addAttribute("collectNews", list);
                modelMap.addAttribute("total", collectNews.get("total"));
            }
            return "usermessage-collect";
        }

        //历史log
        if ("readLog".equals(tab)) {
            Pageable pageable = PageRequest.of(p - 1, Integer.valueOf(pageNum));
            Map<String, Object> collectNews = userService.UserReadLogs(uuid, pageable);
            List<CollectNews> list = (List<CollectNews>) collectNews.get("list");
            modelMap.addAttribute("p", p);
            if (list == null || list.size() == 0) {
                modelMap.addAttribute("total", 1);
            } else {
                modelMap.addAttribute("readLog", list);
                modelMap.addAttribute("total", collectNews.get("total"));
            }
            return "userMessage-readLog";
        }

        //关注的人
        if ("attention".equals(tab)) {
            List<ShowUser> showUsers = userService.ShowAttentionUserList(uuid);
            if (showUsers.size() > 0) {
                modelMap.addAttribute("userList", showUsers);
            }
            return "userMessage-attention";
        }


        List<Tag> tags = userService.ShowAttentionTags(uuid);
        if (tags.size() > 0) {
            modelMap.addAttribute("tags", tags);
        }
        return "userMessage-topics";

    }

    //其他用户主页
    @GetMapping(value = "/userMessage/{userId}")
    public String otherMessage(@PathVariable(value = "userId", required = false) String userId,
                          @RequestParam(value = "p", defaultValue = "1") int p,
                          ModelMap modelMap) {
        String uuid = (String) session.getAttribute("uid");
        if (userId == null && uuid == null) {
            return "redirect:/user/login";
        }
        if (uuid.equals(userId)) {
            return message("topics", 1, modelMap);
        }

        //关注数
        Integer userNum = userService.AttentionUser(userId);
        Integer attentionNum = userService.UserAttention(userId);
        modelMap.addAttribute("userNum", userNum);
        modelMap.addAttribute("attentionNum", attentionNum);
        //动态
        Pageable pageable = PageRequest.of(p - 1, Integer.valueOf(pageNum));
        Map<String, Object> collectNews = userService.UserReadLogs(userId, pageable);
        List<CollectNews> list = (List<CollectNews>) collectNews.get("list");
        modelMap.addAttribute("p", p);
        if (list == null || list.size() == 0) {
            modelMap.addAttribute("total", 1);
        } else {
            modelMap.addAttribute("readLog", list);
            modelMap.addAttribute("total", collectNews.get("total"));
        }

        String check = userService.IsAttentionUser(uuid, userId);
        if (check != null) {
            modelMap.addAttribute("check", 1);
        }
        User user = userService.findUser(userId);
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("otherId", userId);
        return "userMessage";
    }


    //关注话题
    @ResponseBody
    @GetMapping("/user/topic")
    public String attendTopic(@RequestParam(value = "tagName") String tagName, @RequestParam(value = "isCheck", required = false) String isCheck) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) return "失败";
        if ("-1".equals(isCheck)) {
            userService.DeleteAttentionTopic(uid, tagName);
            return "成功取消关注";
        }
        userService.AttentionTopic(uid, tagName);
        return "关注成功";
    }

    //用户收藏或者取消收藏新闻
    @ResponseBody
    @GetMapping("/user/news")
    public String collectNews(@RequestParam(value = "nid") String nid, @RequestParam(value = "isCollect", required = false) Integer isCollect) {
        String uid = (String) session.getAttribute("uid");
        userService.AlertIsCollect(uid, nid, isCollect);
        return "成功";
    }

    //修改用户信息
    @ResponseBody
    @PostMapping(value = "/user/alert")
    public String alertUser(@RequestParam(value = "username", required = false) String username,
                            @RequestParam(value = "password", required = false) String password,
                            @RequestParam(value = "password2", required = false) String password2) {
        System.out.println(username + "-----" + password + "-----" + password2 + "-----");
        String phone = (String) session.getAttribute("phone");
        String name = (String) session.getAttribute("username");
        User user = userService.findByPhoneNum(phone);

        if (username != null && !name.equals(username)) {
            user.setUserName(username);
            session.setAttribute("username", username);
        } else {
            if (!"null".equals(password) && !"null".equals(password2)
                    && password.equals(user.getPassWord())) {
                user.setPassWord(password2);
            } else {
                return new Gson().toJson(new DataResponse("0", "请检查密码", null));
            }
        }
        userService.SaveUser(user);
        DataResponse dataResponse = new DataResponse("1", "修改成功", null);
        return new Gson().toJson(dataResponse);
    }

    //关注用户
    @ResponseBody
    @GetMapping(value = "/user/attention")
    public String attentionUser(@RequestParam(value = "uid") String uid,@RequestParam(value = "isCheck") String isCheck) {
        String selfid = (String) session.getAttribute("uid");
        if ("-1".equals(isCheck)) {
            userService.DeleteAttentionUser(selfid, uid);
        } else {
            userService.AttentionUser(selfid, uid);
        }
        return "成功";
    }

}
