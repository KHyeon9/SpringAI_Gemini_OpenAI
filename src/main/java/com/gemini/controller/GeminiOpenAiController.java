package com.gemini.controller;

import com.gemini.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GeminiOpenAiController {

    private final GeminiChatService geminiChatService;

    /**
     * 텍스트를 입력받고 Gemini 모델에 응답
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        ChatResponse response = geminiChatService.getResponse(prompt);
        return response.getResult().getOutput().getText();
    }
}
