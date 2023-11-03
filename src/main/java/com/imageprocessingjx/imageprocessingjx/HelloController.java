package com.imageprocessingjx.imageprocessingjx;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.imageprocessingjx.imageprocessingjx.MSE.calculateMSE;

public class HelloController {
    public ComboBox NoisesComboBox;
    public AnchorPane anchorPaneObj;
    private List<String> imagesList = new ArrayList<>();
    private List<String> noisesList = new ArrayList<>();
    public static String imagePath;
    public ImageView ImageView1;
    public ImageView ImageView2;
    public static Image publicImage;
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
        noisesList.add("MSE");
        noisesList.add("УИК");

        NoisesComboBox.setItems(FXCollections.observableArrayList(noisesList));
    }

    public void initLineChart() {
        HistogramController controller = new HistogramController();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("histogram.fxml"));
        Stage hStage = new Stage();
        hStage.setTitle("Гистограмма изображения");
        try {
            hStage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hStage.show();
        HistogramController.start(hStage);
    }

    @FXML
    protected void onExitButtonClick() {
        HelloApplication.getMystage().close();
    }

    public void onImgBtn1Click(ActionEvent actionEvent) {
        if(imagePath != null) {
            ImageView1.setImage(new Image(new File(imagePath).toURI().toString())); // устанавливаем изображение в ImageView1
        }
    }

    public void onImgBtn2Click(ActionEvent actionEvent) {
        if(imagePath != null) {
            ImageView2.setImage(new Image(new File(imagePath).toURI().toString())); // устанавливаем изображение в ImageView2
            publicImage = ImageView2.getImage();
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
                SecondImage = RayleighNoiseGenerator.generateNoise(SecondImage);
                ImageView2.setImage(SecondImage);
                publicImage = ImageView2.getImage();
                break;
            case "Фильтр Гаусса":
                SecondImage = GaussianFilter.applyFilter(SecondImage);
                ImageView2.setImage(SecondImage);
                publicImage = ImageView2.getImage();
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
        initLineChart();
    }
}