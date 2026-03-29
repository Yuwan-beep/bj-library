package com.example.demo.controller;

import io.github.lnyocly.ai4j.listener.SseListener;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletion;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatMessage;
import io.github.lnyocly.ai4j.service.IChatService;
import io.github.lnyocly.ai4j.service.PlatformType;
import io.github.lnyocly.ai4j.service.factor.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;

@RestController
public class OpenAiController {

    // 注入Ai服务
    @Autowired
    private AiService aiService;
    @CrossOrigin
    @GetMapping("api/chatStream")
    public SseEmitter getChatMessageStream(@RequestParam String question) {
        SseEmitter emitter = new SseEmitter();

        // 获取DEEPSEEK的聊天服务
        IChatService chatService = aiService.getChatService(PlatformType.DEEPSEEK);

        // 创建请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("deepseek-chat")
                .message(ChatMessage.withUser(question))
                .build();


        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                SseListener sseListener = new SseListener() {
                    @Override
                    protected void send() {
                        try {
                            emitter.send(this.getCurrData());
                            System.out.println(this.getCurrData());  // 打印当前发送的内容
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }
                };

                emitter.onCompletion(() -> {
                    System.out.println("完成");
                    sseListener.getEventSource().cancel();

                });

                // 发送流式数据
                chatService.chatCompletionStream(chatCompletion, sseListener);

                // 完成后关闭连接
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @CrossOrigin
    @GetMapping("api/chatDeepStream")
    public SseEmitter getChatMessageStreamDeep(@RequestParam String question) {
        SseEmitter emitter = new SseEmitter();

        // 获取DEEPSEEK的聊天服务
        IChatService chatService = aiService.getChatService(PlatformType.DEEPSEEK);

        // 创建请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("deepseek-reasoner")
                .message(ChatMessage.withUser(question))
                .build();


        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                SseListener sseListener = new SseListener() {
                    @Override
                    protected void send() {
                        try {
                            emitter.send(this.getCurrData());
                            System.out.println(this.getCurrData());  // 打印当前发送的内容
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }
                };

                emitter.onCompletion(() -> {
                    System.out.println("完成");
                    sseListener.getEventSource().cancel();

                });

                // 发送流式数据
                chatService.chatCompletionStream(chatCompletion, sseListener);

                // 完成后关闭连接
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}

