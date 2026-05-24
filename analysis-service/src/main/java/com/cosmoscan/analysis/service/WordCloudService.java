package com.cosmoscan.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class WordCloudService {

    @Value("${app.wordcloud.output-dir:./data/wordclouds}")
    private String outputDir;

    public String generateWordCloud(String reportId, String text) {
        try {
            Path outputPath = Paths.get(outputDir);
            Files.createDirectories(outputPath);

            BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 800, 600);
            
            String[] words = text.toLowerCase().split("\\W+");
            Map<String, Integer> wordCount = new HashMap<>();
            for (String word : words) {
                if (word.length() > 2) wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
            
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wordCount.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            int x = 30, y = 50;
            Random r = new Random(42);
            for (Map.Entry<String, Integer> e : sorted.stream().limit(25).toList()) {
                int fs = Math.min(12 + e.getValue() * 4, 50);
                g2d.setFont(new Font("Arial", Font.BOLD, fs));
                g2d.setColor(new Color(r.nextInt(200), r.nextInt(200), r.nextInt(200)));
                g2d.drawString(e.getKey(), x, y);
                x += e.getKey().length() * fs / 2 + 15;
                if (x > 700) { x = 30; y += fs + 15; }
            }
            g2d.dispose();
            
            String filename = reportId + ".png";
            Path filePath = outputPath.resolve(filename);
            ImageIO.write(image, "PNG", filePath.toFile());
            log.info("Word cloud generated: {}", filePath);
            return filePath.toString();
        } catch (Exception e) {
            log.error("Failed to generate word cloud", e);
            return null;
        }
    }

    public byte[] getWordCloudImage(String reportId) {
        Path filePath = Paths.get(outputDir).resolve(reportId + ".png");
        try {
            return Files.exists(filePath) ? Files.readAllBytes(filePath) : new byte[0];
        } catch (IOException e) {
            return new byte[0];
        }
    }
}