package com.xuehaoyu.java.ai.langchain4j;


import com.xuehaoyu.java.ai.langchain4j.assistant.Assisant;
import com.xuehaoyu.java.ai.langchain4j.assistant.MemoryChatAssistant;
import com.xuehaoyu.java.ai.langchain4j.assistant.SeparateChatAssistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 00:41
 */
@SpringBootTest
public class ChatMemoryTest {
    @Autowired
    private Assisant assisant;

    @Test
    public void testChatMemory() {
        String answer1 = assisant.chat("我是环环");
        System.out.println(answer1);
        String answer2 = assisant.chat("我是谁");
        System.out.println(answer2);
    }

    @Autowired
    private QwenChatModel qwenChatModel;
    @Test
    public void testChatMemory2() {
        //第一轮对话
        UserMessage userMessage1 = UserMessage.userMessage("我是环环");
        ChatResponse chatResponse1 = qwenChatModel.chat(userMessage1);
        AiMessage aiMessage1 = chatResponse1.aiMessage();
        //输出大语言模型的回复
        System.out.println(aiMessage1.text());
        //第二轮对话
        UserMessage userMessage2 = UserMessage.userMessage("你知道我是谁吗");
        ChatResponse chatResponse2 = qwenChatModel.chat(Arrays.asList(userMessage1, aiMessage1, userMessage2));
        AiMessage aiMessage2 = chatResponse2.aiMessage();
        //输出大语言模型的回复
        System.out.println(aiMessage2.text());
    }

    @Test
    public void testChatMemory3() {
        MessageWindowChatMemory mwcm = MessageWindowChatMemory.withMaxMessages(10);
        Assisant assisant1 = AiServices
                .builder(Assisant.class)
                .chatModel(qwenChatModel)
                .chatMemory(mwcm)
                .build();
        String answer1 = assisant1.chat("我是环环");
        System.out.println(answer1);
        String answer2 = assisant1.chat("我是谁");
        System.out.println(answer2);

    }

    @Autowired
    private MemoryChatAssistant memoryChatAssistant;
    @Test
    public void testChatMemory4() {

        String answer1 = memoryChatAssistant.chat("我是环环");
        System.out.println(answer1);
        String answer2 = memoryChatAssistant.chat("我是谁");
        System.out.println(answer2);

    }

    @Autowired
    private SeparateChatAssistant separateChatAssistant;
    @Test
    public void testChatMemory5() {
        String answer1 = separateChatAssistant.chat(1, "我是环环");
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat(1, "我是谁");
        String answer3 = separateChatAssistant.chat(2, "我是谁");

        System.out.println(answer2);
        System.out.println(answer3);
    }
}