package com.xuehaoyu.java.ai.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author :xuehaoyu
 * @date : 2025/7/8 14:38
 */
@SpringBootTest
public class EmbeddingTest {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore embeddingStore;

    @Test
    public void testEmbedding() {
        Response<Embedding> embeddingResponse = embeddingModel.embed("你好");

        System.out.println(embeddingResponse.content().vector().length);
        System.out.println(embeddingResponse.toString());
    }

    @Test
    public void testPineconeEmbeded() {
        // 将文本转换成向量
        TextSegment segment1 = TextSegment.from("我喜欢羽毛球");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        // 存入向量数据库
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("今天天气很好");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        // 将原本内容和转换后的向量一起存到向量数据库中
        embeddingStore.add(embedding2, segment2);
    }

    @Test
    public void embeddingSearch() {
        // 提问，并将问题转成向量数据
        Embedding queryEmbedding = embeddingModel.embed("你最喜欢的运动是什么？").content();
        // 创建搜索请求对象
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1) // 匹配最相似的一条记录
                // .minScore(0.8)
                .build();

        // 根据搜索请求 searchRequest 在向量存储中进行相似度搜索
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        // searchResult.matches()：获取搜索结果中的匹配项列表。
        //.get(0)：从匹配项列表中获取第一个匹配项
        EmbeddingMatch<TextSegment> embeddingMatch = searchResult.matches().get(0);

        // 获取匹配项的相似度得分
        System.out.println(embeddingMatch.score());
        // 返回文本结果
        System.out.println(embeddingMatch.embedded().text());
    }

    @Test
    public void testUploadKnowledgeLibrary() {
        // 使用FileSystemDocumentLoader读取指定目录下的知识库文档
        // 并使用默认的文档解析器对文档进行解析
        Document document1 = FileSystemDocumentLoader.loadDocument("/Users/xuehaoyu/Desktop/java/projects/medical-agent-langchain4j/src/main/resources/knowledge/医院信息.md");
        Document document2 = FileSystemDocumentLoader.loadDocument("/Users/xuehaoyu/Desktop/java/projects/medical-agent-langchain4j/src/main/resources/knowledge/科室信息.md");
        Document document3 = FileSystemDocumentLoader.loadDocument("/Users/xuehaoyu/Desktop/java/projects/medical-agent-langchain4j/src/main/resources/knowledge/神经内科.md");
        List<Document> documents = Arrays.asList(document1, document2, document3);

        // 文本向量化并存入向量数据库：将每个片段进行向量化，得到一个嵌入向量
        EmbeddingStoreIngestor
                .builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(documents);
    }
}

