package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Models.ClassificationResponse;
import com.soumen.kalpavriksha.Models.PingResponse;
import com.soumen.kalpavriksha.Models.Query;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class LLMService {

    private final WebClient webClient;

    public LLMService(WebClient webClient)
    {
        this.webClient = webClient.mutate().baseUrl("https://spal01-sentenceencoder.hf.space").build();
    }

    public Mono<PingResponse> ping()
    {
        return webClient.get()
                .uri("/")
                .retrieve()
                .bodyToMono(PingResponse.class);
    }

    public Mono<Map<String, Object>> askQuestion(String question)
    {
        return webClient.post()
                .uri("https://spal01-test.hf.space/generate")
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .bodyValue(Map.of("prompt", question))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    if (response == null || response.isEmpty()) {
                        return Response.error("Internal server error");
                    }
                    return Response.success("Question asked successfully", response);
                })
                .onErrorResume(err -> {
                    err.printStackTrace();
                    return Mono.just(Response.error("Error: " + err.getMessage()));
                });
    }


    public Mono<ClassificationResponse> validateQuestion(String question)
    {
        Query query = new Query(question);

        return webClient.post()
                .uri("/classify-query")
                .bodyValue(query)
                .retrieve()
                .bodyToMono(ClassificationResponse.class);
    }
}
