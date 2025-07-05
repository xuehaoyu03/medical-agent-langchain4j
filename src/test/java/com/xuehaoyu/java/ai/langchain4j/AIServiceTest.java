package com.xuehaoyu.java.ai.langchain4j;

import com.xuehaoyu.java.ai.langchain4j.assistant.Assisant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author :xuehaoyu
 * @date : 2025/7/6 00:30
 */

@SpringBootTest
public class AIServiceTest {
    @Autowired
    private QwenChatModel qwenChatModel;

    @Test
    public void testChat() {
        Assisant assisant = AiServices.create(Assisant.class, qwenChatModel);
        String answer = assisant.chat("你是谁");
        System.out.printf(answer);
    }

    @Autowired
    private Assisant assisant;
    @Test
    public void testChatSpring() {
        System.out.println(assisant.chat("你是谁"));
    }

}
