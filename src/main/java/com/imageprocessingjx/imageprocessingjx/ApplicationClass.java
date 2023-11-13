package com.imageprocessingjx.imageprocessingjx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationClass extends Application {
    static Stage mystage;
    Scene myscene;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationClass.class.getResource("UIview.fxml"));
        myscene = new Scene(fxmlLoader.load());
        stage.setTitle("Image Processing Application");
        stage.setScene(myscene);
        mystage = stage;
        mystage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    public static Stage getMystage(){
        return mystage;
    }
}