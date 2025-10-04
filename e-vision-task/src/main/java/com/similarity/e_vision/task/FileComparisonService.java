package com.similarity.e_vision.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileComparisonService {

    @Value("${pool.directory.path}")
    private String poolDirectoryPath;

    public Map<String, Double> compareFiles(Path uploadedFile) throws IOException {
        Set<String> uploadedWords = extractWords(Files.readString(uploadedFile));
        Map<String, Double> scores = new HashMap<>();

        try (Stream<Path> paths = Files.list(Paths.get(poolDirectoryPath))) {
            paths.forEach(path -> {
                try {
                    Set<String> poolFileWords = extractWords(Files.readString(path));
                    double score = calculateSimilarityScore(uploadedWords, poolFileWords);
                    scores.put(path.getFileName().toString(), score);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return scores;
    }

    private Set<String> extractWords(String content) {
        return Set.of(content.toLowerCase().split("\\W+"))
                .stream()
                .filter(word -> word.matches("[a-zA-Z]+"))
                .collect(Collectors.toSet());
    }

    private double calculateSimilarityScore(Set<String> fileWords, Set<String> poolFileWords) {
        Set<String> intersection = new HashSet<>(fileWords);
        intersection.retainAll(poolFileWords);
        return ((double) intersection.size() / fileWords.size()) * 100;
    }
}