package org.csu.bj.controller;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;
import org.csu.bj.entity.DeeseekRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class AIController {

    private final Gson gson = new Gson();


    @PostMapping("api/tall")
    public String tallQuestion(@org.springframework.web.bind.annotation.RequestBody String question) throws IOException, UnirestException {

        Unirest.setTimeouts(0, 0);

//DeeseekRequest: 自己的实体类名称

        List<DeeseekRequest.Message> messages = new ArrayList<>();
//给deepSeek一个角色
        messages.add(DeeseekRequest.Message.builder().role("system").content("你是一个语言学家").build());

// question：说你自己想说的话
        messages.add(DeeseekRequest.Message.builder().role("user").content(question).build());

        DeeseekRequest requestBody = DeeseekRequest.builder()
                .model("deepseek-chat")
                .messages(messages)
                .build();
        HttpResponse<String> response = Unirest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer "+"sk-630ea0bff70d4afebccedeb9aec0cca8")
                .body(gson.toJson(requestBody))
                .asString();
        return  response.getBody();

    }
}