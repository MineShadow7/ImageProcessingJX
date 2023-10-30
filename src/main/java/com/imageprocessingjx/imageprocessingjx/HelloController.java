package com.imageprocessingjx.imageprocessingjx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController {
    private List<String> imagesList = new ArrayList<>();
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
}