package com.example.wearossensores;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

public class AudioHelper {

    private final Context context;
    private final AudioManager audioManager;

    public AudioHelper(Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean audioOutputAvailable(int type) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            return false;
        }
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo device : devices) {
            if (device.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public boolean isSpeakerAvailable() {
        return audioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
    }

    public boolean isBluetoothHeadsetConnected() {
        return audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
    }

    public void registerAudioDeviceCallback(
            Runnable onBluetoothConnected,
            Runnable onBluetoothDisconnected
    ) {
        audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {

            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);
                if (isBluetoothHeadsetConnected()) {
                    onBluetoothConnected.run();
                }
            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);
                if (!isBluetoothHeadsetConnected()) {
                    onBluetoothDisconnected.run();
                }
            }

        }, null);
    }
}