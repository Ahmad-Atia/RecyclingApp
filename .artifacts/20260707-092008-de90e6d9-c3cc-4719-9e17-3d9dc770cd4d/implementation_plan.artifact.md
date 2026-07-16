# Offline Functionality and Robust Error Handling

Implement offline capabilities, synchronization, and better user communication when network is unavailable.

## User Review Required

- **Offline Scans**: Currently, the AI Scan (Mistral) requires internet. The plan is to inform the user and prevent the scan when offline. A future enhancement could be a background sync via WorkManager, but for now, we will focus on clear error messaging.
- **Persistence**: Firestore's built-in persistence will be explicitly enabled and configured.

## Proposed Changes

### Core Utilities

#### [NEW] [NetworkUtils.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/utils/NetworkUtils.java)
- Provide a static method `isOnline(Context)` using `ConnectivityManager` to check for active internet connection.

### Firebase Configuration

#### [MainActivity.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/MainActivity.java)
- Explicitly configure `FirebaseFirestoreSettings` to enable offline persistence with a reasonable cache size.

### Controllers

#### [ScanController.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/controllers/ScanController.java)
- Add a network check in `uploadAndAnalyzeImage`. If offline, call `callback.onScanFailed` with a specific "Network required" message.

#### [DisposalController.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/controllers/DisposalController.java)
- Add network checks before Geocoding and calling `DisposalPointsManager.fetchPoints`. Inform the user if these live services are unavailable.

### User Interface

#### [DashboardView.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/ui/DashboardView.java)
- Before initiating a scan (either camera or gallery), check connectivity and show a Toast if offline.

#### [DisposalListView.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/ui/DisposalListView.java)
- Show a clearer error message or empty state illustration if points cannot be loaded due to network issues.

---

## Verification Plan

### Automated Tests
- No new automated tests are planned at this stage as these involve hardware state (network), but manual verification will be thorough.

### Manual Verification
1. **Offline Mode**: Enable Airplane mode and try to:
    - View scan history (should work via cache).
    - Start a new scan (should show "Internet required" message).
    - Load disposal points (should show network error Toast).
2. **Weak Connection**: Use emulator network throttling to verify that the app doesn't hang and shows appropriate loading states/timeouts.
3. **Recovery**: Start offline, then disable Airplane mode and verify that data (like address updates) synchronizes with Firebase correctly.
