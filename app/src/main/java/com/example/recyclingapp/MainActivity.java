package com.example.recyclingapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclingapp.network.MistralClient;
import com.example.recyclingapp.network.MistralMessage;
import com.example.recyclingapp.network.MistralRequest;
import com.example.recyclingapp.network.MistralResponse;

import java.util.Collections;

/**
 * HelloWorld â€“ Beispiel fÃ¼r einen einfachen Mistral-API-Aufruf.
 * Wird spÃ¤ter durch die RecyclingApp-Screens ersetzt.
 *
 * MVC-Rolle: VIEW (zeigt das Ergebnis an)
 */
public class MainActivity extends AppCompatActivity {

    private TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloTextView = findViewById(R.id.helloTextView);
        helloTextView.setText("Frage Mistral...");

        // Einfacher Beispiel-Aufruf: "Sage Hallo!"
        askMistral("Sag kurz Hallo auf Deutsch!");
    }

    /**
     * Einfaches Beispiel: Sendet eine Textnachricht an Mistral und zeigt die Antwort an.
     */
    private void askMistral(String prompt) {
        MistralRequest request = new MistralRequest(
                Collections.singletonList(new MistralMessage("user", prompt))
        );

        String authHeader = "Bearer " + BuildConfig.MISTRAL_API_KEY;

        MistralClient.getService().chat(authHeader, request)
                .enqueue(new retrofit2.Callback<MistralResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<MistralResponse> call,
                                           retrofit2.Response<MistralResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().choices.isEmpty()) {
                            String answer = response.body().choices.get(0).message.getContent();
                            runOnUiThread(() -> helloTextView.setText(answer));
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MistralResponse> call, Throwable t) {
                        runOnUiThread(() -> helloTextView.setText("Fehler: " + t.getMessage()));
                    }
                });
    }
}
