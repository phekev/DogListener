package com.example.doglistener.ml;

public record Prediction(
        int classId,
        String label,
        float confidence) {

    public boolean isDogBark() {

        String l = label.toLowerCase();

        return l.contains("dog")
                || l.contains("bark")
                || l.contains("growling")
                || l.contains("howl");
    }

}
