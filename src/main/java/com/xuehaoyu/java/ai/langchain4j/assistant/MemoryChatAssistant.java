package com.xuehaoyu.java.ai.langchain4j.assistant;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 00:52
 * 初级智能体
 */
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,chatModel = "qwenChatModel",chatMemory = "chatMemory")
public interface MemoryChatAssistant {
    String chat(String message);
}
