package org.csu.bj.controller;

import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Notice;
import org.csu.bj.entity.ReaderCard;
import org.csu.bj.service.LendListService;
import org.csu.bj.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LendListController {
    @Autowired
    LendListService lendListService;
    @Autowired
    SecurityUtil securityUtil;
    @CrossOrigin
    @GetMapping(value = "api/lend")
    @ResponseBody
    public Result lendBook(@RequestParam( value = "isbn") String isbn,@RequestParam( value = "username") String username){
        return lendListService.lendBook(isbn,username);
    }
    @CrossOrigin
    @GetMapping(value = "api/back")
    @ResponseBody
    public Result backBook(@RequestParam( value = "isbn") String isbn,@RequestParam( value = "username") String username){
        return lendListService.backBook(isbn,username);
    }
    @CrossOrigin
    @GetMapping(value = "api/lendList")
    @ResponseBody
    public Result lendList(){
        return Result.success(lendListService.getLendDetails());
    }
}
