package com.example.doglistener.audio;

import com.example.doglistener.audio.dsp.DspConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Maintains a rolling window of the most recent audio samples.
 *
 * The buffer always contains the latest PATCH_SAMPLES samples once
 * enough audio has been received.
 */
@Component
public class RollingAudioBuffer {

    private final float[] buffer =
            new float[DspConstants.PATCH_SAMPLES];

    /**
     * Number of valid samples currently stored.
     */
    private int size = 0;

    /**
     * Append new samples to the rolling window.
     */
    public synchronized void append(float[] samples) {

        if (samples.length >= buffer.length) {

            // Keep only the newest PATCH_SAMPLES samples.
            System.arraycopy(
                    samples,
                    samples.length - buffer.length,
                    buffer,
                    0,
                    buffer.length);

            size = buffer.length;
            return;
        }

        int overflow =
                Math.max(0, size + samples.length - buffer.length);

        if (overflow > 0) {

            System.arraycopy(
                    buffer,
                    overflow,
                    buffer,
                    0,
                    buffer.length - overflow);

            size -= overflow;
        }

        System.arraycopy(
                samples,
                0,
                buffer,
                size,
                samples.length);

        size += samples.length;
    }

    /**
     * Returns true once a complete inference window is available.
     */
    public synchronized boolean isReady() {

        return size == buffer.length;

    }

    /**
     * Returns a copy of the latest inference window.
     */
    public synchronized float[] latestWindow() {

        if (!isReady()) {

            throw new IllegalStateException(
                    "Need "
                            + DspConstants.PATCH_SAMPLES
                            + " samples. Currently have "
                            + size);

        }

        return Arrays.copyOf(buffer, buffer.length);

    }

    /**
     * Number of samples currently stored.
     */
    public synchronized int size() {

        return size;

    }

    /**
     * Clears the buffer.
     */
    public synchronized void clear() {

        Arrays.fill(buffer, 0f);

        size = 0;

    }

}
