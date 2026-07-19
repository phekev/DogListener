package com.example.doglistener.audio;

import com.example.doglistener.audio.dsp.DspConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RollingAudioBufferTest {

    @Test
    void initiallyNotReady() {

        RollingAudioBuffer buffer =
                new RollingAudioBuffer();

        assertFalse(buffer.isReady());

        assertEquals(0, buffer.size());

    }

    @Test
    void becomesReady() {

        RollingAudioBuffer buffer =
                new RollingAudioBuffer();

        float[] samples =
                new float[DspConstants.PATCH_SAMPLES];

        buffer.append(samples);

        assertTrue(buffer.isReady());

        assertEquals(
                DspConstants.PATCH_SAMPLES,
                buffer.size());

    }

    @Test
    void latestWindowHasCorrectLength() {

        RollingAudioBuffer buffer =
                new RollingAudioBuffer();

        buffer.append(
                new float[DspConstants.PATCH_SAMPLES]);

        float[] window =
                buffer.latestWindow();

        assertEquals(
                DspConstants.PATCH_SAMPLES,
                window.length);

    }

    @Test
    void keepsNewestSamples() {

        RollingAudioBuffer buffer =
                new RollingAudioBuffer();

        float[] first =
                new float[DspConstants.PATCH_SAMPLES];

        for (int i = 0; i < first.length; i++) {
            first[i] = i;
        }

        buffer.append(first);

        buffer.append(new float[] {1000f, 1001f});

        float[] latest =
                buffer.latestWindow();

        assertEquals(2f, latest[0], 0.001f);

        assertEquals(
                1000f,
                latest[latest.length - 2],
                0.001f);

        assertEquals(
                1001f,
                latest[latest.length - 1],
                0.001f);

    }

}
