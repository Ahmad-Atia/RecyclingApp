package com.example.recyclingapp.network;

import java.util.List;

/**
 * Request-Body fÃ¼r die Mistral Chat Completion API.
 */
public class MistralRequest {
    private String model = "mistral-small-latest";
    private List<MistralMessage> messages;
    private ResponseFormat response_format;

    public MistralRequest(List<MistralMessage> messages) {
        this.messages = messages;
    }

    public MistralRequest(String model, List<MistralMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public void setResponseFormat(ResponseFormat response_format) {
        this.response_format = response_format;
    }

    public static class ResponseFormat {
        private String type;
        public ResponseFormat(String type) { this.type = type; }
    }
}

