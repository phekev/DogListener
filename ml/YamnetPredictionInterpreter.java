package com.example.doglistener.ml;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class YamnetPredictionInterpreter {

    private static final int COMPOSITE_CLASS_ID = -1;
    private static final String COMPOSITE_LABEL = "Dog Bark";

    private static final float DOG_WEIGHT = 0.5f;
    private static final float BARK_WEIGHT = 0.3f;
    private static final float BOW_WOW_WEIGHT = 0.1f;
    private static final float YIP_WEIGHT = 0.1f;

    public Prediction interpret(
            float[] logits,
            List<String> labels
    ) {
        validateScores(logits);

        logBarkRelatedScores(logits, labels);

        Prediction overall =
                selectBestPrediction(logits, labels);

        Prediction bark =
                computeBarkPrediction(logits, labels);

        System.out.printf(
                "Overall: %s = %.3f%n",
                overall.label(),
                overall.confidence()
        );

        System.out.printf(
                "Weighted bark prediction: %s = %.3f%n",
                bark.label(),
                bark.confidence()
        );

        return bark;
    }

    private Prediction computeBarkPrediction(
            float[] logits,
            List<String> labels
    ) {
        float dog = 0.0f;
        float bark = 0.0f;
        float bowWow = 0.0f;
        float yip = 0.0f;

        int scoreCount = Math.min(
                logits.length,
                labels.size()
        );

        for (int index = 0; index < scoreCount; index++) {
            String label = labels.get(index);
            float probability = sigmoid(logits[index]);

            switch (label) {
                case "Dog" -> dog = probability;
                case "Bark" -> bark = probability;
                case "Bow-wow" -> bowWow = probability;
                case "Yip" -> yip = probability;
                default -> {
                    // Not used in the weighted bark score.
                }
            }
        }

        float confidence =
                dog * DOG_WEIGHT
                        + bark * BARK_WEIGHT
                        + bowWow * BOW_WOW_WEIGHT
                        + yip * YIP_WEIGHT;

        System.out.printf(
                "Weighted Bark Score = %.3f "
                        + "(Dog=%.3f Bark=%.3f "
                        + "Bow-wow=%.3f Yip=%.3f)%n",
                confidence,
                dog,
                bark,
                bowWow,
                yip
        );

        return new Prediction(
                COMPOSITE_CLASS_ID,
                COMPOSITE_LABEL,
                confidence
        );
    }

    private Prediction selectBestPrediction(
            float[] logits,
            List<String> labels
    ) {
        int bestIndex = 0;
        float bestProbability = sigmoid(logits[0]);

        for (int index = 1; index < logits.length; index++) {
            float probability = sigmoid(logits[index]);

            if (probability > bestProbability) {
                bestProbability = probability;
                bestIndex = index;
            }
        }

        String label = bestIndex < labels.size()
                ? labels.get(bestIndex)
                : "class-" + bestIndex;

        return new Prediction(
                bestIndex,
                label,
                bestProbability
        );
    }

    private void logBarkRelatedScores(
            float[] logits,
            List<String> labels
    ) {
        System.out.println(
                "----- Bark-related scores -----"
        );

        int scoreCount = Math.min(
                logits.length,
                labels.size()
        );

        for (int index = 0; index < scoreCount; index++) {
            String label = labels.get(index);

            if (!isBarkRelated(label)) {
                continue;
            }

            System.out.printf(
                    "%-12s %.6f%n",
                    label,
                    sigmoid(logits[index])
            );
        }
    }

    private boolean isBarkRelated(String label) {
        if (label == null) {
            return false;
        }

        String normalized = label
                .trim()
                .toLowerCase(Locale.ROOT);

        return normalized.equals("dog")
                || normalized.equals("bark")
                || normalized.equals("yip")
                || normalized.equals("howl")
                || normalized.equals("bow-wow")
                || normalized.equals("growling")
                || normalized.equals("whimper");
    }

    private void validateScores(float[] logits) {
        if (logits == null || logits.length == 0) {
            throw new IllegalArgumentException(
                    "Model returned no class scores."
            );
        }
    }

    private static float sigmoid(float logit) {
        if (logit >= 0.0f) {
            float exponential =
                    (float) Math.exp(-logit);

            return 1.0f / (1.0f + exponential);
        }

        float exponential =
                (float) Math.exp(logit);

        return exponential / (1.0f + exponential);
    }
}
