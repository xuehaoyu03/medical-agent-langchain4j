package com.xuehaoyu.java.ai.langchain4j.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 01:01
 */

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,chatModel = "qwenChatModel",chatMemory = "chatMemory",
    chatMemoryProvider = "chatMemoryProvider",tools = "calculatorTools")
public interface SeparateChatAssistant {

    // prompt系统提示词，当系统提示词发生改变的时候，之前会话将不会有记忆性了，只会出现一次但是UserMessage会在每次会话中出现
    // @SystemMessage("你是我的好朋友，请用东北话回答问题。今天是{{current_date}}")
    @SystemMessage(fromResource = "my-prompt-template.txt")
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @UserMessage("你是我的好朋友，请用上海话回答问题，并且添加一些表情符号。 {{message}}")
    String chat2(@MemoryId int memoryId, @V("message") String userMessage);

    @SystemMessage(fromResource = "my-prompt-template2.txt")
    String chat3(
            @MemoryId int memroyId,
            @UserMessage String userMessage,
            @V("username") String username,
            @V("age") int age
    );
}
