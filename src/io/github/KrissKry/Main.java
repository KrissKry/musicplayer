package io.github.KrissKry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {

        MusicLister.setStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("MPlayer_UI.fxml"));
        //FXMLLoader uiLoader = new FXMLLoader();
        //uiLoader.setLocation(this.getClass().getResource("MPlayer_UI.fxml"));
        Scene scene = new Scene(root);

//        primaryStage.initStyle(StageStyle.TRANSPARENT);
//
//        root.setOnMousePressed(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                xOffset = event.getSceneX();
//                yOffset = event.getSceneY();
//            }
//        });
//
//        //move around here
//        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                primaryStage.setX(event.getScreenX() - xOffset);
//                primaryStage.setY(event.getScreenY() - yOffset);
//            }
//        });
        primaryStage.setScene(scene);
        primaryStage.setTitle("Music Player");

        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
