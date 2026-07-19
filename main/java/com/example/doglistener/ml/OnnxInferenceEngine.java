package com.example.doglistener.ml;

import ai.onnxruntime.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.example.doglistener.audio.dsp.MelSpectrogramExtractor;
import com.example.doglistener.audio.dsp.DspConstants;

@Component
public class OnnxInferenceEngine implements InferenceEngine {

    private final MelSpectrogramExtractor spectrogramExtractor;

    private OrtEnvironment environment;
    private OrtSession session;

    private final List<String> labels = new ArrayList<>();

    public OnnxInferenceEngine(MelSpectrogramExtractor spectrogramExtractor) {
    this.spectrogramExtractor = spectrogramExtractor;
}


@PostConstruct
public void initialize() throws Exception {

    System.out.println("Initializing ONNX engine...");

    environment = OrtEnvironment.getEnvironment();

    Path model = extractModel();

    System.out.println("Model extracted to " + model);

    session = environment.createSession(
            model.toString(),
            new OrtSession.SessionOptions());

    System.out.println("Session created.");

    loadClassMap();

    System.out.println("Class map loaded.");

    printModelInformation();
}

private Path extractModel() throws IOException {

    Path tempDir = Files.createTempDirectory("doglistener-");

    copyResource(
            "model/yamnet.onnx",
            tempDir.resolve("yamnet.onnx"));

    copyResource(
            "model/yamnet.data",
            tempDir.resolve("yamnet.data"));

    tempDir.toFile().deleteOnExit();

    return tempDir.resolve("yamnet.onnx");
}

private void copyResource(String resource, Path destination)
        throws IOException {

    try (InputStream in =
                 getClass()
                         .getClassLoader()
                         .getResourceAsStream(resource)) {

        if (in == null) {

            throw new FileNotFoundException(resource);

        }

        Files.copy(
                in,
                destination,
                StandardCopyOption.REPLACE_EXISTING);

    }

    destination.toFile().deleteOnExit();

}

   @Override
public Prediction predict(float[] audio) throws Exception {

    float[][] mel = spectrogramExtractor.extract(audio);

    float[][][][] input = new float[1][1][96][64];

    for  (int frame = 0; frame < DspConstants.FRAMES; frame++) {
        System.arraycopy(
        mel[frame],
        0,
        input[0][0][frame],
        0,
        DspConstants.MEL_BINS);
    }

    try (OnnxTensor tensor =
                 OnnxTensor.createTensor(environment, input);

         OrtSession.Result result =
                 session.run(
                         Collections.singletonMap(
                                 "audio",
                                 tensor))) {

        float[][] scores =
                (float[][]) result
                        .get("class_scores")
                        .orElseThrow()
                        .getValue();

        int bestIndex = 0;
        float bestScore = scores[0][0];

        for (int i = 1; i < scores[0].length; i++) {

            if (scores[0][i] > bestScore) {

                bestScore = scores[0][i];
                bestIndex = i;

            }
        }

        return new Prediction(
                bestIndex,
                labels.get(bestIndex),
                bestScore);
    }
}
  private void loadClassMap() throws Exception {

    try (BufferedReader reader =
                 new BufferedReader(
                         new InputStreamReader(
                                 getClass()
                                         .getClassLoader()
                                         .getResourceAsStream(
                                                 "model/yamnet_class_map.csv")))) {

        // Skip the header row
        reader.readLine();

        String line;

        while ((line = reader.readLine()) != null) {

            String[] parts = line.split(",");

            labels.add(parts[2]);

        }

    }

}

private void printModelInformation() throws OrtException {

    System.out.println();
    System.out.println("========== YAMNet Model ==========");

    System.out.println("Inputs");

    for (var entry : session.getInputInfo().entrySet()) {

        String name = entry.getKey();
        NodeInfo info = entry.getValue();

        System.out.println("  " + name);

        if (info.getInfo() instanceof TensorInfo tensorInfo) {

            System.out.println("    Type  : " + tensorInfo.type);

            System.out.print("    Shape : ");

            for (long dim : tensorInfo.getShape()) {
                System.out.print(dim + " ");
            }

            System.out.println();
        }
    }

    System.out.println();
    System.out.println("Outputs");

    for (var entry : session.getOutputInfo().entrySet()) {

        String name = entry.getKey();
        NodeInfo info = entry.getValue();

        System.out.println("  " + name);

        if (info.getInfo() instanceof TensorInfo tensorInfo) {

            System.out.println("    Type  : " + tensorInfo.type);

            System.out.print("    Shape : ");

            for (long dim : tensorInfo.getShape()) {
                System.out.print(dim + " ");
            }

            System.out.println();
        }
    }

    System.out.println("==============================");
}
    @PreDestroy
    public void shutdown() throws Exception {

        if (session != null)
            session.close();

        if (environment != null)
            environment.close();

    }
}
