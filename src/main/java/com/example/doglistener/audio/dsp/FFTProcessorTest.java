package com.example.doglistener.audio.dsp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FFTProcessorTest {

    @Test
    void producesCorrectNumberOfBins() {

        FFTProcessor fft =
                new FFTProcessor();

        float[] frame =
                new float[DspConstants.WINDOW_SIZE];

        float[] spectrum =
                fft.powerSpectrum(frame);

        assertEquals(
                DspConstants.FFT_BINS,
                spectrum.length);

    }

    @Test
    void zeroSignalProducesZeroSpectrum() {

        FFTProcessor fft =
                new FFTProcessor();

        float[] frame =
                new float[DspConstants.WINDOW_SIZE];

        float[] spectrum =
                fft.powerSpectrum(frame);

        for (float value : spectrum) {

            assertEquals(
                    0.0f,
                    value,
                    1e-6);

        }

    }

}
