package GUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import ClientPac.Client;
import ClientPac.ServerAnswThread;
import ParkingPac.Parking;
import ParkingPac.ParkingCommand;
import ParkingPac.Place;
import ParkingPac.SearchCommand;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import static javafx.scene.paint.Color.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Александр
 */
public class ViewController implements Initializable {

    /**
     * @return the parking
     */
    

    @FXML
    private Rectangle AreaA;
    @FXML
    private Rectangle AreaB;
    @FXML
    private Rectangle AreaC;
    @FXML
    private Rectangle topPoint;
    @FXML
    private Rectangle botPoint;
    @FXML
    private Button bigCar;
    @FXML
    private Button smallCar;
    @FXML
    private Label placeScreen;

    @FXML
    private AnchorPane viewPane;
    @FXML
    private AnchorPane controlPane;
    @FXML
    private SplitPane mainPane;

    private Parking parking;
    private ObjectOutputStream out;
    //private String entryPoint;
    //private List<Rectangle> placeBoxes;
    private ObservableList<Pane> placeBoxes = FXCollections.observableArrayList();
    private Thread serverAnswThread;
    private String entryPoint;
    private static ViewController instance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //placeBoxes = new ArrayList<>();
            //viewPanel.setDisable(true);
            
            instance = this;
            out = new ObjectOutputStream(Client.getSocket().getOutputStream());
            serverAnswThread = new ServerAnswThread(Client.getSocket(), instance);
            entryPoint = null;
            //viewPanel.setDisable(false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void showParking() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                //viewPane.getChildren().add(new Rectangle(100, 100));        
                viewPane.getChildren().removeAll(placeBoxes);
                placeBoxes.clear();                
                ArrayList<String> areas = getParking().getAreaList().get(entryPoint);

                for (String a : areas) {
                    ArrayList<Place> places = getParking().getPlaces().get(a);
                    if (places == null) {
                        continue;
                    }
                    Rectangle areaBox = null;
                    if (a.equals("A")) {
                        areaBox = AreaA;
                    } else if (a.equals("B")) {
                        areaBox = AreaB;
                    } else if (a.equals("C")) {
                        areaBox = AreaC;
                    }
                    double areaPositionX = areaBox.getLayoutX()+20;
                    double areaPositionY = areaBox.getLayoutY()+20;
                    for (Place p : places) {
                        Pane carBox = new CarBox(instance,p,areaPositionX , areaPositionY);
                        //Rectangle box = new Rectangle(20 + areaPositionX + (25 * position), areaPositionY + 20, p.getPlaceSize() * 20, 80);
                        
                        placeBoxes.add(carBox);
                    }

                }
                viewPane.getChildren().addAll(placeBoxes);
               
            }
        });
    }

    @FXML
    private void selectEntryPoint(MouseEvent event) {
        if (event.getTarget().equals(topPoint)) {
            entryPoint = "top";
            botPoint.setVisible(false);
            topPoint.setDisable(true);
        } else if (event.getTarget().equals(botPoint)) {
            entryPoint = "bot";
            topPoint.setVisible(false);
            botPoint.setDisable(true);
        }
        showParking();

    }

    @FXML
    private void newCar(ActionEvent event) throws IOException {
        int carSize = 0;
        if (event.getTarget().equals(bigCar)) {
            carSize = 3;
        } else if (event.getTarget().equals(smallCar)) {
            carSize = 2;
        }
        ParkingCommand search = new SearchCommand(carSize, entryPoint, getParking());
        getOut().writeObject(search);
        getOut().flush();
    }

    public void setParking(Parking parking) {
        this.parking = parking;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    /**
     * @return the placeScreen
     */
    public Label getPlaceScreen() {
        return placeScreen;
    }

    /**
     * @param placeScreen the placeScreen to set
     */
    public void setPlaceScreen(Label placeScreen) {
        this.placeScreen = placeScreen;
    }
    public Parking getParking() {
        return parking;
    }

    /**
     * @return the out
     */
    public ObjectOutputStream getOut() {
        return out;
    }
}
