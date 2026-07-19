package com.example.doglistener.audio.dsp;

import org.springframework.stereotype.Component;

/**
 * Converts PCM samples into the 96×64 log-Mel spectrogram
 * expected by the Qualcomm YAMNet model.
 */
@Component
public class MelSpectrogramExtractor {

    private final FFTProcessor fftProcessor;
    private final MelFilterBank melFilterBank;

    private final float[] window;

    public MelSpectrogramExtractor() {

        this.fftProcessor = new FFTProcessor();
        this.melFilterBank = new MelFilterBank();

        this.window =
                WindowFunctions.hann(
                        DspConstants.WINDOW_SIZE);

    }

    /**
     * Extract a log-Mel spectrogram.
     *
     * @param samples 15600 normalized samples.
     * @return 96 × 64 log-Mel spectrogram.
     */
    public float[][] extract(float[] samples) {

        if (samples.length != DspConstants.PATCH_SAMPLES) {

            throw new IllegalArgumentException(
                    "Expected "
                            + DspConstants.PATCH_SAMPLES
                            + " samples.");

        }

        float[][] spectrogram =
                new float[DspConstants.FRAMES]
                        [DspConstants.MEL_BINS];

        for (int frame = 0;
             frame < DspConstants.FRAMES;
             frame++) {

            int offset =
                    frame * DspConstants.HOP_SIZE;

            float[] frameSamples =
                    new float[DspConstants.WINDOW_SIZE];

            System.arraycopy(
                    samples,
                    offset,
                    frameSamples,
                    0,
                    DspConstants.WINDOW_SIZE);

            WindowFunctions.apply(
                    frameSamples,
                    window);

            float[] spectrum =
                    fftProcessor.powerSpectrum(
                            frameSamples);

            spectrogram[frame] =
                    melFilterBank.apply(
                            spectrum);

        }

        return spectrogram;

    }

}
