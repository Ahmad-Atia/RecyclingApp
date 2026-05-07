package com.example.recyclingapp.network;

import java.util.List;

/**
 * Response-Objekt der Mistral Chat Completion API.
 */
public class MistralResponse {
    public List<Choice> choices;

    public static class Choice {
        public MistralMessage message;
    }
}

