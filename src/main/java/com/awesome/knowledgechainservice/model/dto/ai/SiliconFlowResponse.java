package com.awesome.knowledgechainservice.model.dto.ai;


import lombok.Data;

import java.util.List;

@Data
public class SiliconFlowResponse {

    /**
     * 唯一标识这次对话完成的ID。
     */
    private String id;
    /**
     * 响应对象的类型。
     * 示例值：chat.completion
     */
    private String object;
    /**
     * 响应生成的时间戳（Unix时间戳，单位为秒）。
     */
    private long created;
    /**
     * 生成响应所使用的模型名称。
     * 示例值：deepseek-ai/DeepSeek-V3
     */
    private String model;
    /**
     * 包含模型生成的对话内容和工具调用信息。
     */
    private List<Choice> choices;
    /**
     * 包含这次对话的使用统计信息。
     */
    private Usage usage;


    @Data
    public static class Choice{
        /**
         * 模型生成的消息对象。
         */
        private Message message;
        /**
         * 指示生成结束的原因。
         * 示例值：stop, eos, length, tool_calls
         */
        private String finishReason;
        /**
         * 模型调用的工具信息。
         */
        private List<ToolCall> toolCalls;

        /**
         * 消息对象，包含角色和内容。
         */
        @Data
        public static class Message {
            /**
             * 消息的发送者角色。
             * 示例值：assistant
             */
            private String role;

            /**
             * 模型生成的具体回答内容。
             */
            private String content;
        }

        /**
         * 工具调用对象，包含工具调用的详细信息。
         */
        @Data
        public static class ToolCall {
            /**
             * 工具调用的唯一标识符。
             */
            private String id;

            /**
             * 工具调用的类型。
             * 示例值：function
             */
            private String type;

            /**
             * 调用的函数对象。
             */
            private Function function;

            /**
             * 函数对象，包含函数名称和参数。
             */
            @Data
            public static class Function {
                /**
                 * 调用的函数名称。
                 */
                private String name;

                /**
                 * 调用函数时的参数，以JSON格式表示。
                 */
                private Object arguments;
            }
        }
    }

    /**
     * 使用统计信息对象。
     */
    @Data
    public static class Usage {
        /**
         * 用户输入的提示（prompt）中包含的token数量。
         */
        private int promptTokens;

        /**
         * 模型生成的回答（completion）中包含的token数量。
         */
        private int completionTokens;

        /**
         * 这次对话中总共使用的token数量。
         */
        private int totalTokens;
    }

}
