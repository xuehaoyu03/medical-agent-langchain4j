package com.xuehaoyu.java.ai.langchain4j.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 01:03
 */

@Configuration
public class SeparateChatAssistantConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return memoryId -> MessageWindowChatMemory.builder().id(memoryId).maxMessages(10).build();
    }
}
