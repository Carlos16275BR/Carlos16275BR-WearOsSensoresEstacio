package com.example.wearossensores;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TtsHelper implements TextToSpeech.OnInitListener {

    private static final String TAG = "TtsHelper";

    private final TextToSpeech tts;
    private boolean isReady = false;

    // Guarda o texto caso speak() seja chamado antes do TTS estar pronto
    private String pendingText = null;

    public TtsHelper(Context context) {
        this.tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            // Tenta pt-BR primeiro
            int result = tts.setLanguage(new Locale("pt", "BR"));

            // Se pt-BR não estiver disponível, usa inglês como fallback
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "pt-BR não disponível, usando inglês.");
                tts.setLanguage(Locale.ENGLISH);
            }

            isReady = true;
            Log.d(TAG, "TTS inicializado com sucesso.");

            // Se alguém chamou speak() antes do TTS estar pronto, fala agora
            if (pendingText != null) {
                speak(pendingText);
                pendingText = null;
            }

        } else {
            Log.e(TAG, "Falha ao inicializar TTS. Status: " + status);
        }
    }

    public void speak(String text) {
        if (isReady) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            // TTS ainda não está pronto — guarda o texto para falar quando estiver
            Log.d(TAG, "TTS ainda não pronto. Texto em fila: " + text);
            pendingText = text;
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}