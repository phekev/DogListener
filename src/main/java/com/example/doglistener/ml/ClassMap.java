package com.example.doglistener.ml;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClassMap {

    private final List<String> labels =
            new ArrayList<>();

    public ClassMap() throws Exception {

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     getClass()
                                             .getClassLoader()
                                             .getResourceAsStream(
                                                     "model/yamnet_class_map.csv")))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts =
                        line.split(",");

                labels.add(parts[2]);

            }

        }

    }

    public String label(int index) {

        return labels.get(index);

    }

}
