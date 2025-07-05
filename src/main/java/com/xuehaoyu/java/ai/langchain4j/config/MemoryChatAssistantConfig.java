package com.xuehaoyu.java.ai.langchain4j.config;

import com.xuehaoyu.java.ai.langchain4j.assistant.MemoryChatAssistant;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 00:55
 */

@Configuration
public class MemoryChatAssistantConfig {

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.withMaxMessages(10);
    }
}
