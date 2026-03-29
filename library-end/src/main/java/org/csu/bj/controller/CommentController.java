package org.csu.bj.controller;

import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Comment;
import org.csu.bj.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommentController {
    @Autowired
    CommentService commentService;
    @CrossOrigin
    @PostMapping("api/comment")
    @ResponseBody
    public Result addComment(@RequestBody Comment comment){
        commentService.addComment(comment);
        return Result.success("");
    }

    @CrossOrigin
    @GetMapping("api/comments")
    @ResponseBody
    public Result<List<Comment>> getComments(@RequestParam(value = "id") int id){
        List<Comment> commentList = commentService.getComments(id);
        return Result.success(commentList);
    }
}
