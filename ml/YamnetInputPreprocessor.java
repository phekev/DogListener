package com.example.doglistener.ml;

import com.example.doglistener.audio.dsp.MelSpectrogramExtractor;
import org.springframework.stereotype.Component;

@Component
public class YamnetInputPreprocessor {

    private static final int MEL_FRAME_COUNT = 96;
    private static final int MEL_BIN_COUNT = 64;

    private final WaveformPreprocessor waveformPreprocessor;
    private final MelSpectrogramExtractor melSpectrogramExtractor;

    public YamnetInputPreprocessor(
            WaveformPreprocessor waveformPreprocessor,
            MelSpectrogramExtractor melSpectrogramExtractor
    ) {
        this.waveformPreprocessor = waveformPreprocessor;
        this.melSpectrogramExtractor =
                melSpectrogramExtractor;
    }

    public float[][][][] prepare(float[] audioSamples) {
        if (audioSamples == null) {
            throw new IllegalArgumentException(
                    "Audio samples must not be null."
            );
        }

        float[] preparedWaveform =
                waveformPreprocessor.prepare(audioSamples);

        float[][] melSpectrogram =
                melSpectrogramExtractor.extract(
                        preparedWaveform
                );

        validateMelSpectrogram(melSpectrogram);

        return createModelInput(melSpectrogram);
    }

    private float[][][][] createModelInput(
            float[][] melSpectrogram
    ) {
        float[][][][] modelInput =
                new float[1][1]
                        [MEL_FRAME_COUNT]
                        [MEL_BIN_COUNT];

        for (
                int frame = 0;
                frame < MEL_FRAME_COUNT;
                frame++
        ) {
            System.arraycopy(
                    melSpectrogram[frame],
                    0,
                    modelInput[0][0][frame],
                    0,
                    MEL_BIN_COUNT
            );
        }

        return modelInput;
    }

    private void validateMelSpectrogram(
            float[][] melSpectrogram
    ) {
        if (melSpectrogram == null) {
            throw new IllegalArgumentException(
                    "Mel spectrogram must not be null."
            );
        }

        if (melSpectrogram.length
                != MEL_FRAME_COUNT) {

            throw new IllegalArgumentException(
                    "Expected "
                            + MEL_FRAME_COUNT
                            + " mel frames, received "
                            + melSpectrogram.length
            );
        }

        for (
                int frame = 0;
                frame < melSpectrogram.length;
                frame++
        ) {
            float[] melBins =
                    melSpectrogram[frame];

            if (melBins == null
                    || melBins.length
                    != MEL_BIN_COUNT) {

                throw new IllegalArgumentException(
                        "Expected "
                                + MEL_BIN_COUNT
                                + " mel bins at frame "
                                + frame
                                + ", received "
                                + (
                                melBins == null
                                        ? "null"
                                        : melBins.length
                        )
                );
            }
        }
    }
}