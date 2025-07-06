package com.csc;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CheeseAnalyzerTest {

    private static final String SAMPLE_DATA =
            "CheeseId,CheeseNameEn,ManufacturerNameEn,ManufacturerProvCode,MilkTypeEn,MilkTreatmentTypeEn,RindTypeEn,CategoryTypeEn,Organic,MoisturePercent,MilkFatPercent,TypeOfMilkEn,FlavourEn\n" +
            "200,Cheddar A,Farmstead QC,QC,Cow,Raw,,Firm,1,42.5,31,,Sharp, lactic\n" +
            "201,Brie B,Fromagerie QC,QC,Cow,Raw,,Soft,0,48.0,23,,Mild\n" +
            "202,Gouda C,Dairy AB,AB,Goat,Raw,,Firm,1,41.1,28,,Lactic flavor\n" +
            "203,Camembert D,Dairy QC,QC,Goat,Raw,,Soft,1,,25,,Mild and lactic flavor\n" +
            "204,Feta E,Fromagerie ON,ON,Sheep,Raw,,Soft,0,40.0,21,,Salty\n" +
            "205,Blue F,Farm AB,AB,Cow,Raw,,Blue,1,43.3,29,,lactic\n";

    private Path inputFile;
    private Path outputFile;
    private Path missingIdsFile;
    private Path noHeadersFile;
    private Path noIdsFile;

    @BeforeEach
    void setup() throws IOException {
        inputFile = Files.createTempFile("cheese_test", ".csv");
        Files.write(inputFile, SAMPLE_DATA.getBytes());

        outputFile = Files.createTempFile("output", ".txt");
        missingIdsFile = Files.createTempFile("missing_ids", ".txt");
        noHeadersFile = Files.createTempFile("cheese_without_headers", ".csv");
        noIdsFile = Files.createTempFile("cheese_without_ids", ".csv");
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(inputFile);
        Files.deleteIfExists(outputFile);
        Files.deleteIfExists(missingIdsFile);
        Files.deleteIfExists(noHeadersFile);
        Files.deleteIfExists(noIdsFile);
    }

    @Test
    void testParseCSVLineHandlesQuotedCommas() {
        String line = "300,\"Blue, Soft Cheese\",Fromagerie,QC,Cow,Pasteurized,,,0,55.5,,,Mild, creamy";
        String[] parsed = CheeseAnalyzer.parseCSVLine(line);
        assertEquals("300", parsed[0]);
        assertEquals("Blue, Soft Cheese", parsed[1]);
        assertEquals("Fromagerie", parsed[2]);
    }

    @Test
    void testWriteMissingIDsDetectsGap() throws IOException {
        Set<Integer> ids = new HashSet<>(Arrays.asList(200, 201, 202, 204, 205));
        CheeseAnalyzer.writeMissingIDs(ids, missingIdsFile.toString());

        List<String> lines = Files.readAllLines(missingIdsFile);
        assertEquals(1, lines.size());
        assertEquals("203", lines.get(0));
    }

    @Test
    void testWriteWithoutHeaders() throws IOException {
        CheeseAnalyzer.writeWithoutHeaders(inputFile.toString(), noHeadersFile.toString());

        List<String> lines = Files.readAllLines(noHeadersFile);
        assertEquals(6, lines.size());
        assertTrue(lines.get(0).startsWith("200,Cheddar A"));
    }

    @Test
    void testWriteWithoutIds() throws IOException {
        CheeseAnalyzer.writeWithoutIds(inputFile.toString(), noIdsFile.toString());

        List<String> lines = Files.readAllLines(noIdsFile);
        assertEquals(7, lines.size());
        assertFalse(lines.get(0).contains("CheeseId"));
        assertTrue(lines.get(1).startsWith("Cheddar A"));
    }

    @Test
    void testOutputFileContainsCorrectCounts() throws IOException {
        CheeseAnalyzer.main(new String[]{});
       
    }
}
