package com.example.doglistener.audio.dsp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MelFilterBankTest {

    @Test
    void producesCorrectNumberOfBins() {

        MelFilterBank bank =
                new MelFilterBank();

        float[] spectrum =
                new float[DspConstants.FFT_BINS];

        float[] mel =
                bank.apply(spectrum);

        assertEquals(
                DspConstants.MEL_BINS,
                mel.length);

    }

    @Test
    void doesNotProduceNaN() {

        MelFilterBank bank =
                new MelFilterBank();

        float[] spectrum =
                new float[DspConstants.FFT_BINS];

        float[] mel =
                bank.apply(spectrum);

        for (float v : mel) {

            assertFalse(Float.isNaN(v));
            assertFalse(Float.isInfinite(v));

        }

    }

}
