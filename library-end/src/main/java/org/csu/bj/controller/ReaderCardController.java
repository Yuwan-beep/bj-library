package org.csu.bj.controller;

import org.csu.bj.api.vo.Result;
import org.csu.bj.constant.CommonConstant;
import org.csu.bj.entity.Admin;
import org.csu.bj.entity.ReaderCard;
import org.csu.bj.service.AdminService;
import org.csu.bj.service.ReaderCardService;
import org.csu.bj.utils.JwtUtil;
import org.csu.bj.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class ReaderCardController {
    @Autowired
    ReaderCardService readerCardService;
    @Autowired
    RedisUtil redisUtil = new RedisUtil();
    @CrossOrigin
    @PostMapping("/api/user/login")
    @ResponseBody
    public Result userLogin(@RequestBody ReaderCard readerCard){
        String username = readerCard.getUsername();
        String password = readerCard.getPassword();
        ReaderCard readerCard1 = readerCardService.login(username,password);
        if (readerCard1 == null){
            return Result.failed("用户名或密码错误！");
        }
        String token = JwtUtil.userSign(readerCard1.getUsername(),readerCard1.getPassword(),readerCard1.getBan());
        //设置超时时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN+token,token);
        //redis设置30天缓存
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("username",username);
        resultMap.put("ban",readerCard1.getBan());
        resultMap.put("token",token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token,30 * 24 * 60 * 60);
        return Result.success(resultMap);
    }
    @CrossOrigin
    @PostMapping("/api/user/register")
    @ResponseBody
    public Result userRegister(@RequestBody ReaderCard readerCard){
        String username = readerCard.getUsername();
        String password = readerCard.getPassword();
        String phone = readerCard.getPhone();
        int ans = readerCardService.register(username,password,phone);
        if(ans == 1){
            return Result.success("");
        } else if (ans == -1){
            return Result.failed("用户名重复!");
        } else if(ans == -2){
            return Result.failed("手机号重复注册");
        } else {
            return Result.failed("注册失败，请重试");
        }
    }
    @CrossOrigin
    @GetMapping("/api/user/getId")
    @ResponseBody
    public Result userId(@RequestParam String username){
        int id = readerCardService.getId(username);
        return Result.success(id);
    }
    @CrossOrigin
    @GetMapping("/api/user/getUsers")
    @ResponseBody
    public Result getUsers(){
        return readerCardService.getUsers();
    }
}
