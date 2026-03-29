package org.csu.bj.controller;

import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Advice;
import org.csu.bj.entity.Notice;
import org.csu.bj.service.AdviceService;
import org.csu.bj.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdviceController {
    @Autowired
    AdviceService adviceService;
    @Autowired
    CommentService commentService;
    @GetMapping(value = "api/advice/all/nosolve")
    @CrossOrigin
    @ResponseBody
    public Result<List<Advice>> GetpostsAllNotSolve(){
        List<Advice> advice = adviceService.GetAdvicesAllNotSolve();
        return Result.success(advice);
    }
    @CrossOrigin
    @GetMapping(value = "api/advice")
    @ResponseBody
    public Result viewAdvices(@RequestParam( value = "id") int id){
        Advice advice = adviceService.getById(id);
        return Result.success(advice);
    }
    @CrossOrigin
    @DeleteMapping("api/advice")
    @ResponseBody
    public Result deleteOneAdvice(@RequestParam(value = "id") Integer id){
        int deletePost = adviceService.DeleteOne(id);
        if(deletePost == 1){
            return Result.success("","删除成功!");
        } else {
            return Result.failed("删除失败!");
        }
    }
    @CrossOrigin
    @PostMapping(value = "api/advice")
    @ResponseBody
    public Result addAdvice(@RequestBody Advice advice){
        try {
            System.out.println(advice);
            adviceService.AddAdvices(advice);
            return Result.success(advice);
        } catch (Exception e){
            e.printStackTrace();
            return Result.failed();
        }
    }
    @CrossOrigin
    @GetMapping("api/advice/byUser")
    @ResponseBody
//    根据id号查帖子
    public Result<List<Advice>> GetPersonalAdvices(@RequestParam(value = "username") String username){
        List<Advice> adviceList = adviceService.GetByUser(username);
        return Result.success(adviceList);
    }
    @CrossOrigin
    @GetMapping("api/advice/solve")
    @ResponseBody
    public Result adviceSolve(int id){
        Advice advice = adviceService.changeSolve(id);
        return Result.success(advice);
    }
    @CrossOrigin
    @GetMapping("api/advice/update")
    @ResponseBody
    public Result update(Advice advice){
        return adviceService.updateAdvice(advice);
    }
    @CrossOrigin
    @GetMapping("api/advice/all")
    @ResponseBody
    public Result all(){
        List<Advice> adviceList = adviceService.getAll();
        return Result.success(adviceList);
    }
}
