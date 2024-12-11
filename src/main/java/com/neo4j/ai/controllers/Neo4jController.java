package com.neo4j.ai.controllers;

import com.neo4j.ai.models.Neo4jResponse;
import com.neo4j.ai.services.Neo4jService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class Neo4jController {
    private final Neo4jService service;

    @Operation(summary = "Ready to use")
    @PostMapping("/question")
    public ResponseEntity<Neo4jResponse> makeQuestion(@RequestParam String question) {
        String result = service.makeQuestion(question);
        return ResponseEntity.ok().body(new Neo4jResponse(result));
    }

}
