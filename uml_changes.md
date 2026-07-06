# UML Implementation & Overpass API Integration

This document lists the changes made to implement the requested UML and the **Overpass Turbo (OpenStreetMap)** integration.

## New Models

### [Item](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/models/Item.java)
- **Attributes**: `id: String`, `name: String`, `category: String`.

### [Location](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/models/Location.java)
- **Attributes**: `latitude: double`, `longitude: double`, `locationName: String`.

### [DisposalPoint](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/models/DisposalPoint.java)
- **Attributes**: `pointId: String`, `name: String`, `openingHours: String`, `address: String`, `location: Location`.

### [DisposalPointsManager](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/models/DisposalPointsManager.java)
- **Methods**: 
  - `fetchPoints(lat: double, lon: double, callback: PointsCallback)`: Integrated with Overpass API.
  - `searchPoints(loc: Location): List<DisposalPoint>`

## Network Layer (Overpass API)

### [OverpassApiService](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/network/OverpassApiService.java)
- **Endpoints**: `interpreter` (GET) to send Overpass QL queries.

### [OverpassResponse](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/network/OverpassResponse.java)
- **Structure**: Maps OpenStreetMap JSON elements (nodes with tags) to our model.

### [ScanStrategy](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/network/ScanStrategy.java)
- **Interface**: Defined `scan(imageData: byte[])` as per UML.

## Controller & View

### [ScanController](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/controllers/ScanController.java)
- Updated to coordinate between the scanning strategy and the disposal points manager.

### [DisposalListView](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/ui/DisposalListView.java)
- Real-time display of recycling points using live data from Overpass API.
