package com.example.doglistener.audio.dsp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindowFunctionsTest {

    @Test
    void hannWindowHasCorrectLength() {

        float[] window = WindowFunctions.hann(400);

        assertEquals(400, window.length);
    }

    @Test
    void hannWindowStartsNearZero() {

        float[] window = WindowFunctions.hann(400);

        assertEquals(0.0f, window[0], 1e-6);
    }

    @Test
    void hannWindowEndsNearZero() {

        float[] window = WindowFunctions.hann(400);

        assertEquals(0.0f, window[399], 1e-6);
    }

    @Test
    void hannWindowPeaksNearOne() {

        float[] window = WindowFunctions.hann(400);

        float max = 0;

        for (float v : window) {
            max = Math.max(max, v);
        }

        assertTrue(max > 0.99f);
    }
}
