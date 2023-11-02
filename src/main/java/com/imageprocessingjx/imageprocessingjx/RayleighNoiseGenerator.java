package com.imageprocessingjx.imageprocessingjx;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class RayleighNoiseGenerator {

    public static Image generateNoise(Image image) {
        // Convert to JavaFX image
        Image fxImage = image;

        // Apply Rayleigh noise
        Image noisyImage = applyRayleighNoise(fxImage);
        return noisyImage;
    }

    /**
     * Applies Rayleigh noise to the given image
     * @param image The original image
     * @return The image with added Rayleigh noise
     */
    private static WritableImage applyRayleighNoise(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Get pixel data from the original image
        int[] pixels = new int[width * height];
        image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), pixels, 0, width);

        // Add Rayleigh noise to each pixel
        for (int i = 0; i < pixels.length; i++) {
            int red = (pixels[i] >> 16) & 0xff;
            int green = (pixels[i] >> 8) & 0xff;
            int blue = pixels[i] & 0xff;

            // Calculate Rayleigh noise for each color channel
            red = (int) (red + generateRayleighNoise());
            green = (int) (green + generateRayleighNoise());
            blue = (int) (blue + generateRayleighNoise());

            // Make sure the values are in the range of 0-255
            red = Math.max(0, Math.min(255, red));
            green = Math.max(0, Math.min(255, green));
            blue = Math.max(0, Math.min(255, blue));

            // Combine the colors back into a single pixel value
            pixels[i] = (255 << 24) | (red << 16) | (green << 8) | blue;
        }

        // Create a new image with the noisy pixels
        WritableImage noisyImage = new WritableImage(width, height);
        noisyImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), pixels, 0, width);

        return noisyImage;
    }

    /**
     * Generates a random value using the Rayleigh distribution
     * @return A random value
     */
    private static double generateRayleighNoise() {
        double u1 = Math.random();
        double u2 = Math.random();

        double x = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);

        // Scale the value to fit in the range of 0-255
        return (x + 3) * (255 / 6);
    }
}