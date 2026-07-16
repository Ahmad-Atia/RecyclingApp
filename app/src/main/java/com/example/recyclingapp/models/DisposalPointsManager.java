package com.example.recyclingapp.models;

import com.example.recyclingapp.network.OverpassClient;
import com.example.recyclingapp.network.OverpassResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisposalPointsManager {
    private List<DisposalPoint> disposalPoints;

    public DisposalPointsManager() {
        this.disposalPoints = new ArrayList<>();
    }

    public void fetchPoints(double lat, double lon, final PointsCallback callback) {
        fetchPointsWithRetry(lat, lon, callback, 0);
    }

    private void fetchPointsWithRetry(double lat, double lon, final PointsCallback callback, int retryCount) {
        String query = "[out:json];node[\"amenity\"=\"recycling\"](around:1000," + lat + "," + lon + ");out;";
        OverpassClient.getApiService().getRecyclingPoints(query).enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Call<OverpassResponse> call, Response<OverpassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    disposalPoints.clear();
                    for (OverpassResponse.Element element : response.body().getElements()) {
                        disposalPoints.add(mapElementToModel(element));
                    }
                    callback.onSuccess(disposalPoints);
                } else if ((response.code() == 504 || response.code() == 429) && retryCount < 2) {
                    // Timeout or Rate Limit -> Try next server
                    OverpassClient.switchToNextServer();
                    fetchPointsWithRetry(lat, lon, callback, retryCount + 1);
                } else if (response.code() == 504 || response.code() == 429) {
                    callback.onError("Der Server ist aktuell überlastet. Bitte versuche es in Kürze erneut.");
                } else {
                    callback.onError("Fehler beim Laden der Entsorgungsstellen (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<OverpassResponse> call, Throwable t) {
                if (retryCount < 2) {
                    OverpassClient.switchToNextServer();
                    fetchPointsWithRetry(lat, lon, callback, retryCount + 1);
                } else {
                    callback.onError(t.getMessage());
                }
            }
        });
    }

    private DisposalPoint mapElementToModel(OverpassResponse.Element element) {
        DisposalPoint point = new DisposalPoint();
        point.setPointId(String.valueOf(element.getId()));
        
        String name = "Recycling Point";
        String address = "Dortmund";
        
        if (element.getTags() != null) {
            if (element.getTags().containsKey("name")) {
                name = element.getTags().get("name");
            } else if (element.getTags().containsKey("operator")) {
                name = element.getTags().get("operator");
            }
            
            if (element.getTags().containsKey("addr:street")) {
                address = element.getTags().get("addr:street");
                if (element.getTags().containsKey("addr:housenumber")) {
                    address += " " + element.getTags().get("addr:housenumber");
                }
            }
        }
        
        point.setName(name);
        point.setAddress(address);
        
        Location loc = new Location();
        loc.setLatitude(element.getLat());
        loc.setLongitude(element.getLon());
        loc.setLocationName(name);
        point.setLocation(loc);
        
        return point;
    }

    public List<DisposalPoint> searchPoints(Location loc) {
        return disposalPoints;
    }

    public void calculateRoute() {
        // Implementation for routing
    }

    public interface PointsCallback {
        void onSuccess(List<DisposalPoint> points);
        void onError(String error);
    }
}
