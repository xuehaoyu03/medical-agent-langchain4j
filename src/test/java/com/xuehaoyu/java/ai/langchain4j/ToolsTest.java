package com.xuehaoyu.java.ai.langchain4j;

import com.xuehaoyu.java.ai.langchain4j.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author :xuehaoyu
 * @date : 2025/7/7 14:51
 */
@SpringBootTest
public class ToolsTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;



    @Test
    public void testCalculatorTools() {

        String answer = separateChatAssistant.chat( 99,   "1+2等于几，475695037565的平方根是多少");
        //答案：3，689706.4865
        System.out.println(answer);
    }
}


