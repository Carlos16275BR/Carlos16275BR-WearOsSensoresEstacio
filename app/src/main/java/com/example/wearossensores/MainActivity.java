package com.example.wearossensores;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AudioHelper audioHelper;
    private TtsHelper ttsHelper;
    private TextView tvStatus;
    private Button btnSpeak;
    private Button btnBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioHelper = new AudioHelper(this);
        ttsHelper = new TtsHelper(this);

        tvStatus = findViewById(R.id.tvStatus);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnBluetooth = findViewById(R.id.btnBluetooth);

        // Verifica saída de áudio disponível no início
        updateAudioStatus();

        // Registra callback dinâmico para mudanças de dispositivo de áudio
        audioHelper.registerAudioDeviceCallback(
                () -> runOnUiThread(() -> {
                    tvStatus.setText("Bluetooth conectado");
                    ttsHelper.speak("Fone de ouvido conectado. Pronto para uso.");
                }),
                () -> runOnUiThread(() -> {
                    tvStatus.setText("Bluetooth desconectado !!");
                    ttsHelper.speak("Fone de ouvido desconectado.");
                })
        );

        // Botão: falar com TTS
        btnSpeak.setOnClickListener(v -> ttsHelper.speak("Olá! Assistência ativa para funcionários com necessidades especiais."));

        // Botão: abre as configurações Bluetooth
        btnBluetooth.setOnClickListener(v ->
                BluetoothHelper.openBluetoothSettings(this)
        );
    }

    private void updateAudioStatus() {
        if (audioHelper.isBluetoothHeadsetConnected()) {
            tvStatus.setText("Bluetooth conectado !!");
        } else if (audioHelper.isSpeakerAvailable()) {
            tvStatus.setText("Usando alto-falante !!");
        } else {
            tvStatus.setText("Nenhuma saída de áudio detectada!!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsHelper.shutdown();
    }
}