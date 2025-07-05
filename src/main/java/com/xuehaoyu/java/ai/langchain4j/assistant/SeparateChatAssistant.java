package com.xuehaoyu.java.ai.langchain4j.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 01:01
 */

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,chatModel = "qwenChatModel",chatMemory = "chatMemory",
    chatMemoryProvider = "chatMemoryProvider")
public interface SeparateChatAssistant {

    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
