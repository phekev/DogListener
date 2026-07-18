package com.example.doglistener.ml;

public final class AudioConverter {

    private AudioConverter() {
    }

    public static float[] pcm16ToFloat(byte[] pcm) {

        int samples = pcm.length / 2;

        float[] output = new float[samples];

        for (int i = 0; i < samples; i++) {

            int low = pcm[i * 2] & 0xff;
            int high = pcm[i * 2 + 1];

            short sample = (short) ((high << 8) | low);

            output[i] = sample / 32768.0f;

        }

        return output;
    }

}
