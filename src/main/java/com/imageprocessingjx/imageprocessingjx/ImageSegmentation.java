package com.imageprocessingjx.imageprocessingjx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageSegmentation {
    private int[][] labels;
    private double[][] distance;
    private int width;
    private int height;
    private BufferedImage inputImage;
    private BufferedImage segmentedImage;
    private int numLabels;

    public BufferedImage segmentImage(BufferedImage image) {
        BufferedImage resultImage;

        // Load the input image
        inputImage = image;
        width = inputImage.getWidth();
        height = inputImage.getHeight();

        // Convert the input image to grayscale
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        grayscaleImage.getGraphics().drawImage(inputImage, 0, 0, null);

        // Initialize labels and distance matrices
        labels = new int[width][height];
        distance = new double[width][height];

        // Apply the distance transform
        computeDistanceTransform(grayscaleImage);

        // Perform thresholding and assign labels to segments
        segmentImage();

        // Compute moments for each segment
        computeMoments();

        // Create segmented image
        createSegmentedImage();

        resultImage = segmentedImage;
        return resultImage;
    }

    private void computeDistanceTransform(BufferedImage grayscaleImage) {
        // Compute the distance transform using the squared Euclidean distance
        double[] distances = new double[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                distances[y * width + x] = Double.MAX_VALUE;
                if (grayscaleImage.getRGB(x, y) == Color.WHITE.getRGB()) {
                    distances[y * width + x] = 0;
                }
            }
        }

        for (int i = 1; i < width * height; i++) {
            int x = i % width;
            int y = i / width;

            double minDistance = distances[i];
            if (x > 0 && y > 0) {
                minDistance = Math.min(minDistance, distances[(y - 1) * width + (x - 1)] + Math.sqrt(2));
            }
            if (y > 0) {
                minDistance = Math.min(minDistance, distances[(y - 1) * width + x] + 1);
            }
            if (x < width - 1 && y > 0) {
                minDistance = Math.min(minDistance, distances[(y - 1) * width + (x + 1)] + Math.sqrt(2));
            }
            if (x > 0) {
                minDistance = Math.min(minDistance, distances[y * width + (x - 1)] + 1);
            }

            distances[i] = minDistance;
        }

        // Assign the computed distances to the distance matrix
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                distance[x][y] = distances[y * width + x];
            }
        }
    }

    private void segmentImage() {
        // Threshold the distance matrix to obtain binary image
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (distance[x][y] > 0) {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        // Label the segments using connected component labeling algorithm
        labels = new int[width][height];
        numLabels = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (binaryImage.getRGB(x, y) == Color.WHITE.getRGB() && labels[x][y] == 0) {
                    numLabels++;
                    labels[x][y] = numLabels;
                    labelNeighbors(x, y);
                }
            }
        }
    }

    private void labelNeighbors(int x, int y) {
        if (x > 0 && y > 0 && labels[x - 1][y - 1] == 0 && distance[x - 1][y - 1] > 0) {
            labels[x - 1][y - 1] = labels[x][y];
            labelNeighbors(x - 1, y - 1);
        }
        if (y > 0 && labels[x][y - 1] == 0 && distance[x][y - 1] > 0) {
            labels[x][y - 1] = labels[x][y];
            labelNeighbors(x, y - 1);
        }
        if (x < width - 1 && y > 0 && labels[x + 1][y - 1] == 0 && distance[x + 1][y - 1] > 0) {
            labels[x + 1][y - 1] = labels[x][y];
            labelNeighbors(x + 1, y - 1);
        }
        if (x > 0 && labels[x - 1][y] == 0 && distance[x - 1][y] > 0) {
            labels[x - 1][y] = labels[x][y];
            labelNeighbors(x - 1, y);
        }
        if (x < width - 1 && labels[x + 1][y] == 0 && distance[x + 1][y] > 0) {
            labels[x + 1][y] = labels[x][y];
            labelNeighbors(x + 1, y);
        }
        if (x > 0 && y < height - 1 && labels[x - 1][y + 1] == 0 && distance[x - 1][y + 1] > 0) {
            labels[x - 1][y + 1] = labels[x][y];
            labelNeighbors(x - 1, y + 1);
        }
        if (y < height - 1 && labels[x][y + 1] == 0 && distance[x][y + 1] > 0) {
            labels[x][y + 1] = labels[x][y];
            labelNeighbors(x, y + 1);
        }
        if (x < width - 1 && y < height - 1 && labels[x + 1][y + 1] == 0 && distance[x + 1][y + 1] > 0) {
            labels[x + 1][y + 1] = labels[x][y];
            labelNeighbors(x + 1, y + 1);
        }
    }

    private void computeMoments() {
        double[][] moments = new double[numLabels][7];

        // Initialize the moments matrix
        for (int i = 0; i < numLabels; i++) {
            for (int j = 0; j < 7; j++) {
                moments[i][j] = 0;
            }
        }

        // Calculate moments for each segment
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int label = labels[x][y];
                if (label > 0) {
                    moments[label - 1][0]++; // M00 moment
                    moments[label - 1][1] += x; // M10 moment
                    moments[label - 1][2] += y; // M01 moment
                    moments[label - 1][3] += x * x; // M20 moment
                    moments[label - 1][4] += x * y; // M11 moment
                    moments[label - 1][5] += y * y; // M02 moment
                    moments[label - 1][6] += x * x * x + y * y * y; // M30+M03 moment
                }
            }
        }

        // Normalize the moments
        for (int i = 0; i < numLabels; i++) {
            moments[i][1] /= moments[i][0];
            moments[i][2] /= moments[i][0];
            moments[i][3] -= moments[i][0] * moments[i][1] * moments[i][1];
            moments[i][4] -= moments[i][0] * moments[i][1] * moments[i][2];
            moments[i][5] -= moments[i][0] * moments[i][2] * moments[i][2];
            moments[i][6] -= 3 * moments[i][0] * moments[i][1] * (moments[i][3] + moments[i][5]);
        }

        // Print the moments
        for (int i = 0; i < numLabels; i++) {
            System.out.println("Segment " + (i + 1) + " moments:");
            System.out.println("M00 moment: " + moments[i][0]);
            System.out.println("M10 moment: " + moments[i][1]);
            System.out.println("M01 moment: " + moments[i][2]);
            System.out.println("M20 moment: " + moments[i][3]);
            System.out.println("M11 moment: " + moments[i][4]);
            System.out.println("M02 moment: " + moments[i][5]);
            System.out.println("M30+M03 moment: " + moments[i][6]);
            System.out.println();
        }
    }

    private void createSegmentedImage() {
        // Create a new image for the segmented image
        segmentedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Assign a random color to each segment
        Color[] colors = new Color[numLabels];
        for (int i = 0; i < numLabels; i++) {
            colors[i] = new Color((int) (Math.random() * 0x1000000)); // Randomly generated color
        }
        // Set the color for each pixel in the segmented image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int label = labels[x][y];
                if (label > 0) {
                    segmentedImage.setRGB(x, y, colors[label - 1].getRGB());
                } else {
                    segmentedImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }
}