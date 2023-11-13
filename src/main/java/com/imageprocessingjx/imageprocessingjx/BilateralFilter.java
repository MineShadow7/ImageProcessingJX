package com.imageprocessingjx.imageprocessingjx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BilateralFilter {

    public static Image bilateralFilter(Image image) {
        BufferedImage img = SwingFXUtils.fromFXImage(image, null);
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Image resultImg;

        int[][] red = new int[width][height];
        int[][] green = new int[width][height];
        int[][] blue = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                red[x][y] = new Color(img.getRGB(x, y)).getRed();
                green[x][y] = new Color(img.getRGB(x, y)).getGreen();
                blue[x][y] = new Color(img.getRGB(x, y)).getBlue();
            }
        }

        double sigmaD = 2.0; // пространственный параметр
        double sigmaR = 50.0; // радиальный параметр

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double sumR = 0;
                double sumG = 0;
                double sumB = 0;
                double wP = 0;

                for (int i = x - 2; i <= x + 2; i++) {
                    for (int j = y - 2; j <= y + 2; j++) {
                        if (i >= 0 && i < width && j >= 0 && j < height) {
                            double spatialDist = Math.sqrt((x - i) * (x - i) + (y - j) * (y - j));
                            double colorDist = Math.sqrt((red[x][y] - red[i][j]) * (red[x][y] - red[i][j])
                                    + (green[x][y] - green[i][j]) * (green[x][y] - green[i][j])
                                    + (blue[x][y] - blue[i][j]) * (blue[x][y] - blue[i][j]));

                            double w = Math.exp(-spatialDist / (2 * sigmaD * sigmaD))
                                    * Math.exp(-colorDist / (2 * sigmaR * sigmaR));

                            sumR += red[i][j] * w;
                            sumG += green[i][j] * w;
                            sumB += blue[i][j] * w;
                            wP += w;
                        }
                    }
                }

                int newRed = (int) (sumR / wP);
                int newGreen = (int) (sumG / wP);
                int newBlue = (int) (sumB / wP);

                result.setRGB(x, y, new Color(newRed, newGreen, newBlue).getRGB());
            }
        }
        resultImg = SwingFXUtils.toFXImage(result, null);
        return resultImg;
    }
}