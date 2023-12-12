package com.imageprocessingjx.imageprocessingjx;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.awt.image.BufferedImage;
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
    public TextArea textField;
    private List<String> imagesList = new ArrayList<>();
    private List<String> noisesList = new ArrayList<>();
    public static String imagePath;
    public ImageView ImageView1;
    public ImageView ImageView2;
    public ComboBox<String> comboBox;
    @FXML
    private Label welcomeText;

    //Canny Edge Detection Variables
    private static final double CANNY_THRESHOLD_RATIO = .2; //Suggested range .2 - .4
    private static final int CANNY_STD_DEV = 1;             //Range 1-3


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
        noisesList.add("Фильтр Билатериальный");
        noisesList.add("Выделение границ Canny");
        noisesList.add("Моменты форм объектов изображения");
        noisesList.add("Сегментация Distance Transform и Моменты сегментов");
        noisesList.add("MSE");
        noisesList.add("УИК");

        textField.setVisible(false);
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
            case "Фильтр Билатериальный":
                SecondImage = BilateralFilter.bilateralFilter(SecondImage);
                ImageView2.setImage(SecondImage);
                break;
            case "Выделение границ Canny":
                //Sample JCanny usage
                try {
                    BufferedImage input = SwingFXUtils.fromFXImage(ImageView2.getImage(), null);
                    CannyEdgeDetection JCanny = new CannyEdgeDetection();
                    BufferedImage output = JCanny.CannyEdges(input, CANNY_STD_DEV, CANNY_THRESHOLD_RATIO);
                    ImageView2.setImage(SwingFXUtils.toFXImage(output, null));
                } catch (Exception ex) {
                    System.out.println("ERROR ACCESING IMAGE:\n" + ex.getMessage());
                }
                break;
            case "Моменты форм объектов изображения":
                BufferedImage image = SwingFXUtils.fromFXImage(ImageView2.getImage(), null);
                ImageMoments moments = new ImageMoments(image);
                textField.setVisible(true);
                textField.setText("CentralMomentXX: " + String.valueOf(moments.getCentralMomentXX()) + "\r\n" + "CentralMomentXY: " +  String.valueOf(moments.getCentralMomentXY() + "\r\n" +
                        "CentralMomentYY: " + String.valueOf(moments.getCentralMomentYY()) + "\r\n" + "NormalizedCentralMomentXX: " + String.valueOf(moments.getNormalizedCentralMomentXX()) + "\r\n" +
                        "NormalizedCentralMomentXY: " + String.valueOf(moments.getNormalizedCentralMomentXY()) + "\r\n" + "CentralMomentYY: " + String.valueOf(moments.getNormalizedCentralMomentYY()) + "\r\n" +
                        "Area: " + String.valueOf(moments.getArea()) + "\r\n" + "CentroidX: " + String.valueOf(moments.getCentroidX()) + "\r\n" +"CentroidY: " + String.valueOf(moments.getCentroidY())));
                break;
            case "Сегментация Distance Transform и Моменты сегментов":
                BufferedImage toSegmentImg = SwingFXUtils.fromFXImage(ImageView2.getImage(), null);
                BufferedImage resultImg;
                ImageSegmentation segmentation = new ImageSegmentation();
                resultImg = segmentation.segmentImage(toSegmentImg);
                ImageView2.setImage(SwingFXUtils.toFXImage(resultImg, null));
                textField.setVisible(true);
                textField.setText(segmentation.getText());
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
        histogramChart.getData().clear();
        anchorPaneObj.getChildren().removeAll(histogramChart);
    }

    public void removeMoments(ActionEvent actionEvent) {
        textField.clear();
        textField.setVisible(false);
    }
}