package org.csu.bj.controller;

import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Lecture;
import org.csu.bj.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LectureController {
    @Autowired
    LectureService lectureService;
    @GetMapping(value = "api/lecture/all")
    @CrossOrigin
    @ResponseBody
    public Result<List<Lecture>> GetpostsAll(){
        List<Lecture> lectures = lectureService.GetLectureAll();
        return Result.success(lectures);
    }
    @CrossOrigin
    @GetMapping(value = "api/lecture")
    @ResponseBody
    public Result viewPosts(@RequestParam( value = "id") int id){
        Lecture lecture = lectureService.getById(id);
        return Result.success(lecture);
    }
    @CrossOrigin
    @DeleteMapping("api/lecture")
    @ResponseBody
    public Result deleteOnePost(@RequestParam(value = "id") Integer id){
        int deletePost = lectureService.DeleteOne(id);
        if(deletePost == 1){
            return Result.success("","删除成功!");
        } else {
            return Result.failed("删除失败!");
        }
    }
    @CrossOrigin
    @PostMapping(value = "api/lecture")
    @ResponseBody
    public Result addPost(@RequestBody Lecture lecture){
        try {
            lectureService.AddLecture(lecture);
            return Result.success(lecture);
        } catch (Exception e){
            return Result.failed();
        }
    }
}
