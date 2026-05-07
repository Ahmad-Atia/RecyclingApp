# Recycling App

Eine Android-App zur intelligenten Mülltrennung mittels KI-Kamera-Scan (Mistral Vision API).

## Setup & Credentials

Um die App lokal kompilieren und ausführen zu können, benötigst du einen Mistral API Key. Dieser wird sicher in der `local.properties` hinterlegt und nicht auf GitHub hochgeladen.

Öffne die Datei `local.properties` im Hauptverzeichnis des Projekts (falls sie nicht existiert, lege sie an) und füge folgende Zeile ein:

```properties
MISTRAL_API_KEY="dein_mistral_api_key_hier"
```

## Starten

1. Projekt in Android Studio öffnen
2. Gradle Sync abwarten
3. App auf dem Emulator oder einem Android-Gerät ausführen
