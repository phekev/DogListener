package com.example.doglistener.audio.dsp;

public final class DspConstants {

    private DspConstants() {
    }

    public static final int SAMPLE_RATE = 16000;

    public static final int FFT_SIZE = 512;

    public static final int WINDOW_SIZE = 400;

    public static final int HOP_SIZE = 160;

    public static final int MEL_BINS = 64;

    public static final int FRAMES = 96;

    public static final float MIN_FREQUENCY = 125.0f;

    public static final float MAX_FREQUENCY = 7500.0f;

    public static final float LOG_OFFSET = 0.001f;
    
    public static final int FFT_BINS = FFT_SIZE / 2 + 1;
    
    public static final int PATCH_SAMPLES =
        WINDOW_SIZE + (FRAMES - 1) * HOP_SIZE;
}
