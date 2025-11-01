package com.gemini.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        // 메시지 리스트 구성: 사용자 메시지
        List<Message> messages = List.of(
                new UserMessage(userInput)
        );
        // ChatOptions 생성: 모델 옵션 설정
        ChatOptions options = ChatOptions.builder()
                                .model(model)
                                .temperature(temperature)
                                .build();
        // Prompt 객체 생성: 메시지와 옵션을 합쳐서 모델 호출 준비
        Prompt prompt = new Prompt(messages, options);
        // OpenAiChatModel 생성: Gemini API와 연동
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                                        .openAiApi(geminiApi)
                                        .build();

        return chatModel.call(prompt);
    }

    /**
     * 이미지 + 텍스트 입력 받아 응답 반환
     * OpenAI 호환 모드에선 Multipart 형식 지원
     */
    public String explainImage(MultipartFile file) throws IOException {
        //  MultipartFile을 바이트 배열로 변환
        byte[] image = file.getBytes();
        // 바이트 배열을 ByteArrayResource로 감싸 Media 객체 생성 준비
        ByteArrayResource imageResource = new ByteArrayResource(image);
        //  Media 객체 생성: 이미지 데이터 + MIME 타입 지정
        Media imageMedia = Media.builder()
                            .mimeType(MediaType.IMAGE_PNG)
                            .data(imageResource)
                            .build();
        // UserMessage 생성: 모델에게 전달할 사용자 메시지 + 이미지 포함
        UserMessage userMessage = UserMessage.builder()
                                    .text("이 이미지를 자세히 설명해주세요.")
                                    .media(imageMedia)
                                    .build();
        // 메시지 리스트 구성: 시스템 메시지 + 사용자 메시지
        List<Message> messages = List.of(
                new SystemMessage("당신은 시각적 설명 전문가 입니다."),
                userMessage
        );
        // ChatOptions 생성: 모델 옵션 설정
        ChatOptions options = ChatOptions.builder()
                                .model("gemini-2.5-flash")
                                .maxTokens(2000)
                                .build();
        // Prompt 객체 생성: 메시지와 옵션을 합쳐서 모델 호출 준비
        Prompt prompt = new Prompt(messages, options);
        // OpenAiChatModel 생성: Gemini API와 연동
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                                        .openAiApi(geminiApi)
                                        .build();
        // 모델 호출: 이미지와 프롬프트를 전달하고 응답 받기
        ChatResponse response = chatModel.call(prompt);
        // 전체 응답 구조 출력
        System.out.println(response);
        // 모델이 생성한 텍스트 결과 추출 후 반환
        return response.getResult().getOutput().getText();
    }

}
