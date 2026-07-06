package com.example.recyclingapp.network;

import com.example.recyclingapp.models.ScanResult;

public class AiRecognition implements ScanStrategy {
    @Override
    public ScanResult scan(byte[] imageData) {
        return new ScanResult();
    }
}
