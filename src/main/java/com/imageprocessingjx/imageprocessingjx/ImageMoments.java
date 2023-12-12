package com.imageprocessingjx.imageprocessingjx;

import java.awt.image.BufferedImage;

public class ImageMoments {

    private double area;
    private double centroidX;
    private double centroidY;
    private double centralMomentXX;
    private double centralMomentXY;
    private double centralMomentYY;
    private double normalizedCentralMomentXX;
    private double normalizedCentralMomentXY;
    private double normalizedCentralMomentYY;

    public ImageMoments(BufferedImage image) {
        double[][] dimage = convertToDouble(image);
        calculateMoments(dimage);
    }

    // метод для вычисления моментов на изображении
    private void calculateMoments(double[][] image) {
        int height = image.length;
        int width = image[0].length;

        // вычисление нормализованной площади
        area = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (image[i][j] == 1) {
                    area++;
                }
            }
        }
        area /= (height * width);

        // вычисление центроида по X и Y
        centroidX = 0;
        centroidY = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (image[i][j] == 1) {
                    centroidX += j;
                    centroidY += i;
                }
            }
        }
        centroidX /= area * (height * width);
        centroidY /= area * (height * width);

        // вычисление центральных моментов XX, XY, YY
        centralMomentXX = 0;
        centralMomentXY = 0;
        centralMomentYY = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (image[i][j] == 1) {
                    centralMomentXX += (j - centroidX) * (j - centroidX);
                    centralMomentXY += (j - centroidX) * (i - centroidY);
                    centralMomentYY += (i - centroidY) * (i - centroidY);
                }
            }
        }

        // вычисление нормализованных центральных моментов XX, XY, YY
        normalizedCentralMomentXX = centralMomentXX / Math.pow(area, 2);
        normalizedCentralMomentXY = centralMomentXY / Math.pow(area, 2);
        normalizedCentralMomentYY = centralMomentYY / Math.pow(area, 2);
    }

    // геттеры для получения значений моментов
    public double getArea() {
        return area;
    }

    public double getCentroidX() {
        return centroidX;
    }

    public double getCentroidY() {
        return centroidY;
    }

    public double getCentralMomentXX() {
        return centralMomentXX;
    }

    public double getCentralMomentXY() {
        return centralMomentXY;
    }

    public double getCentralMomentYY() {
        return centralMomentYY;
    }

    public double getNormalizedCentralMomentXX() {
        return normalizedCentralMomentXX;
    }

    public double getNormalizedCentralMomentXY() {
        return normalizedCentralMomentXY;
    }

    public double getNormalizedCentralMomentYY() {
        return normalizedCentralMomentYY;
    }

    public static double[][] convertToDouble(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        // создание двумерного массива для хранения значений пикселей
        double[][] result = new double[height][width];

        // перебор всех пикселей изображения
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // получение значения пикселя в формате ARGB
                int pixel = image.getRGB(j, i);

                // извлечение красного, зеленого и синего компонентов
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                // перевод значений в оттенки серого
                double gray = ((red + green + blue) / 255.0) - 2;

                // сохранение значения в двумерный массив
                result[i][j] = gray;
            }
        }

        return result;
    }
}