![](https://cdn.nlark.com/yuque/0/2025/png/29704292/1751961350410-798b38f6-0ed1-40fa-891f-d2ce94661dcc.png)

### <font style="color:#080808;background-color:#ffffff;">1.assistant接口</font>

<font style="color:#080808;background-color:#ffffff;">com.xuehaoyu.java.ai.langchain4j.assistant;</font>

```java
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
//        chatModel = "qwenChatModel",
        streamingChatModel = "qwenStreamingChatModel",  //选择千问模型，并且支持流输出
        chatMemoryProvider = "chatMemoryProviderXiaozhi", // 持久化数据
        tools = "appointmentTools", // function calling
        contentRetriever = "contentRetrieverXiaozhiPincone" // 使用远程向量数据库进行管理
)
public interface XiaozhiAgent {

    @SystemMessage(fromResource = "xiaozhi-prompt-template.txt")
    Flux<String> chat(@MemoryId Long memroyId, @UserMessage String userMessage);
}

```

### 2配置config

#### 1）聊天隔离<font style="color:#080808;background-color:#ffffff;">SeparateChatAssistantConfig</font>

```java
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

```

#### 2）聊天记忆MemoryChatAssistantConfig

```java
@Configuration
public class MemoryChatAssistantConfig {

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.withMaxMessages(10);
    }
}
```

#### 3）向量存储嵌入<font style="color:#080808;background-color:#ffffff;">EmbeddingStoreConfig</font>

```java
@Configuration
public class EmbeddingStoreConfig {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        // 创建向量存储，使用 Pinecone 作为嵌入存储
        EmbeddingStore<TextSegment> embeddingStore = PineconeEmbeddingStore.builder()
                .apiKey("pcsk_56nmi6_96RSe7wcoqj7JUuhTVYzKBL6gev7qzv5ctxtsZn84HtovfsBdrBfbbMgScyfBm8")
                .index("xiaozhi-index")
                .nameSpace("xiaozhi-namespace")
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud("AWS")
                        .region("us-east-1")
                        .dimension(embeddingModel.dimension())
                        .build())
                .build();

        return embeddingStore;
    }
}
```

[Pinecone Console](https://app.pinecone.io/)

#### 4）agent配置<font style="color:#080808;background-color:#ffffff;">XiaozhiAgentConfig</font>

```java
@Configuration
public class XiaozhiAgentConfig {

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Autowired
    private EmbeddingStore embeddingStore;
    @Autowired
    private EmbeddingModel embeddingModel;

    @Bean
    public ChatMemoryProvider chatMemoryProviderXiaozhi(){
        return memoryId ->
            MessageWindowChatMemory.builder()
                    .id(memoryId)
                    .maxMessages(20)
                    .chatMemoryStore(mongoChatMemoryStore)
                    .build();
    }

    // 使用外部向量服务器
    @Bean
    public ContentRetriever contentRetrieverXiaozhiPincone() {
        // 创建一个 EmbeddingStoreContentRetriever 对象，用于从嵌入存储中检索内容
        return EmbeddingStoreContentRetriever
                .builder()
                // 设置用于生成嵌入向量的嵌入模型
                .embeddingModel(embeddingModel)
                // 指定要使用的嵌入存储
                .embeddingStore(embeddingStore)
                // 设置最大检索结果数量，这里表示最多返回 1 条匹配结果
                .maxResults(1)
                // 设置最小得分阈值，只有得分大于等于 0.8 的结果才会被返回
                .minScore(0.8)
                // 构建最终的 EmbeddingStoreContentRetriever 实例
                .build();
    }
}
```

### 3.controller流输出

```java
@Tag(name="xiaozhi")
@RestController
@RequestMapping("/xiaozhi")
public class XiaozhiController {

    @Autowired
    private XiaozhiAgent xiaozhiAgent;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat" ,produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm){
        return xiaozhiAgent.chat(chatForm.getMemoryId(),chatForm.getMessage());
    }
}
```

### 4.聊天记录持久化<font style="color:#080808;background-color:#ffffff;">MongoChatMemoryStore</font>

```java
@Component
public  class MongoChatMemoryStore implements ChatMemoryStore {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);
        if(chatMessages == null) return new LinkedList<>();
        return ChatMessageDeserializer.messagesFromJson(chatMessages.getContent());
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("content", ChatMessageSerializer.messagesToJson(messages));
        //根据query条件能查询出文档，则修改文档；否则新增文档
        mongoTemplate.upsert(query, update, ChatMessages.class);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, ChatMessages.class);
    }

}
```

### 5.function calling

```java
@Component
public class AppointmentTools {

    @Autowired
    private AppointmentService appointmentService;

    @Tool(name = "预约挂号",value = "根据参数,先执行工具方法queryDepartment查询是否可预约，并直接给用户回答是否可预约,并让用户确认所有预约信息，用户确认后再进行预约.如果用户没有提供具体的医生姓名，请从向量存储中找到一位医生。")
    public String bookAppointment(Appointment appointment) {
        Appointment appointmentDB = appointmentService.getOne(appointment);

        if(appointmentDB == null){
            appointment.setId(null);//防止大模型幻觉设置了id
            if(appointmentService.save(appointment)){
                return "预约成功，并返回预约详情";
            }else{
                return "预约失败";
            }
        }
        return "您在相同的科室和时间已有预约";
    }

    @Tool(name="取消预约挂号",value="根据参数，查询预约是否存在，如果存在则删除预约记录并返回取消预约成功，否则返回取消预约失败")
    public String cancelAppointment(Appointment appointment){
        Appointment appointmentDB = appointmentService.getOne(appointment);
        if(appointmentDB != null){
            //删除预约记录
            if(appointmentService.removeById(appointmentDB.getId())){
                return "取消预约成功";
            }else{
                return "取消预约失败";
            }
        }
        //取消失败
        return "您没有预约记录，请核对预约科室和时间";
    }

    @Tool(name = "查询是否有号源", value= "根据科室名称，日期，时间和医生查询是否有号源，并返回给用户")
    public boolean queryDepartment(
            @P(value = "科室名称") String name,
            @P(value = "日期") String date,
            @P(value = "时间，可选值：上午、下午") String time,
            @P(value = "医生名称", required = false) String doctorName
    ) {
        System.out.println("查询是否有号源");
        System.out.println("科室名称：" + name);
        System.out.println("日期：" + date);
        System.out.println("时间：" + time);
        System.out.println("医生名称：" + doctorName);

        //TODO 维护医生的排班信息：
        //如果没有指定医生名字，则根据其他条件查询是否有可以预约的医生（有返回true，否则返回false）；
        //如果指定了医生名字，则判断医生是否有排班（没有排版返回false）
        //如果有排班，则判断医生排班时间段是否已约满（约满返回false，有空闲时间返回true）

        return true;
    }
}

```

### 6.properties

```xml
# model
langchain4j.community.dashscope.chat-model.api-key=sk-959f13b8c1b44e80817ea6971931705d
langchain4j.community.dashscope.chat-model.model-name=qwen-max-2025-01-25

# stream output
langchain4j.community.dashscope.streaming-chat-model.api-key=sk-959f13b8c1b44e80817ea6971931705d
langchain4j.community.dashscope.streaming-chat-model.model-name=qwen-plus

# vector
langchain4j.community.dashscope.embedding-model.api-key=sk-959f13b8c1b44e80817ea6971931705d
langchain4j.community.dashscope.embedding-model.model-name=text-embedding-v3

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/chat_memory_db

# debug level
logging.level.root=info

# mysql
spring.datasource.url=jdbc:mysql://localhost:3306/guiguxiaozhi?
useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=root
spring.datasource.password=20031206
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# mybatis-plus
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

