package com.xuehaoyu.java.ai.langchain4j.assistant;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 00:28
 */

// 让spring管理 EXPLICIT:特定的指定
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,chatModel = "qwenChatModel")
public interface Assisant {

    String chat(String message);
}
