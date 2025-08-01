package com.xuehaoyu.java.ai.langchain4j.config;

import com.xuehaoyu.java.ai.langchain4j.store.MongoChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 01:03
 */

@Configuration
public class SeparateChatAssistantConfig {


    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;
    // 使用mongodb进行聊天记录的持久化

    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return memoryId -> MessageWindowChatMemory
                .builder()
                .id(memoryId)
                .maxMessages(10)
                // .chatMemoryStore(new InMemoryChatMemoryStore())
                .chatMemoryStore(mongoChatMemoryStore)
                .build();
    }
}
