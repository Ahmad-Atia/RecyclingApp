package com.example.recyclingapp.network;

import com.google.gson.annotations.SerializedName;

/**
 * ReprÃ¤sentiert eine einzelne Nachricht in der Mistral-Konversation.
 * UnterstÃ¼tzt sowohl einfachen Text als auch komplexe Inhalte (z.B. Bild + Text fÃ¼r Vision).
 */
public class MistralMessage {

    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private Object content; // String fÃ¼r Text, List<ContentPart> fÃ¼r Vision

    public MistralMessage(String role, Object content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() { return role; }
    public String getContent() { return String.valueOf(content); }

    // --- Verschachtelte Klassen fÃ¼r Vision (Bild-Analyse) ---

    public static class ContentPart {
        public String type;
        public String text;
        public ImageUrl image_url;

        public ContentPart(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public ContentPart(String type, ImageUrl image_url) {
            this.type = type;
            this.image_url = image_url;
        }
    }

    public static class ImageUrl {
        public String url;
        public ImageUrl(String url) { this.url = url; }
    }
}

