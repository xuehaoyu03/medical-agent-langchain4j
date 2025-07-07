package com.xuehaoyu.java.ai.langchain4j;

import com.xuehaoyu.java.ai.langchain4j.assistant.MemoryChatAssistant;
import com.xuehaoyu.java.ai.langchain4j.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author :xuehaoyu
 * @date : 2025/7/7 10:57
 */
@SpringBootTest
public class PromptTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    @Test
    public void testSystemMessage() {
        System.out.println(separateChatAssistant.chat(5,"今天是几号"));
    }

    @Autowired
    private MemoryChatAssistant memoryChatAssistant;
    @Test
    public void testUserMessage() {
        String answer1 = memoryChatAssistant.chat("我是环环");
        System.out.println(answer1);

        String answer2 = memoryChatAssistant.chat("我18了");
        System.out.println(answer1);

        String answer3 = memoryChatAssistant.chat("我是谁");
        System.out.println(answer1);
    }

    @Test
    public void testV() {
        String answer1 = separateChatAssistant.chat2(10,"我是环环");
                System.out.println(answer1);
        String answer2 = separateChatAssistant.chat2(10,"我是谁");
        System.out.println(answer2);
    }

    @Test
    public void testUserInfo() {

        // 从数据库中获取用户
        String username = "翠花";
        int age = 18;

        String answer = separateChatAssistant.chat3(20, "我是谁，多大了",username,age);
        System.out.println(answer);

//        String answer1 = separateChatAssistant.chat2(10, "我是谁");
//        System.out.println(answer1);


    }

}
