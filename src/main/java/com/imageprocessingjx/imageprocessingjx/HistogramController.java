package com.imageprocessingjx.imageprocessingjx;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HistogramController {

    static Image image = HelloController.publicImage;


    public static void start(Stage primaryStage) {
        Button btnDo = new Button("Do Histogram");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String, Number> chartHistogram
                = new LineChart<>(xAxis, yAxis);
        chartHistogram.setCreateSymbols(false);

        btnDo.setOnAction((ActionEvent event) -> {
            chartHistogram.getData().clear();
            Histogram imageHistogram = new Histogram(image);
            if (imageHistogram.isSuccess()) {
                chartHistogram.getData().addAll(
                        //imageHistogram.getSeriesAlpha(),
                        imageHistogram.getSeriesRed(),
                        imageHistogram.getSeriesGreen(),
                        imageHistogram.getSeriesBlue());
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(chartHistogram);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(btnDo, hBox);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 1000, 500);

        primaryStage.setTitle("Гистограмма Изображения");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}