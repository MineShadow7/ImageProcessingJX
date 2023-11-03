package com.imageprocessingjx.imageprocessingjx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import static com.almasb.fxgl.texture.ImagesKt.toBufferedImage;

public class GaussianFilter {
    public static Image applyFilter(Image image1) {
        // Загрузка изображения
        BufferedImage image = null;
        try {
            image = toBufferedImage(image1);
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке изображения");
        }

        // Применение фильтра Гаусса для удаления шума
        assert image != null;
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Получение окрестности пикселя
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                int count = 0;
                for (int k = i - 1; k <= i + 1; k++) {
                    for (int l = j - 1; l <= j + 1; l++) {
                        if (k >= 0 && k < width && l >= 0 && l < height) {
                            // Увеличение суммы цветов и количества пикселей в окрестности
                            sumR += (image.getRGB(k, l) >> 16) & 0xFF;
                            sumG += (image.getRGB(k, l) >> 8) & 0xFF;
                            sumB += image.getRGB(k, l) & 0xFF;
                            count++;
                        }
                    }
                }
                // Вычисление среднего значения цветов
                int avgR = sumR / count;
                int avgG = sumG / count;
                int avgB = sumB / count;
                // Установка нового цвета пикселя
                filteredImage.setRGB(i, j, (avgR << 16) | (avgG << 8) | avgB);
            }
        }
        // Сохранение профильтрованного изображения
        return SwingFXUtils.toFXImage(filteredImage, null);
    }
}