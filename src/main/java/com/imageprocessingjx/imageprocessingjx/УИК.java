package com.imageprocessingjx.imageprocessingjx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class УИК {
    public static double calculateUQ(Image image1, Image image2) throws Exception {
        double uq = 0;
        int width = (int) image1.getWidth();
        int height = (int) image1.getHeight();
        int imageSize = width * height;
        int imageSize2 = (int) (image2.getHeight() * image2.getWidth());
        double Gxy = 0, Gx = 0, Gy = 0, sigmaX = 0, sigmaY = 0;
        int pixelIntensity1, pixelIntensity2;
        int[] pixelColor1;
        int[] pixelColor2;

        if(imageSize2 != imageSize){
            throw new Exception();
        }
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                pixelColor1 = GetPixelColor(image1, i, j);
                pixelColor2 = GetPixelColor(image2, i, j);
                pixelIntensity1 = Clamp((int)(0.36 * pixelColor1[0]) + (int)(0.53 * pixelColor1[1]) + (int)(0.11 * pixelColor1[2]), 0, 255);
                pixelIntensity2 = Clamp((int)(0.36 * pixelColor2[0]) + (int)(0.53 * pixelColor2[1]) + (int)(0.11 * pixelColor2[2]), 0, 255);
                sigmaX += pixelIntensity1;
                sigmaY += pixelIntensity2;
            }
        }
        sigmaX = sigmaX / imageSize;
        sigmaY = sigmaY / imageSize;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                pixelColor1 = GetPixelColor(image1, i, j);
                pixelColor2 = GetPixelColor(image2, i, j);
                pixelIntensity1 = Clamp((int)(0.36 * pixelColor1[0]) + (int)(0.53 * pixelColor1[1]) + (int)(0.11 * pixelColor1[2]), 0, 255);
                pixelIntensity2 = Clamp((int)(0.36 * pixelColor2[0]) + (int)(0.53 * pixelColor2[1]) + (int)(0.11 * pixelColor2[2]), 0, 255);
                Gx += (pixelIntensity1 - sigmaX) * (pixelIntensity1 - sigmaX);
                Gy += (pixelIntensity2 - sigmaY) * (pixelIntensity2 - sigmaY);
                Gxy += (pixelIntensity1 - sigmaX) * (pixelIntensity2 - sigmaY);
            }
        }
        Gx = Gx / imageSize;
        Gy = Gy / imageSize;
        Gxy = Gxy / imageSize;
        float res1 = (float) ((2 * sigmaX * sigmaY) / ((sigmaX * sigmaX) + (sigmaY * sigmaY)));
        float res2 = (float) ((2 * Gxy * Gxy) / (((Gx * Gx) + (Gy * Gy))));
        uq = (int) (res1 * res2);
        return uq;
    }
    protected static int[] GetPixelColor(Image image, int x, int y) {
            // Getting pixel color by position x and y
        PixelReader pixelReader = image.getPixelReader();
            int clr = pixelReader.getArgb(x, y);
            int red =   (clr & 0x00ff0000) >> 16;
            int green = (clr & 0x0000ff00) >> 8;
            int blue =   clr & 0x000000ff;
        return new int[]{red, green ,blue};
        }

    protected static int Clamp(int value, int min, int max){
        if(value < min){
            return min;
        }
        if(value > max){
            return max;
        }
        return value;
    }
}

