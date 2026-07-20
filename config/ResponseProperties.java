package com.example.doglistener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "response")
public class ResponseProperties {

    private String firstSoundFile = "sounds/response-1.wav";
    private String secondSoundFile = "sounds/response-2.wav";
    private String prolongedSoundFile = "sounds/response-prolonged.wav";

    private float volume = 1.0f;

    private long secondResponseDelayMillis = 10_000;
    private long prolongedResponseDelayMillis = 60_000;
    private long quietResetMillis = 10_000;

    public String getFirstSoundFile() {
        return firstSoundFile;
    }

    public void setFirstSoundFile(String firstSoundFile) {
        this.firstSoundFile = firstSoundFile;
    }

    public String getSecondSoundFile() {
        return secondSoundFile;
    }

    public void setSecondSoundFile(String secondSoundFile) {
        this.secondSoundFile = secondSoundFile;
    }

    public String getProlongedSoundFile() {
        return prolongedSoundFile;
    }

    public void setProlongedSoundFile(String prolongedSoundFile) {
        this.prolongedSoundFile = prolongedSoundFile;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public long getSecondResponseDelayMillis() {
        return secondResponseDelayMillis;
    }

    public void setSecondResponseDelayMillis(long secondResponseDelayMillis) {
        this.secondResponseDelayMillis = secondResponseDelayMillis;
    }

    public long getProlongedResponseDelayMillis() {
        return prolongedResponseDelayMillis;
    }

    public void setProlongedResponseDelayMillis(
            long prolongedResponseDelayMillis
    ) {
        this.prolongedResponseDelayMillis =
                prolongedResponseDelayMillis;
    }

    public long getQuietResetMillis() {
        return quietResetMillis;
    }

    public void setQuietResetMillis(long quietResetMillis) {
        this.quietResetMillis = quietResetMillis;
    }
}
