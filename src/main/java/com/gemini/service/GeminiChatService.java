package com.gemini.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
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
     * @param userInput 사용자의 질문
     * @return 질문에 대한 응답
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
     * @param file 업로드된 이미지 파일 (MultipartFile)
     * @return 모델이 분석한 이미지의 설명 텍스트
     */
    public String explainImage(MultipartFile file) throws IOException {
        // MultipartFile을 바이트 배열로 변환
        byte[] image = file.getBytes();
        // 바이트 배열을 ByteArrayResource로 감싸 Media 객체 생성 준비
        ByteArrayResource imageResource = new ByteArrayResource(image);
        // Media 객체 생성: 이미지 데이터 + MIME 타입 지정
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

    /**
     * 오디오 파일 분석
     * @param file 업로드된 오디오 파일 (MultipartFile)
     * @return 모델이 분석한 오디오의 설명 텍스트
     */
    public String analyzeAudio(MultipartFile file) throws IOException {
        // MultipartFile을 바이트 배열로 변환
        byte[] audioBytes = file.getBytes();
        // 바이트 배열을 ByteArrayResource로 감싸 Media 객체 생성 준비
        ByteArrayResource audioResource = new ByteArrayResource(audioBytes);

        MediaType mediaType;
        try {
            // 파일의 Content-Type을 파싱하여 MediaType 설정 (ex: audio/mpeg)
            mediaType = MediaType.parseMediaType(file.getContentType());
        } catch (Exception e) {
            // Content-Type이 없거나 파싱 실패 시 기본값으로 설정
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        // Media 객체 생성: 이미지 데이터 + MIME 타입 지정
        Media audioMedia = Media.builder()
                            .mimeType(mediaType)
                            .data(audioResource)
                            .build();
        // UserMessage 생성: 모델에게 전달할 사용자 메시지 + 오디오 포함
        UserMessage userMessage = UserMessage.builder()
                .text("이 오디오를 분석하고 내용을 설명해 주세요.")
                .media(audioMedia)
                .build();
        // 메시지 리스트 구성: 시스템 메시지 + 사용자 메시지
        List<Message> messages = List.of(
                new SystemMessage("당신은 오디오 분석 전문가입니다."),
                userMessage
        );
        // ChatOptions 생성: 모델 옵션 설정
        ChatOptions options = ChatOptions.builder()
                                .model("gemini-2.5-flash")
                                .temperature(0.5)
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

        // 모델의 응답에서 AssistantMessage 추출
        AssistantMessage assistantMessage = response.getResult().getOutput();

        // 모델의 텍스트 출력이 존재하는지 확인 후 결과에 저장
        String resultText = "";
        if (assistantMessage != null && assistantMessage.getText() != null && !assistantMessage.getText().isEmpty()) {
            resultText = assistantMessage.getText();
        } else {
            // 모델이 텍스트 응답을 반환하지 않은 경우
            resultText = "모델에서 출력된 Text가 없습니다.";
        }
        // 예외 상황: 최종 결과가 비어있는 경우 추가 메시지 처리
        if (resultText.isEmpty()) {
            resultText = "모델에서 오디오 분석 결과가 없습니다.";
        }

        return resultText;
    }

}
