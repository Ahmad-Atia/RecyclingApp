package com.example.recyclingapp.controllers;

import com.example.recyclingapp.models.Item;
import com.example.recyclingapp.models.ScanResult;
import com.example.recyclingapp.models.DisposalPointsManager;
import com.example.recyclingapp.models.Location;
import com.example.recyclingapp.network.ScanStrategy;
import com.example.recyclingapp.network.AiRecognition;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScanController {
    private FirebaseFirestore db;
    private ScanStrategy scanStrategy;
    private DisposalPointsManager disposalPointsManager;

    public ScanController() {
        db = FirebaseFirestore.getInstance();
        scanStrategy = new AiRecognition(); 
        disposalPointsManager = new DisposalPointsManager();
    }

    public void uploadAndAnalyzeImage(File imageFile) {
        try {
            byte[] imageData = new byte[(int) imageFile.length()];
            try (FileInputStream fis = new FileInputStream(imageFile)) {
                fis.read(imageData);
            }
            ScanResult result = scanStrategy.scan(imageData);
            
            result.setId(java.util.UUID.randomUUID().toString());
            result.setTimestamp(new java.util.Date());
            result.setDetectedItems(Arrays.asList(new Item("1", "Plastikflasche", "Gelbe Tonne")));

            db.collection("scans").add(result.toMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDisposalPoints(double lat, double lon, DisposalPointsManager.PointsCallback callback) {
        disposalPointsManager.fetchPoints(lat, lon, callback);
    }
}
