package com.sneha.vectordb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.*;

@Service
public class OllamaService {
    private final RestClient client;
    private final String embeddingModel;
    private final String generationModel;
    public OllamaService(@Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
                         @Value("${ollama.embedding-model:nomic-embed-text}") String embeddingModel,
                         @Value("${ollama.generation-model:llama3.2:1b}") String generationModel) {
        this.client=RestClient.builder().baseUrl(baseUrl).build();this.embeddingModel=embeddingModel;this.generationModel=generationModel;
    }
    @SuppressWarnings("unchecked") public double[] embed(String text){
        Map<String,Object> body=Map.of("model",embeddingModel,"input",text);
        Map<String,Object> response=client.post().uri("/api/embed").contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(Map.class);
        if(response==null)throw new IllegalStateException("Empty response from Ollama");
        Object embeddings=response.get("embeddings");
        if(embeddings instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof List<?> vector){return vector.stream().mapToDouble(x->((Number)x).doubleValue()).toArray();}
        Object legacy=response.get("embedding"); if(legacy instanceof List<?> list)return list.stream().mapToDouble(x->((Number)x).doubleValue()).toArray();
        throw new IllegalStateException("Ollama did not return an embedding. Check the model name and Ollama version.");
    }
    @SuppressWarnings("unchecked") public String generate(String prompt){
        Map<String,Object> body=Map.of("model",generationModel,"prompt",prompt,"stream",false);
        Map<String,Object> response=client.post().uri("/api/generate").contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(Map.class);
        return response==null?"":String.valueOf(response.getOrDefault("response",""));
    }
    public Map<String,Object> status(){try{Map<?,?> tags=client.get().uri("/api/tags").retrieve().body(Map.class);return Map.of("online",true,"embeddingModel",embeddingModel,"generationModel",generationModel,"models",tags==null?List.of():tags.get("models"));}catch(Exception e){return Map.of("online",false,"embeddingModel",embeddingModel,"generationModel",generationModel,"message","Start Ollama and pull the configured models.");}}
}
