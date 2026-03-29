package org.csu.bj.controller;

import org.csu.bj.entity.Admin;
import org.csu.bj.service.AdminService;
import org.csu.bj.utils.JwtUtil;
import org.csu.bj.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.csu.bj.api.vo.Result;
import org.csu.bj.constant.CommonConstant;

import java.util.HashMap;

@Controller
public class AdminController {
    @Autowired
    AdminService adminService;
    @Autowired
    RedisUtil redisUtil = new RedisUtil();
    @CrossOrigin
    @PostMapping("/api/admin/login")
    @ResponseBody
    public Result adminLogin(@RequestBody Admin admin){
        String username = admin.getUsername();
        String password = admin.getPassword();
        Admin admin1 = adminService.login(username,password);
        if (admin1 == null){
            return Result.failed("用户名或密码错误！");
        }
        String token = JwtUtil.sign(admin.getUsername(),admin.getPassword(),admin1.getAuthority());
        //设置超时时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN+token,token);
        //redis设置30天缓存
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("username",username);
        resultMap.put("authority",admin1.getAuthority());
        resultMap.put("token",token);
//            resultMap.put("upstreamLoginModel",loginRepModel);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token,30 * 24 * 60 * 60);
        return Result.success(resultMap);
//            return Result.success(user,"登录成功");
    }

    @CrossOrigin
    @GetMapping("/api/admin/getAdmins")
    @ResponseBody
    public Result getAdmins(){
        return adminService.getAdmins();
    }
    @CrossOrigin
    @PostMapping("/api/admin/create")
    @ResponseBody
    public Result createAdmin(@RequestBody Admin admin){
        String username = admin.getUsername();
        String password = admin.getPassword();
        return adminService.register(username,password);
    }
    @CrossOrigin
    @PostMapping("/api/admin/updatePassword")
    @ResponseBody
    public Result updatePassword(@RequestBody Admin admin){
        return adminService.updatePassword(admin);
    }
}
