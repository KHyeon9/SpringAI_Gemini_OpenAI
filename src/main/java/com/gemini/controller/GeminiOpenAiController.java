package com.gemini.controller;

import com.gemini.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    /**
     * Gemini에게 이미지 설명 요청 및 응답
     */
    @GetMapping("/image/explain")
    public String explainImage(MultipartFile file) {
        try {
            return geminiChatService.explainImage(file);
        } catch (IOException e) {
            return "이미지 설명 중 오류 발생: " + e.getMessage();
        }
    }

    /**
     * Gemini에게 오디오 설명 요청 및 응답
     */
    @GetMapping("/wav/explain")
    public String explainWav(MultipartFile file) {
        try {
            return geminiChatService.analyzeAudio(file);
        } catch (IOException e) {
            return "오디오 설명 중 오류 발생: " + e.getMessage();
        }
    }

    /**
     * Gemini에게 동영상 설명 요청 및 응답
     */
    @GetMapping("/video/explain")
    public String explainVideo(MultipartFile file) {
        try {
            return geminiChatService.analyzeVideo(file);
        } catch (IOException e) {
            return "동영상 설명 중 오루 발생: " + e.getMessage();
        }
    }
}
