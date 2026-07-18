package com.example.doglistener.audio;

public class AudioChunk {

    private final byte[] pcm;

    private final long timestamp;

    public AudioChunk(byte[] pcm, long timestamp) {

        this.pcm = pcm;
        this.timestamp = timestamp;

    }

    public byte[] getPcm() {
        return pcm;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
