package com.imageprocessingjx.imageprocessingjx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.imageprocessingjx.imageprocessingjx.MSE.calculateMSE;

public class UIController {
    public ComboBox NoisesComboBox;
    public AnchorPane anchorPaneObj;
    static NumberAxis xAxis = new NumberAxis();
    static NumberAxis yAxis = new NumberAxis();
    public static LineChart<Number, Number> histogramChart = new LineChart<>(xAxis, yAxis);;
    private List<String> imagesList = new ArrayList<>();
    private List<String> noisesList = new ArrayList<>();
    public static String imagePath;
    public ImageView ImageView1;
    public ImageView ImageView2;
    public ComboBox<String> comboBox;
    @FXML
    private Label welcomeText;


    public void initialize() {
        // получаем путь к папке с изображениями
        String imagesPath = "Assets/";
        // создаем объект File для этой папки
        File folder = new File(imagesPath);
        // получаем список файлов в этой папке
        File[] files = folder.listFiles();
        // проходим по списку файлов и добавляем их названия в список
        for (File file : files) {
            imagesList.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
        }
        imagesList.sort(null);
        // устанавливаем список в ComboBox
        comboBox.setItems(FXCollections.observableArrayList(imagesList));

        noisesList.add("Шум Райли");
        noisesList.add("Фильтр Гаусса");
        noisesList.add("Фильтр Билатериальный");
        noisesList.add("MSE");
        noisesList.add("УИК");

        NoisesComboBox.setItems(FXCollections.observableArrayList(noisesList));
    }

    public void showHistogram(){
        xAxis.setLabel("Pixel Value");
        yAxis.setLabel("Frequency");
        // Create a XYChart.Series for the histogram data
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Histogram");

        // Assume the noisyImage is the input noisy image
        Image noisyImage = ImageView2.getImage(); // Load the noisy image
        // Calculate the histogram of the noisy image
        int[] histogram = calculateHistogram(noisyImage);

        // Add the histogram data to the series
        for (int i = 0; i < histogram.length; i++) {
            series.getData().add(new XYChart.Data<>(i, histogram[i]));
        }

        // Add the series to the lineChart
        histogramChart.setLayoutX(510);
        histogramChart.setLayoutY(370);
        histogramChart.setPrefWidth(345);
        histogramChart.setPrefHeight(241);
        histogramChart.setVisible(true);
        histogramChart.getData().add(series);
        anchorPaneObj.getChildren().removeAll(xAxis);
        anchorPaneObj.getChildren().removeAll(yAxis);
        anchorPaneObj.getChildren().removeAll(histogramChart);
        anchorPaneObj.getChildren().add(histogramChart);
    }

    private int[] calculateHistogram(Image image) {
        int[] histogram = new int[256]; // Assuming 8-bit grayscale image

        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Iterate through each pixel in the image and update the histogram
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = (int) (pixelReader.getColor(x, y).getBrightness() * 255); // Convert color to grayscale value
                histogram[pixelValue]++;
            }
        }

        return histogram;
    }

    @FXML
    protected void onExitButtonClick() {
        ApplicationClass.getMystage().close();
    }

    public void onImgBtn1Click(ActionEvent actionEvent) {
        if(imagePath != null) {
            ImageView1.setImage(new Image(new File(imagePath).toURI().toString())); // устанавливаем изображение в ImageView1
        }
    }

    public void onImgBtn2Click(ActionEvent actionEvent) {
        if(imagePath != null) {
            ImageView2.setImage(new Image(new File(imagePath).toURI().toString())); // устанавливаем изображение в ImageView2
        }
    }

    public void createImageChoices(ActionEvent actionEvent) {
        String selectedImage = comboBox.getValue(); // получаем выбранное значение из ComboBox
        imagePath = "Assets/" + selectedImage + ".jpg"; // формируем путь к изображению
    }


    public void createNoisesComboBoxOptions(ActionEvent actionEvent) {
        // Не используется
    }

    public void onNoiseBtnClick(ActionEvent actionEvent) throws Exception {
        String selecteItem = (String) NoisesComboBox.getValue();
        Image FirstImage = ImageView1.getImage();
        Image SecondImage = ImageView2.getImage();
        switch (selecteItem){
            case "Шум Райли":
                ImageView2.setImage(RayleighNoise.generateNoise(ImageView2.getImage()));
                break;
            case "Фильтр Гаусса":
                SecondImage = GaussianFilter.applyFilter(SecondImage);
                ImageView2.setImage(SecondImage);
                break;
            case "Фильтр Билатериальный":
                SecondImage = BilateralFilter.bilateralFilter(SecondImage);
                ImageView2.setImage(SecondImage);
                break;
            case "MSE":
                double MSE;
                MSE = calculateMSE(FirstImage, SecondImage);
                Alert alertMSE = new Alert(Alert.AlertType.INFORMATION);
                alertMSE.setTitle("MSE");
                alertMSE.setHeaderText("Значение MSE для изображений");
                alertMSE.setContentText(String.valueOf(MSE));
                alertMSE.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
                break;
            case "УИК":
                double UIQ;
                UIQ = УИК.calculateUQ(FirstImage, SecondImage);
                Alert alertUIQ = new Alert(Alert.AlertType.INFORMATION);
                alertUIQ.setTitle("Универсальный Индекс Качества");
                alertUIQ.setHeaderText("Универсальный Индекс Качества");
                alertUIQ.setContentText(String.valueOf(UIQ));
                alertUIQ.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
                break;
        }
    }

    public void onImgBtn3Click(ActionEvent actionEvent) {
        if(imagePath != null) {
            ImageView1.setImage(new Image(new File(imagePath).toURI().toString())); // устанавливаем изображение в ImageView1
            ImageView2.setImage(new Image(new File(imagePath).toURI().toString())); // устанавливаем изображение в ImageView2
        }
    }

    public void onHistogramBtnClick(ActionEvent actionEvent) {
        showHistogram();
    }

    public void refreshHistogram(MouseEvent mouseEvent) {
        histogramChart.setVisible(false);

    }

    public void onHideHistogramClick(ActionEvent actionEvent) {
        anchorPaneObj.getChildren().removeAll(histogramChart);
    }
}