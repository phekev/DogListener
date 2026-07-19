package com.example.doglistener.audio.dsp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MelSpectrogramExtractorSineTest {

    @Test
    void extractsSpectrogramFromOneKHzTone() {

        MelSpectrogramExtractor extractor =
                new MelSpectrogramExtractor();

        float[] samples =
                generateSineWave(
                        1000,
                        DspConstants.SAMPLE_RATE,
                        DspConstants.PATCH_SAMPLES);

        float[][] spectrogram =
                extractor.extract(samples);


        // Shape checks

        assertEquals(
                DspConstants.FRAMES,
                spectrogram.length);

        assertEquals(
                DspConstants.MEL_BINS,
                spectrogram[0].length);


        // Check values

        float energy = 0.0f;

        for (float[] frame : spectrogram) {

            for (float value : frame) {

                assertFalse(Float.isNaN(value));
                assertFalse(Float.isInfinite(value));

                energy += Math.abs(value);
            }
        }


        // A sine wave should not produce silence

        assertTrue(
                energy > 0.0f,
                "Spectrogram contains no energy");

    }


    private float[] generateSineWave(
            double frequency,
            int sampleRate,
            int samples) {

        float[] output =
                new float[samples];


        for (int i = 0; i < samples; i++) {

            output[i] =
                    (float) Math.sin(
                            2.0
                            * Math.PI
                            * frequency
                            * i
                            / sampleRate);

        }

        return output;
    }

}
