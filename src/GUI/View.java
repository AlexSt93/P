package GUI;

import java.net.Socket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Александр
 */
public class View extends Application {

    private static View instance;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Resources/View.fxml"));
        primaryStage.setTitle("Parking");
        primaryStage.setScene(new Scene(root, primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.show();
    }

    public static View getInstance() {
        if (instance == null) {
            instance = new View();
        }
        return instance;
    }
}
