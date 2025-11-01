package com.gemini.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiChatService {

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.temperature}")
    private double temperature;

    private final OpenAiApi geminiApi;

    /**
     * 사용자 입력에 대한 Gemini 모델로 채팅 응답받는 메소드
     */
    public ChatResponse getResponse(String userInput) {
        // 메세지 구성
        List<Message> messages = List.of(
                new UserMessage(userInput)
        );
        // 옵션 설정
        ChatOptions options = ChatOptions.builder()
                                .model(model)
                                .temperature(temperature)
                                .build();
        // 프롬포트 작성
        Prompt prompt = new Prompt(messages, options);
        // 쳇 모델 셋팅
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                                        .openAiApi(geminiApi)
                                        .build();

        return chatModel.call(prompt);
    }

}
