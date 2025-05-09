package com.awesome.knowledgechainservice.model.dto.ai;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SiliconFlowRequest {


    /**
     * 指定使用的模型名称。
     * 示例值：deepseek-ai/DeepSeek-V3
     */
    private String model;
    /**
     * 包含对话上下文的消息列表。
     * 每个消息包含角色（role）和内容（content）。
     */
    private List<Message> messages;
    /**
     * 是否以流式（Server-Sent Events, SSE）的方式返回生成的文本。
     * 默认值：false
     */
    private boolean stream;
    /**
     * 指定生成的最大token数量。
     * 范围：1 <= maxTokens <= 16384
     */
    private int maxTokens;

    /**
     * 控制生成文本的随机性。
     * 范围：0.0 到 1.0
     * 模型温度 0.0 - 1.0 精确 -> 多样  严谨 -> 创新
     */
    private double temperature;

    /**
     * 控制生成每个token时的候选词汇数量。
     * 范围：0.0 到 1.0
     */
    private double topP;
    /**
     * 指定生成的文本序列数量。
     * 默认值：1
     */
    private int n;
    /**
     * 指定模型输出的格式。
     */
    private ResponseFormat responseFormat;
    /**
     * 定义模型可以调用的外部工具。
     */
    private List<Tool> tools;

    @Data
    @Accessors(chain = true)
    public static class Message{
        /**
         * user 用户发送的消息
         * assistant AI助手发送的消息
         * system 系统消息 用于设置对话的初始指令或上下文
         */
        private String role;
        /**
         * 消息的具体内容。
         */
        private String content;
    }

    @Data
    @Accessors(chain = true)
    public static class ResponseFormat {
        /**
         * 输出格式类型。
         * 示例值：text
         */
        private String type;
    }

    @Data
    @Accessors(chain = true)
    public static class Tool {
        /**
         * 工具类型。
         * 目前仅支持 function 类型。
         */
        private String type;

        /**
         * 函数对象，定义调用的函数。
         */
        private Function function;

        /**
         * 函数对象，包含函数名称、描述和参数。
         */
        @Data
        @Accessors(chain = true)
        public static class Function {
            /**
             * 函数名称。
             */
            private String name;

            /**
             * 函数描述，帮助模型理解何时调用该函数。
             */
            private String description;

            /**
             * 函数的参数，以JSON Schema格式定义。
             */
            private Object parameters;
        }
    }


}
