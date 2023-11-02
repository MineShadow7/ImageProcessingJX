package com.imageprocessingjx.imageprocessingjx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.paint.Color;

import static com.imageprocessingjx.imageprocessingjx.УИК.Clamp;
import static com.imageprocessingjx.imageprocessingjx.УИК.GetPixelColor;

public class MSE {
    public static double calculateMSE(Image image1, Image image2) {
        double MSE = 0;
        int width = (int) image1.getWidth();
        int height = (int) image1.getHeight();
        int imageSize = width * height;
        int pixelIntensity1, pixelIntensity2;
        int[] pixelColor1, pixelColor2;
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                pixelColor1 = GetPixelColor(image1, i, j);
                pixelColor2 = GetPixelColor(image2, i, j);
                pixelIntensity1 = Clamp((int)(0.36 * pixelColor1[0]) + (int)(0.53 * pixelColor1[1]) + (int)(0.11 * pixelColor1[2]), 0, 255);
                pixelIntensity2 = Clamp((int)(0.36 * pixelColor2[0]) + (int)(0.53 * pixelColor2[1]) + (int)(0.11 * pixelColor2[2]), 0, 255);
                MSE += (pixelIntensity1 - pixelIntensity2) * (pixelIntensity1 - pixelIntensity2);
            }
        }
        MSE = MSE / imageSize;

        return MSE;
    }
}
