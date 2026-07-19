package com.example.doglistener.audio.dsp;

/**
 * Generates and applies a Mel filter bank.
 */
public class MelFilterBank {

    private final float[][] filters;

    public MelFilterBank() {
        filters = createFilters();
    }

    /**
     * Converts a power spectrum into Mel energies.
     *
     * @param powerSpectrum 257 FFT bins.
     * @return 64 Mel energies.
     */
    public float[] apply(float[] powerSpectrum) {

        if (powerSpectrum.length != DspConstants.FFT_BINS) {
            throw new IllegalArgumentException(
                    "Expected "
                            + DspConstants.FFT_BINS
                            + " FFT bins.");
        }

        float[] mel = new float[DspConstants.MEL_BINS];

        for (int m = 0; m < DspConstants.MEL_BINS; m++) {

            float sum = 0.0f;

            for (int k = 0; k < DspConstants.FFT_BINS; k++) {

                sum += filters[m][k] * powerSpectrum[k];

            }

            /*
             * YAMNet preprocessing uses
             *
             * log(mel + 0.001)
             */
            mel[m] = (float) Math.log(
                    sum + DspConstants.LOG_OFFSET);

        }

        return mel;

    }

    /**
     * Build the triangular Mel filters.
     */
    private float[][] createFilters() {

        float[][] bank =
                new float[DspConstants.MEL_BINS][DspConstants.FFT_BINS];

        double minMel = hzToMel(DspConstants.MIN_FREQUENCY);
        double maxMel = hzToMel(DspConstants.MAX_FREQUENCY);

        double[] melPoints =
                new double[DspConstants.MEL_BINS + 2];

        for (int i = 0; i < melPoints.length; i++) {

            melPoints[i] =
                    minMel
                            + (maxMel - minMel)
                            * i
                            / (melPoints.length - 1);

        }

        double[] hzPoints =
                new double[melPoints.length];

        for (int i = 0; i < melPoints.length; i++) {

            hzPoints[i] = melToHz(melPoints[i]);

        }

        int[] bins =
                new int[hzPoints.length];

        for (int i = 0; i < hzPoints.length; i++) {

            bins[i] = (int) Math.floor(

                    (DspConstants.FFT_SIZE + 1)
                            * hzPoints[i]
                            / DspConstants.SAMPLE_RATE);

        }

        for (int m = 1; m <= DspConstants.MEL_BINS; m++) {

            int left = bins[m - 1];
            int center = bins[m];
            int right = bins[m + 1];

            for (int k = left; k < center; k++) {

                if (k >= 0 && k < DspConstants.FFT_BINS) {

                    bank[m - 1][k] =
                            (float) (k - left)
                                    / (center - left);

                }

            }

            for (int k = center; k < right; k++) {

                if (k >= 0 && k < DspConstants.FFT_BINS) {

                    bank[m - 1][k] =
                            (float) (right - k)
                                    / (right - center);

                }

            }

        }

        return bank;

    }

    private double hzToMel(double hz) {

        return 2595.0
                * Math.log10(1.0 + hz / 700.0);

    }

    private double melToHz(double mel) {

        return 700.0
                * (Math.pow(10.0, mel / 2595.0) - 1.0);

    }

}
