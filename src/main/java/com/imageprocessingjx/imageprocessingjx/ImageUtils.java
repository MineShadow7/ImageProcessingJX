package com.imageprocessingjx.imageprocessingjx;

import java.awt.image.BufferedImage;

public class ImageUtils {

    /**
     * Send this method a BufferedImage to get an RGB array (int, value 0-255).
     */
    public static int[][][] RGBArray(BufferedImage img) {
        int[][][] rgb = null;
        int height = img.getHeight();
        int width = img.getWidth();

        if (height > 0 && width > 0) {
            rgb = new int[height][width][3];

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    rgb[row][column] = intRGB(img.getRGB(column, row));
                }
            }
        }

        return rgb;
    }

    /**
     * Send this method an array of RGB pixels (int) to get a BufferedImage.
     */
    public static BufferedImage RGBImg(int[][][] raw) {
        BufferedImage img = null;
        int height = raw.length;
        int width = raw[0].length;

        if (height > 0 && width > 0 || raw[0][0].length == 3) {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    img.setRGB(column, row, (raw[row][column][0] << 16) | (raw[row][column][1] << 8) | (raw[row][column][2]));
                }
            }
        }

        return img;
    }

    /**
     * Send this method a BufferedImage to get a grayscale array (int, value 0-255.)
     */
    public static int[][] GSArray(BufferedImage img) {
        int[][] gs = null;
        int height = img.getHeight();
        int width = img.getWidth();

        if (height > 0 && width > 0) {
            gs = new int[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int bits = img.getRGB(j, i);
                    //Not sure if precision is needed, but adding for now
                    long avg = Math.round((((bits >> 16) & 0xff) + ((bits >> 8) & 0xff) + (bits & 0xff)) / 3.0);
                    gs[i][j] = (int) avg;
                }
            }
        }

        return gs;
    }

    /**
     * Send this method an array of grayscale pixels (int) to get a BufferedImage
     */
    public static BufferedImage GSImg(int[][] raw) {
        BufferedImage img = null;
        int height = raw.length;
        int width = raw[0].length;

        if (height > 0 && width > 0) {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    img.setRGB(j, i, (raw[i][j] << 16) | (raw[i][j] << 8) | (raw[i][j]));
                }
            }
        }

        return img;
    }

    public static BufferedImage HSVImg(double[][][] raw) {
        int height = raw.length;
        int width = raw[0].length;

        if (height < 1 || width < 1 || raw[0][0].length != 3) {
            throw new IllegalArgumentException("ERROR: Malformed RGB array!");
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int[] rgb = new int [3];

                rgb[0] = (int) raw[r][c][0] * 255;
                rgb[1] = (int) raw[r][c][1] * 255;
                rgb[2] = (int) ((raw[r][c][2] / 360) * raw[r][c][2]);

                img.setRGB(c, r, (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2]));
            }
        }

        return img;
    }

    /**
     * Send this method a 32-bit pixel value from BufferedImage to get the RGB
     * @param bits  int, 32-bit BufferedImage pixel value
     * @return rgb  int[], RGB values extracted from pixel
     */
    private static int[] intRGB(int bits) {
        int[] rgb = { (bits >> 16) & 0xff, (bits >> 8) & 0xff, bits & 0xff };

        //Don't propagate bad pixel values
        for (int i = 0; i < 3; i++) {
            if (rgb[i] < 0) {
                rgb[i] = 0;
            } else if (rgb[i] > 255) {
                rgb[i] = 255;
            }
        }

        return rgb;
    }

    private static double Min(int a, int b, int c) {
        if (a <= b && a <= c) {
            return (double) a;
        } else if (b <= c && b <= a) {
            return (double) b;
        } else {
            return (double) c;
        }
    }
}
