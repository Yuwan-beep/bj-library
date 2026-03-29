package org.csu.bj.controller;

import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Advice;
import org.csu.bj.entity.Notice;
import org.csu.bj.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NoticeController {
    @Autowired
    NoticeService noticeService;
    @GetMapping(value = "api/notice/all")
    @CrossOrigin
    @ResponseBody
    public Result<List<Notice>> GetpostsAll(){
        List<Notice> notices = noticeService.GetNoticeAll();
        return Result.success(notices);
    }
    @CrossOrigin
    @GetMapping(value = "api/notice")
    @ResponseBody
    public Notice viewPosts(@RequestParam( value = "id") int id){
        return noticeService.getById(id);
    }
    @CrossOrigin
    @DeleteMapping("api/notice")
    @ResponseBody
    public Result deleteOnePost(@RequestParam(value = "id") Integer id){
        int deletePost = noticeService.DeleteOne(id);
        if(deletePost == 1){
            return Result.success("","删除成功!");
        } else {
            return Result.failed("删除失败!");
        }
    }
    @CrossOrigin
    @PostMapping(value = "api/notice")
    @ResponseBody
    public Result addPost(@RequestBody Notice notice){
        try {
            noticeService.AddPosts(notice);
            return Result.success(notice);
        } catch (Exception e){
            return Result.failed();
        }
    }
    @CrossOrigin
    @PostMapping("api/notice/update")
    @ResponseBody
    public Result update(Notice notice){
        try{
            return noticeService.updateNotice(notice);
        }   catch (Exception e){
            e.printStackTrace();
            return Result.failed("");
        }

    }
}
