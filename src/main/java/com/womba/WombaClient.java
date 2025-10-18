package com.womba;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * HTTP client for Womba API
 */
public class WombaClient {
    private final String baseUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public WombaClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(120))
                .build();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Generate test cases for a Jira story
     */
    public GenerateResponse generateTests(String storyKey, boolean uploadToZephyr) throws IOException, InterruptedException {
        GenerateRequest request = new GenerateRequest(storyKey, uploadToZephyr);
        String jsonBody = gson.toJson(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/test-plans/generate"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
            throw new IOException(String.format("API error %d: %s - %s",
                    response.statusCode(), error.error, error.detail));
        }

        return gson.fromJson(response.body(), GenerateResponse.class);
    }

    /**
     * Check API health
     */
    public Map<String, Object> healthCheck() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
    }

    // Request/Response models
    public static class GenerateRequest {
        private final String story_key;
        private final boolean upload_to_zephyr;

        public GenerateRequest(String storyKey, boolean uploadToZephyr) {
            this.story_key = storyKey;
            this.upload_to_zephyr = uploadToZephyr;
        }
    }

    public static class GenerateResponse {
        public String story_key;
        public List<TestCase> test_cases;
        public double quality_score;
        public String suggested_folder;
        public double execution_time_seconds;
        public List<String> zephyr_ids;
        public Map<String, Object> metadata;
    }

    public static class TestCase {
        public String title;
        public String description;
        public List<Map<String, String>> steps;
        public String preconditions;
        public String expected_result;
        public String priority;
        public String test_type;
    }

    public static class ErrorResponse {
        public String error;
        public String detail;
        public int status_code;
    }
}

