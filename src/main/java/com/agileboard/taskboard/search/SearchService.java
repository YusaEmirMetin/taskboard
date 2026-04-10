package com.agileboard.taskboard.search;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SearchService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String ES_URL = "http://localhost:9200";

    public SearchService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Elasticsearch'e yeni döküman kaydet (indeksle)
    public void indexTask(TaskDocument task) {
        try {
            String json = objectMapper.writeValueAsString(task);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ES_URL + "/tasks/_doc/" + task.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("🔍 Elasticsearch'e indekslendi: " + task.getTitle());
        } catch (Exception e) {
            System.err.println("ES indeksleme hatası: " + e.getMessage());
        }
    }

    // Elasticsearch'te arama yap
    public List<TaskDocument> search(String query) {
        List<TaskDocument> results = new ArrayList<>();
        try {
            // Elasticsearch'ün kendi REST API'sine sorgu gönderiyoruz
            String searchJson = """
                {
                  "query": {
                    "multi_match": {
                      "query": "%s",
                      "fields": ["title", "description"],
                      "fuzziness": "AUTO"
                    }
                  }
                }
                """.formatted(query);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ES_URL + "/tasks/_search"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(searchJson))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode hits = root.path("hits").path("hits");

            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                TaskDocument doc = new TaskDocument();
                doc = objectMapper.treeToValue(source, TaskDocument.class);
                results.add(doc);
            }
        } catch (Exception e) {
            System.err.println("ES arama hatası: " + e.getMessage());
        }
        return results;
    }

    // Elasticsearch'ten dökümanı sil
    public void deleteTask(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ES_URL + "/tasks/_doc/" + id))
                .DELETE()
                .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("🔍 Elasticsearch'ten silindi: ID " + id);
        } catch (Exception e) {
            System.err.println("ES silme hatası: " + e.getMessage());
        }
    }
}
