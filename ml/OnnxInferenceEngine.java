package com.example.doglistener.ml;

import ai.onnxruntime.*;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.util.Locale;


@Component
public class OnnxInferenceEngine implements InferenceEngine {

 private final YamnetPredictionInterpreter predictionInterpreter;
private final OnnxModelRunner modelRunner;
private final YamnetInputPreprocessor            inputPreprocessor;
private final List<String> labels = new ArrayList<>();

    public OnnxInferenceEngine(
            YamnetInputPreprocessor inputPreprocessor,
            OnnxModelRunner modelRunner,
            YamnetPredictionInterpreter predictionInterpreter
    ) {
        this.inputPreprocessor = inputPreprocessor;
        this.modelRunner = modelRunner;
        this.predictionInterpreter =
                predictionInterpreter;
    }


@PostConstruct
public void initialize() throws Exception {
    loadClassMap();
    printBarkRelatedLabels();

    System.out.println("Class map loaded.");
}

    @Override
    public Prediction predict(float[] audio)
            throws Exception {

        float[][][][] modelInput =
                inputPreprocessor.prepare(audio);

        float[] logits =
                modelRunner.run(modelInput);

        return predictionInterpreter.interpret(
                logits,
                labels
        );
    }

private void loadClassMap() throws Exception {

    InputStream inputStream = getClass()
            .getClassLoader()
            .getResourceAsStream("model/yamnet_class_map.csv");

    if (inputStream == null) {
        throw new IllegalStateException(
                "Could not find model/yamnet_class_map.csv"
        );
    }

    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(inputStream))) {

        // Skip the header row
        reader.readLine();

        String line;

        while ((line = reader.readLine()) != null) {

            List<String> parts = parseCsvLine(line);

            if (parts.size() < 3) {
                throw new IllegalStateException(
                        "Malformed class-map row: " + line
                );
            }

            labels.add(parts.get(2));
        }
    }
}

private static List<String> parseCsvLine(String line) {

    List<String> fields = new ArrayList<>();
    StringBuilder current = new StringBuilder();

    boolean insideQuotes = false;

    for (int i = 0; i < line.length(); i++) {

        char character = line.charAt(i);

        if (character == '"') {

            if (insideQuotes
                    && i + 1 < line.length()
                    && line.charAt(i + 1) == '"') {

                current.append('"');
                i++;

            } else {
                insideQuotes = !insideQuotes;
            }

        } else if (character == ',' && !insideQuotes) {

            fields.add(current.toString().trim());
            current.setLength(0);

        } else {
            current.append(character);
        }
    }

    fields.add(current.toString().trim());

    return fields;
}

private void printBarkRelatedLabels() {
    System.out.println("========== Bark-related labels ==========");

    for (int i = 0; i < labels.size(); i++) {
        String label = labels.get(i);
        String normalized = label.toLowerCase(Locale.ROOT);

        if (normalized.contains("dog")
                || normalized.contains("bark")
                || normalized.contains("bow-wow")
                || normalized.contains("yip")
                || normalized.contains("howl")
                || normalized.contains("growl")
                || normalized.contains("whimper")) {

            System.out.printf(
                    "%d: %s%n",
                    i,
                    label
            );
        }
    }

    System.out.println("=========================================");
}

}
