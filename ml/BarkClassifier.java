package com.example.doglistener.ml;

public interface BarkClassifier {

    Prediction predict(float[] samples) throws Exception;

}
