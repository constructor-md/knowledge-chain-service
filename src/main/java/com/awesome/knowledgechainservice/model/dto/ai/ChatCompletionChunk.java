package com.awesome.knowledgechainservice.model.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionChunk {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private String systemFingerprint;
    private Usage usage;



    @Data
    public static class Choice {
        private int index;
        private Delta delta;
        private String finishReason;
    }

    @Data
    public static class Delta {
        private String content;
        private String reasoningContent;
    }

    @Data
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }
}

