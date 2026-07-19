package com.example.doglistener.audio.dsp;

import org.jtransforms.fft.FloatFFT_1D;


/**
 * Computes the power spectrum of a single audio frame.
 */
public class FFTProcessor {

    private final FloatFFT_1D fft =
            new FloatFFT_1D(DspConstants.FFT_SIZE);

    /**
     * Computes the power spectrum.
     *
     * @param frame Windowed audio frame (400 samples)
     * @return Power spectrum (257 bins)
     */
    public float[] powerSpectrum(float[] frame) {

        if (frame.length != DspConstants.WINDOW_SIZE) {

            throw new IllegalArgumentException(
                    "Expected "
                            + DspConstants.WINDOW_SIZE
                            + " samples.");

        }

        /*
         * JTransforms works in-place.
         *
         * Create a zero-padded FFT buffer.
         */
        float[] fftData =
                new float[DspConstants.FFT_SIZE];

        System.arraycopy(
                frame,
                0,
                fftData,
                0,
                frame.length);

        /*
         * Real forward FFT.
         */
        fft.realForward(fftData);

        float[] power =
                new float[DspConstants.FFT_BINS];

        /*
         * DC component
         */
        power[0] = fftData[0] * fftData[0];

        /*
         * Interior bins
         */
        for (int k = 1; k < DspConstants.FFT_BINS - 1; k++) {

            float real = fftData[2 * k];
            float imag = fftData[2 * k + 1];

            power[k] =
                    real * real
                            + imag * imag;

        }

        /*
         * Nyquist
         */
        float nyquist =
                fftData[1];

        power[DspConstants.FFT_BINS - 1] =
                nyquist * nyquist;

        return power;

    }

}
