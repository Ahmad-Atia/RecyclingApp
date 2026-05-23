# Implementation Plan - Recycling App MVC

Implement the classes and views defined in the PlantUML diagram for the Recycling App, using Firebase for the backend.

## Proposed Changes

### Build Configuration
#### [build.gradle.kts](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/build.gradle.kts)
- Add `firebase-auth` and `firebase-firestore` dependencies.

### Models
#### [User.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/models/User.java)
- Fields: uid, email, address, ecoScore.
- Methods: toMap(), fromMap().

#### [ScanResult.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/models/ScanResult.java)
- Fields: id, timestamp, imageUrl, detectedItems, depositFound.
- Methods: toMap(), fromMap().

### Controllers
#### [AuthController.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/controllers/AuthController.java)
- Methods: login, register, logout using FirebaseAuth.

#### [ScanController.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/controllers/ScanController.java)
- Methods: uploadAndAnalyzeImage, getDisposalPoints using Firestore.

#### [ProfileController.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/controllers/ProfileController.java)
- Methods: updateAddress, fetchWasteCalendar using Firestore.

### Views (UI)
- Implement Views as Fragments with corresponding XML layouts.

#### [LoginView.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/ui/LoginView.java) & [fragment_login.xml](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/res/layout/fragment_login.xml)
#### [CameraView.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/ui/CameraView.java) & [fragment_camera.xml](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/res/layout/fragment_camera.xml)
#### [ResultView.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/ui/ResultView.java) & [fragment_result.xml](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/res/layout/fragment_result.xml)
#### [DashboardView.java](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/java/com/example/recyclingapp/ui/DashboardView.java) & [fragment_dashboard.xml](file:///C:/Users/aatia/Desktop/Github/RecyclingApp/app/src/main/res/layout/fragment_dashboard.xml)

## Verification Plan
### Automated Tests
- Run `gradle assembleDebug` to ensure compilation.
### Manual Verification
- Verify that XML layouts are correctly formed and link to their respective View classes.
