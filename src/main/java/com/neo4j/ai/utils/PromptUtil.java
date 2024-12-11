package com.neo4j.ai.utils;

import dev.langchain4j.model.input.PromptTemplate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@UtilityClass
public class PromptUtil {

    public static <T> PromptTemplate loadPromptTemplate(Class<T> clazz, String resourceName, Object[] variables) throws IOException {
        final ClassLoader classLoader = clazz.getClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream(resourceName);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + resourceName);
        }

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 4*1024)) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }

            String promptText = String.format(result.toString(), variables);
            log.info(promptText);

            return PromptTemplate.from(promptText);
        }
    }

}
