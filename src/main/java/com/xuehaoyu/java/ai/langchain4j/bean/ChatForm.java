package com.xuehaoyu.java.ai.langchain4j.bean;

import lombok.Data;

/**
 * @author :xuehaoyu
 * @date : 2025/7/7 14:30
 */

@Data
public class ChatForm {

    private Long memoryId; // 对话ID
    private String message; // 用户问题
}
