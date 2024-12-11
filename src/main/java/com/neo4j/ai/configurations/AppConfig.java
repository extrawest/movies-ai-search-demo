package com.neo4j.ai.configurations;

import com.neo4j.ai.services.agents.QuestionAgent;
import com.neo4j.ai.utils.PromptUtil;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.neo4j.Neo4jContentRetriever;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.graph.neo4j.Neo4jGraph;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.singletonList;

@Slf4j
@Configuration
public class AppConfig {
    @Value("${ai.api-key}")
    private String apiKey;
    @Value("${ai.base-url}")
    private String baseUrl;
    @Value("${ai.chat-model-name}")
    private String chatModelName;

    @Bean(name = "chatOpenAiChatModel")
    public OpenAiChatModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(chatModelName)
                .timeout(Duration.ofMinutes(2))
                .logRequests(true)
                .logResponses(true)
                .maxRetries(1)
                .temperature(0.0)
                .maxTokens(2000)
                .build();
    }

    @Bean
    Neo4jContentRetriever neo4jAstContentRetriever(Driver neo4jDriver, ChatLanguageModel chatLanguageModel) throws IOException {
        final PromptTemplate promptTemplate = PromptUtil.loadPromptTemplate(this.getClass(), "neo4j_system_prompt.txt", getPromptVariables().toArray());
        Neo4jGraph neo4jGraph = new Neo4jGraph(neo4jDriver);

        return Neo4jContentRetriever.builder()
                .graph(neo4jGraph)
                .chatLanguageModel(chatLanguageModel)
                .promptTemplate(promptTemplate)
                .build();
    }

    @Bean
    RetrievalAugmentor retrievalAugmentor(Neo4jContentRetriever neo4jAstContentRetriever) {
        QueryRouter queryRouter = query -> singletonList(neo4jAstContentRetriever);

        return DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();
    }

    @Bean
    QuestionAgent assistant(ChatLanguageModel chatLanguageModel, RetrievalAugmentor retrievalAugmentor) {
        return AiServices.builder(QuestionAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    @NotNull
    private static List<String> getPromptVariables() {
        List<String> promptVariables = new LinkedList<>() {};
        promptVariables.add("What are the top 5 highest-rated movies?");
        promptVariables.add("Find actors who have acted in multiple movies");
        promptVariables.add("MATCH (m:Movie) RETURN m.title, m.imdbRating ORDER BY m.imdbRating DESC LIMIT 5");
        promptVariables.add("MATCH (a:Actor)-[:ACTED_IN]->(m:Movie) WITH a, COUNT(DISTINCT m) AS movie_count WHERE movie_count > 1 RETURN a.name, movie_count ORDER BY movie_count DESC");
        return promptVariables;
    }
}
