package edu.iastate.cs311;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The CharacterSeparator class analyzes bitmap images of text to identify 
 * whitespace rows and columns that likely represent separations between characters or words.
 * 
 * It uses a graph-based approach, where each pixel is treated as a vertex in a weighted graph.
 * Dijkstra's algorithm is applied horizontally and vertically to detect continuous paths of white pixels,
 * which are interpreted as separation lines. 
 * 
 * The class also includes functionality to visualize the detected separations by drawing colored lines
 * on the original image for easier debugging and verification.
 * 
 * Primary methods:
 * - findSeparationWeighted(String path): Detects horizontal and vertical whitespace regions.
 * - visualizeSeparations(String inputPath): Overlays visual markers on detected whitespace regions and saves the modified image.
 * 
 * Dependencies:
 * - BitmapProcessor: Handles image loading, pixel matrix extraction, and saving.
 * - WeightedAdjacencyList: Graph structure supporting weighted edges and Dijkstra’s algorithm.
 * - Pair: Utility class for handling coordinate pairs.
 * 
 * @author martin
 */
public class CharacterSeparator {
    final static int WHITESPACE = 0xFFFFFFFF; // RGB value representing white pixels

    /**
     * This method uses the WeightedAdjacencyList class to identify the space between characters in an image of text.
     * For efficiency, it should only construct a single graph object and should only make a constant
     * number of calls to Dijkstra's algorithm.
     * @param path The location of the image on disk.
     * @return Two lists of Integer. The first list indicates whitespace rows. The second list indicates whitespace columns. Returns null if some error occurred loading the image.
     */
    public static Pair<List<Integer>, List<Integer>> findSeparationWeighted(String path) {
        BitmapProcessor bmp;
        try {
            bmp = new BitmapProcessor(path); // Load the bitmap image.
        } catch (IOException e) {
            return null; // If loading fails, return null.
        }
        int[][] rgbMatrix = bmp.getRGBMatrix(); // Get RGB values of the image pixels.
        int rows = rgbMatrix.length;
        int cols = rgbMatrix[0].length;
        ArrayList<Pair<Integer, Integer>> pixels = new ArrayList<>();

        // Add all valid pixel coordinates to the list.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                pixels.add(new Pair<>(i, j));
            }
        }

        // Add dummy nodes to represent starting points for Dijkstra.
        pixels.add(new Pair<>(-1, -1));
        pixels.add(new Pair<>(-2, -2));

        // Create the graph with all pixels (and dummies) as vertices.
        WeightedAdjacencyList<Pair<Integer, Integer>> graph = new WeightedAdjacencyList<>(pixels);

        // Add edges between adjacent pixels with appropriate weights.
        for (Pair<Integer, Integer> pixel: graph.getVertices()) {
            int i = pixel.getFirst();
            int j = pixel.getSecond();

            // Skip out-of-bounds or dummy node pixels.
            if (i < 0 || i >= rows || j < 0 || j >= cols) continue;

            // LEFT
            if (j - 1 >= 0) {
                Pair<Integer, Integer> left = new Pair<>(i, j - 1);
                graph.addEdge(pixel, left, getWeight(pixel, left, rgbMatrix));
            }
            // RIGHT
            if (j + 1 < cols) {
                Pair<Integer, Integer> right = new Pair<>(i, j + 1);
                graph.addEdge(pixel, right, getWeight(pixel, right, rgbMatrix));
            }
            // UP
            if (i - 1 >= 0) {
                Pair<Integer, Integer> up = new Pair<>(i - 1, j);
                graph.addEdge(pixel, up, getWeight(pixel, up, rgbMatrix));
            }
            // DOWN
            if (i + 1 < rows) {
                Pair<Integer, Integer> down = new Pair<>(i + 1, j);
                graph.addEdge(pixel, down, getWeight(pixel, down, rgbMatrix));
            }

            // Connect dummy horizontal start node to left-edge pixels.
            if (j == 0) {
                graph.addEdge(new Pair<>(-1, -1), pixel, 0);
            }
            // Connect dummy vertical start node to top-edge pixels.
            if (i == 0) {
                graph.addEdge(new Pair<>(-2, -2), pixel, 0);
            }
        }

        // Run Dijkstra from dummy left node to all other nodes (horizontal pass).
        HashMap<Pair<Integer, Integer>, Long> horizontal = (HashMap)graph.getShortestPaths(new Pair<>(-1, -1));
        // Run Dijkstra from dummy top node to all other nodes (vertical pass).
        HashMap<Pair<Integer, Integer>, Long> vertical = (HashMap)graph.getShortestPaths(new Pair<>(-2, -2));
        
        // Check for rows that have uninterrupted white pixels across.
        ArrayList<Integer> whitespaceRows = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            if(horizontal.get(Pair.create(i, cols-1)) == cols - 1) {
                whitespaceRows.add(i);
            }
        }

        // Check for columns that have uninterrupted white pixels top-to-bottom.
        ArrayList<Integer> whitespaceCols = new ArrayList<>();
        for (int j = 0; j < cols; j++) {
            if(vertical.get(Pair.create(rows-1, j)) == rows - 1) {
                whitespaceCols.add(j);
            }
        }

        return new Pair<>(whitespaceRows, whitespaceCols);        
    }

    /**
     * Assigns edge weights based on whether both pixels are white (cheap) or not (expensive).
     */
    private static int getWeight(Pair<Integer, Integer> a, Pair<Integer, Integer> b, int[][] rgbMatrix) {
        int aColour = rgbMatrix[a.getFirst()][a.getSecond()];
        int bColour = rgbMatrix[b.getFirst()][b.getSecond()];
        if (aColour == WHITESPACE && bColour == WHITESPACE) {
            return 1; // low cost for white-to-white.
        }
        return 100; // high cost otherwise.
    }


    // TESTING! Code from HW4 Tests on piazza by Vedant Pungliya.
    public static void visualizeSeparations(String inputPath) throws IOException{
        try {
            System.out.println("Loading image from: " + inputPath);

            Pair<List<Integer>, List<Integer>> separations = findSeparationWeighted(inputPath);
            List<Integer> rowSeps = separations.getFirst();
            List<Integer> colSeps = separations.getSecond();

            BitmapProcessor processor = new BitmapProcessor(inputPath);
            BufferedImage image = processor.bi;
            int width = image.getWidth();
            int height = image.getHeight();

            System.out.println("Found " + rowSeps.size() + " row separations");
            System.out.println("Found " + colSeps.size() + " column separations");

            for (Integer row : rowSeps) {
                for (int x = 0; x < width; x++) {
                    image.setRGB(x, row, Color.RED.getRGB());
                }
            }

            for (Integer col : colSeps) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(col, y, Color.GREEN.getRGB());
                }
            }

            processor.writeToFile();
            System.out.println("Saving processed image as: " + inputPath + ".new.bmp");

        } catch (IOException e) {
            System.out.println("Error processing image: " + e.getMessage());
        }
    }
}