package com.neo4j.ai.services.agents;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface QuestionAgent {

    @SystemMessage("""
            As a final summary answer on user question return info in the following format.
            Generated Cypher Query: query
            Query processing result: processing result
            """)
    String chat(@UserMessage String userMessage);

}
