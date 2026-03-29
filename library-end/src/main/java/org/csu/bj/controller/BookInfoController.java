package org.csu.bj.controller;

import org.csu.bj.api.vo.Result;
import org.csu.bj.constant.CommonConstant;
import org.csu.bj.entity.Admin;
import org.csu.bj.entity.BookInfo;
import org.csu.bj.service.BookInfoService;
import org.csu.bj.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
public class BookInfoController {
    @Autowired
    BookInfoService bookInfoService;
    @CrossOrigin
    @PostMapping("/api/book/enterBook")
    @ResponseBody
    public Result enterBook(@RequestBody BookInfo bookInfo){
        try {
            bookInfoService.enterBook(bookInfo);
            return Result.success(bookInfo);
        }catch (Exception e){
            e.printStackTrace();
            return Result.failed();
        }
    }
    @CrossOrigin
    @GetMapping("/api/book/all")
    @ResponseBody
    public Result enterBook(){
        List<BookInfo> bookInfos = bookInfoService.getAllBooks();
        return Result.success(bookInfos);
    }
    @CrossOrigin
    @GetMapping("/api/book/allByClass")
    @ResponseBody
    public Result enterBook(@RequestParam String classification){
        List<BookInfo> bookInfos = bookInfoService.getBooksStartWithClass(classification);
        return Result.success(bookInfos);
    }
    @CrossOrigin
    @GetMapping("/api/book/allIsNull")
    @ResponseBody
    public Result getBook(){
        List<BookInfo> bookInfos = bookInfoService.getBooksWithNullClassification();
        return Result.success(bookInfos);
    }
    @CrossOrigin
    @PostMapping("/api/book/update")
    @ResponseBody
    public Result update(@RequestBody BookInfo bookInfo){
        BookInfo bookInfos = bookInfoService.updateBook(bookInfo);
        return Result.success(bookInfos);
    }
    @CrossOrigin
    @GetMapping("/api/book/delete")
    @ResponseBody
    public Result delete(@RequestParam int id){
        bookInfoService.deleteBook(id);
        return Result.success("删除成功");
    }
}
