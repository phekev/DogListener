package com.example.doglistener.ml;

public interface InferenceEngine {

    Prediction predict(float[] audio) throws Exception;

}
