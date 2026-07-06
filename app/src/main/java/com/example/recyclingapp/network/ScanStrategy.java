package com.example.recyclingapp.network;

import com.example.recyclingapp.models.ScanResult;

public interface ScanStrategy {
    ScanResult scan(byte[] imageData);
}
