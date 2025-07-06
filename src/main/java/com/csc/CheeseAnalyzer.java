package com.csc;

import java.io.*;
import java.util.*;

public class CheeseAnalyzer {

    public static void main(String[] args) {
        String inputFile = "cheese.csv";
        String outputFile = "output.txt";

        int pasteurized = 0;
        int raw = 0;
        int organicMoist = 0;
        int totalMoisture = 0;
        int moistureCount = 0;

        Map<String, Integer> milkTypeCount = new HashMap<>();
        Set<Integer> cheeseIds = new HashSet<>();
        int lacticCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);

                try {
                    int id = Integer.parseInt(data[0]);
                    cheeseIds.add(id);

                    String milkTreatment = data[5].trim().toLowerCase();
                    if (milkTreatment.contains("pasteurized")) pasteurized++;
                    else if (milkTreatment.contains("raw")) raw++;

                    int organic = data[8].isEmpty() ? 0 : Integer.parseInt(data[8]);
                    double moisture = data[9].isEmpty() ? -1 : Double.parseDouble(data[9]);
                    if (organic == 1 && moisture > 41.0) organicMoist++;
                    if (moisture >= 0) {
                        totalMoisture += moisture;
                        moistureCount++;
                    }

                    String milkType = data[4].trim().toLowerCase();
                    milkTypeCount.put(milkType, milkTypeCount.getOrDefault(milkType, 0) + 1);

                    if (data.length > 12) {
                        String flavour = data[12].toLowerCase();
                        if (flavour.contains("lactic")) lacticCount++;
                    }

                } catch (Exception e) {
                }
            }

            String mostCommonMilk = milkTypeCount.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Unknown");

            double avgMoisture = moistureCount > 0 ? (double) totalMoisture / moistureCount : 0;

            try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
                out.println("Pasteurized cheeses: " + pasteurized);
                out.println("Raw milk cheeses: " + raw);
                out.println("Organic cheeses with moisture > 41%: " + organicMoist);
                out.println("Most common milk type: " + mostCommonMilk);
                out.printf("Average moisture percent: %.2f\n", avgMoisture);
                out.println("Cheeses described as lactic: " + lacticCount);
            }

            writeMissingIDs(cheeseIds, "missing_ids.txt");
            writeWithoutHeaders(inputFile, "cheese_without_headers.csv");
            writeWithoutIds(inputFile, "cheese_without_ids.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private static void writeMissingIDs(Set<Integer> ids, String fileName) {
        List<Integer> sortedIds = new ArrayList<>(ids);
        Collections.sort(sortedIds);
        List<Integer> missing = new ArrayList<>();

        for (int i = sortedIds.get(0); i <= sortedIds.get(sortedIds.size() - 1); i++) {
            if (!ids.contains(i)) {
                missing.add(i);
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            for (int id : missing) {
                out.println(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeWithoutHeaders(String inputFile, String outputFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeWithoutIds(String inputFile, String outputFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    pw.println(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
