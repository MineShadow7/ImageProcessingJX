package com.imageprocessingjx.imageprocessingjx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.imageprocessingjx.imageprocessingjx.MSE.calculateMSE;

public class HelloController {
    public ComboBox NoisesComboBox;
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
        // устанавливаем список в ComboBox
        comboBox.setItems(FXCollections.observableArrayList(imagesList));

        noisesList.add("Шум Райли");
        noisesList.add("MSE");
        noisesList.add("УИК");

        NoisesComboBox.setItems(FXCollections.observableArrayList(noisesList));
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
        }
    }

    public void createImageChoices(ActionEvent actionEvent) {
        String selectedImage = comboBox.getValue(); // получаем выбранное значение из ComboBox
        imagePath = "Assets/" + selectedImage + ".jpg"; // формируем путь к изображению
    }


    public void createNoisesComboBoxOptions(ActionEvent actionEvent) {
        // Не используется пока что
    }

    public void onNoiseBtnClick(ActionEvent actionEvent) throws Exception {
        String selecteItem = (String) NoisesComboBox.getValue();
        Image FirstImage = ImageView1.getImage();
        Image SecondImage = ImageView2.getImage();
        switch (selecteItem){
            case "Шум Райли":
                SecondImage = RayleighNoiseGenerator.generateNoise(SecondImage);
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
}