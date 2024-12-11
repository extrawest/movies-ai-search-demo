package com.neo4j.ai.services;

import com.neo4j.ai.services.agents.QuestionAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Neo4jService {
    private final QuestionAgent questionAgent;

    public String makeQuestion(String question) {
        return questionAgent.chat(question);
    }

}
