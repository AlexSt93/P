package GUI;

import ClientPac.Client;

import ParkingPac.LeaveCommand;
import ParkingPac.ParkingCommand;
import ParkingPac.Place;
import java.awt.Point;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
public class CarBox extends Pane {

    private Label placeName;
    private Button delBut;
    private Place place;
    private ViewController vCntrl;

    public CarBox(final ViewController vCntrl, final Place place, double X, double Y) {
        this.place = place;
        this.placeName = new Label(place.getAreaName() + place.getPlacePosition());
        this.vCntrl = vCntrl;
        String borderStyle = "";
        this.setLayoutX(X + place.getPlacePosition() * 25);
        this.setLayoutY(Y);
        this.setPrefSize(place.getPlaceSize() * 20, 80);
        if (place.getStatus() == 0) {
            borderStyle = "-fx-border-style: dotted;"
                    + "-fx-border-width: 1;";
        } else if (place.getStatus() == 1) {
            borderStyle = "-fx-border-style: solid;"
                    + "-fx-border-width: 1;";
            this.delBut = new Button("X");
            this.delBut.setLayoutX(this.getPrefWidth() / 2 - 12);
            this.delBut.setLayoutY(50);
            delBut.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        ParkingCommand leave = new LeaveCommand(place, vCntrl.getParking());
                        vCntrl.getOut().writeObject(leave);
                        vCntrl.getOut().flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            });
            delBut.setVisible(false);

            this.addEventHandler(MouseEvent.MOUSE_ENTERED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            delBut.setVisible(true);
                        }
                    });
            this.addEventHandler(MouseEvent.MOUSE_EXITED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            delBut.setVisible(false);
                        }
                    });

            this.getChildren().add(this.delBut);
        }
        this.setStyle(borderStyle);

        this.placeName.setLayoutX(this.getPrefWidth() / 2 - 10);

        this.placeName.setLayoutY(10);

        this.getChildren().add(this.placeName);

    }
}
